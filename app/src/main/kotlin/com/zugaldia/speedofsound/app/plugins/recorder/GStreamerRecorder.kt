package com.zugaldia.speedofsound.app.plugins.recorder

import com.zugaldia.speedofsound.app.ENV_DISABLE_GSTREAMER
import com.zugaldia.speedofsound.core.audio.AudioInputDevice
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderEvent
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderOptions
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderPlugin
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderResponse
import org.freedesktop.gstreamer.app.AppSink
import org.freedesktop.gstreamer.gst.Bus
import org.freedesktop.gstreamer.gst.Caps
import org.freedesktop.gstreamer.gst.Device
import org.freedesktop.gstreamer.gst.DeviceMonitor
import org.freedesktop.gstreamer.gst.ElementFactory
import org.freedesktop.gstreamer.gst.FlowReturn
import org.freedesktop.gstreamer.gst.Gst
import org.freedesktop.gstreamer.gst.MapInfo
import org.freedesktop.gstreamer.gst.Message
import org.freedesktop.gstreamer.gst.MessageType
import org.freedesktop.gstreamer.gst.Pipeline
import org.freedesktop.gstreamer.gst.State
import org.gnome.glib.Source
import org.javagi.base.Out
import java.io.ByteArrayOutputStream

/**
 * Recorder plugin that uses GStreamer for audio capture.
 *
 * On Flatpak, we request --socket=pulseaudio, that gives us access to PulseAudio. It includes sound input (mic),
 * sound output/playback, MIDI and ALSA sound devices in /dev/snd.
 * https://docs.flatpak.org/en/latest/sandbox-permissions.html#permissions-guidelines
 *
 * On Snap, we request the audio-record interface that allows an application to access audio recording hardware,
 * such as a microphone, via the system's audio service, such as PulseAudio.
 * https://snapcraft.io/docs/reference/interfaces/audio-record-interface/index.html
 *
 * For troubleshooting purposes, you can set SOS_DISABLE_GSTREAMER=true for the application to fall back
 * to a `javax.sound` implementation.
 *
 */
@Suppress("TooManyFunctions")
class GStreamerRecorder(
    options: RecorderOptions = RecorderOptions(),
) : RecorderPlugin<RecorderOptions>(initialOptions = options) {
    override val id: String = ID

    @Volatile
    private var isRecording = false

    private var pipeline: Pipeline? = null
    private var appSink: AppSink? = null
    private var audioBuffer: ByteArrayOutputStream? = null
    private val audioBufferLock = Any()
    private var busWatchId: Int = 0

    companion object {
        const val ID = "RECORDER_GSTREAMER"
        private const val BITS_PER_BYTE = 8
        private const val GST_MAP_READ = 1
    }

    @Suppress("TooGenericExceptionCaught")
    override fun enable() {
        super.enable()
        try {
            val args = arrayOf<String>()
            val version = "${Gst.VERSION_MAJOR}.${Gst.VERSION_MINOR}.${Gst.VERSION_MICRO}"
            Gst.init(Out(args))
            pipeline = buildPipeline()
            log.info("GStreamer v$version initialized.")
            if (currentOptions.enableDebug) {
                val devices = getAvailableDevices()
                devices.forEach { device ->
                    log.info(
                        "Found audio input device: id=${device.deviceId}, " +
                            "name=${device.name}, description=${device.description}"
                    )
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Failed to initialize GStreamer: ${e.message}. " +
                "Ensure GStreamer is installed, or set $ENV_DISABLE_GSTREAMER=true to use the fallback recorder."
            log.error(errorMsg, e)
            throw IllegalStateException(errorMsg, e)
        }
    }

    override fun isCurrentlyRecording(): Boolean = isRecording

    /**
     * Returns a list of all available audio input devices (microphones) in the system.
     * Only devices that support audio capture are included.
     */
    @Suppress("TooGenericExceptionCaught")
    override fun getAvailableDevices(): List<AudioInputDevice> {
        return try {
            val monitor = DeviceMonitor().apply { addFilter("Audio/Source", Caps.fromString("audio/x-raw")) }
            val started = monitor.start()
            if (!started) {
                log.error("Failed to start DeviceMonitor")
                return emptyList()
            }

            try {
                val devices = monitor.getDevices()
                log.info("Found ${devices?.size} audio input devices.")
                devices?.mapNotNull { device -> device?.let { extractDeviceInfo(it) } } ?: emptyList()
            } finally {
                monitor.stop()
            }
        } catch (e: Exception) {
            log.error("Failed to enumerate devices: ${e.message}")
            emptyList()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun extractDeviceInfo(device: Device): AudioInputDevice? {
        return try {
            val displayName = device.getDisplayName() ?: "Unknown Device"
            val deviceClass = device.getDeviceClass() ?: "Audio/Source"
            val deviceId = device.getProperties()?.getString("device.id") ?: displayName
            AudioInputDevice(deviceId = deviceId, name = displayName, description = deviceClass)
        } catch (e: Exception) {
            log.warn("Failed to extract device info: ${e.message}")
            null
        }
    }

    private fun buildCapsString(): String {
        val audioInfo = currentOptions.audioInfo
        val bitsPerSample = audioInfo.sampleWidth * BITS_PER_BYTE
        val format = "S${bitsPerSample}LE" // Signed little-endian
        return "audio/x-raw,format=$format,rate=${audioInfo.sampleRate},channels=${audioInfo.channels}"
    }

    @Suppress("TooGenericExceptionCaught")
    private fun buildPipeline(): Pipeline {
        val pipeline = Pipeline("audio-recorder")

        // Automatically selects the best available audio source (e.g., PulseAudio, ALSA)
        val source = ElementFactory.make("autoaudiosrc", "source")
        requireNotNull(source) { "Failed to create autoaudiosrc element" }

        // Converts audio between formats (e.g. float to integer samples)
        val convert = ElementFactory.make("audioconvert", "convert")
        requireNotNull(convert) { "Failed to create audioconvert element" }

        // Converts audio between different sample rates
        val resample = ElementFactory.make("audioresample", "resample")
        requireNotNull(resample) { "Failed to create audioresample element" }

        // Enforces the target audio format (sample rate, channels, bit depth)
        val capsFilter = ElementFactory.make("capsfilter", "capsfilter")
        requireNotNull(capsFilter) { "Failed to create capsfilter element" }

        // Delivers audio buffers to the application via callbacks
        val sink = ElementFactory.make("appsink", "appsink")
        requireNotNull(sink) { "Failed to create appsink element" }

        val capsString = buildCapsString()
        capsFilter.setProperty("caps", Caps.fromString(capsString))

        val typedSink = AppSink(sink.handle())
        typedSink.emitSignals = true
        typedSink.setProperty("sync", false)
        typedSink.onNewSample { handleNewSample(typedSink) }
        appSink = typedSink

        pipeline.add(source)
        pipeline.add(convert)
        pipeline.add(resample)
        pipeline.add(capsFilter)
        pipeline.add(sink)

        source.link(convert)
        convert.link(resample)
        resample.link(capsFilter)
        capsFilter.link(sink)

        // Watch the message bus
        if (currentOptions.enableDebug) {
            val bus = pipeline.getBus()
            if (bus != null) {
                busWatchId = bus.addWatch(0, ::busCall)
            }
        }

        return pipeline
    }

    @Suppress("UnusedParameter")
    private fun busCall(bus: Bus, message: Message): Boolean {
        val messageTypes = message.readType()
        when {
            messageTypes.contains(MessageType.EOS) -> {
                log.info("End of stream reached.")
            }

            messageTypes.contains(MessageType.ERROR) -> {
                val errorOut = Out<org.gnome.glib.GError>()
                val debugOut = Out<String>()
                message.parseError(errorOut, debugOut)
                log.error("GStreamer error: ${errorOut.get()?.readMessage()}")
            }

            messageTypes.contains(MessageType.WARNING) -> {
                val errorOut = Out<org.gnome.glib.GError>()
                val debugOut = Out<String>()
                message.parseWarning(errorOut, debugOut)
                log.warn("GStreamer warning: ${errorOut.get()?.readMessage()}")
            }
        }

        return true
    }

    /**
     * Starts recording audio from the default input device using GStreamer.
     * Audio is captured using the format specified in RecorderOptions.
     */
    @Suppress("TooGenericExceptionCaught")
    override fun startRecording() {
        if (isRecording) {
            log.warn("Recording is already in progress.")
            return
        }

        try {
            synchronized(audioBufferLock) { audioBuffer = ByteArrayOutputStream() }
            val currentPipeline = pipeline ?: throw IllegalStateException("Pipeline not initialized")
            currentPipeline.setState(State.PLAYING)
            isRecording = true
            log.info("GStreamer recording started.")
        } catch (e: Exception) {
            log.error("Failed to start recording: ${e.message}")
            isRecording = false
            synchronized(audioBufferLock) { audioBuffer = null }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun handleNewSample(sink: AppSink): FlowReturn = try {
        val sample = sink.pullSample()
        val buffer = sample?.getBuffer() ?: return FlowReturn.OK
        val mapInfo = MapInfo()
        if (!buffer.map(mapInfo, GST_MAP_READ)) {
            return FlowReturn.OK
        }

        try {
            val data = mapInfo.readData()
            if (data != null) {
                synchronized(audioBufferLock) { audioBuffer?.write(data) }
                if (currentOptions.computeVolumeLevel) {
                    val level = AudioManager.computeRmsLevel(data)
                    tryEmitEvent(RecorderEvent.RecordingLevel(level))
                }
            }
        } finally {
            buffer.unmap(mapInfo)
        }

        FlowReturn.OK
    } catch (e: Exception) {
        log.error("Error handling sample: ${e.message}", e)
        FlowReturn.ERROR
    }

    /**
     * Stops recording and returns the captured audio data.
     * @return Result containing RecorderResponse with the captured audio data, or an error.
     */
    override fun stopRecording(): Result<RecorderResponse> = runCatching {
        if (!isRecording) {
            throw IllegalStateException("No recording in progress.")
        }

        isRecording = false

        val currentPipeline = pipeline ?: throw IllegalStateException("Pipeline not initialized")
        currentPipeline.setState(State.NULL)
        val audioData = synchronized(audioBufferLock) {
            val data = audioBuffer?.toByteArray()
            audioBuffer = null
            data
        }

        if (audioData == null || audioData.isEmpty()) {
            throw IllegalStateException("No audio data captured.")
        }

        log.info("Recording stopped. Captured ${audioData.size} bytes.")
        RecorderResponse(audioData)
    }

    private fun cleanup() {
        // The order of operations is important. We need to set the pipeline to NULL before removing the bus watch.
        // Otherwise, we might hit a NPE race condition in org.javagi.interop.Arenas.close_cb (root cause unclear).
        pipeline?.setState(State.NULL)
        pipeline = null
        appSink = null
        synchronized(audioBufferLock) { audioBuffer = null }
        if (busWatchId != 0) {
            Source.remove(busWatchId)
            busWatchId = 0
        }

        log.info("GStreamer pipeline destroyed")
    }

    override fun disable() {
        if (isRecording) {
            stopRecording().onFailure { error ->
                log.error("Failed to stop recording during disable: ${error.message}")
            }
        }
        cleanup()
        super.disable()
    }
}
