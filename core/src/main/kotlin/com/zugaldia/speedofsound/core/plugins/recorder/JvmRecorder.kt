package com.zugaldia.speedofsound.core.plugins.recorder

import com.zugaldia.speedofsound.core.audio.AudioConstants.BITS_PER_BYTE
import com.zugaldia.speedofsound.core.audio.AudioInputDevice
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.plugins.AppPlugin
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.TargetDataLine

/**
 * JVM-based audio recorder plugin.
 */
class JvmRecorder(
    options: RecorderOptions = RecorderOptions(),
) : AppPlugin<RecorderOptions>(initialOptions = options) {
    private var targetDataLine: TargetDataLine? = null
    private var recordingThread: Thread? = null
    private var audioBuffer: ByteArrayOutputStream? = null

    @Volatile
    private var isRecording = false

    /**
     * Returns whether a recording is currently in progress.
     */
    fun isCurrentlyRecording(): Boolean = isRecording

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1024
        private const val THREAD_JOIN_TIMEOUT_MS = 1000L

        /**
         * Returns a list of all available audio input devices (microphones) in the system.
         * Only devices that support audio capture (TargetDataLine) are included.
         */
        fun getAvailableDevices(): List<AudioInputDevice> {
            return AudioSystem.getMixerInfo()
                .filter { mixerInfo ->
                    val mixer = AudioSystem.getMixer(mixerInfo)
                    mixer.targetLineInfo.any { lineInfo ->
                        TargetDataLine::class.java.isAssignableFrom(lineInfo.lineClass)
                    }
                }
                .map { mixerInfo ->
                    AudioInputDevice(
                        name = mixerInfo.name,
                        vendor = mixerInfo.vendor,
                        description = mixerInfo.description,
                        version = mixerInfo.version,
                    )
                }
        }
    }

    override fun initialize() {
        super.initialize()
    }

    override fun enable() {
        super.enable()
    }

    /**
     * Starts recording audio from the default input device.
     * Audio is captured using the format specified in RecorderOptions.
     */
    fun startRecording() {
        if (isRecording) {
            log.warn("Recording is already in progress.")
            return
        }

        val audioInfo = currentOptions.audioInfo
        val audioFormat = AudioFormat(
            audioInfo.sampleRate.toFloat(),
            audioInfo.sampleWidth * BITS_PER_BYTE,
            audioInfo.channels,
            true,  // signed
            false  // little endian
        )

        val dataLineInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)
        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            log.error("Audio line not supported for format: $audioFormat")
            return
        }

        try {
            targetDataLine = AudioSystem.getLine(dataLineInfo) as TargetDataLine
            targetDataLine?.open(audioFormat)
            targetDataLine?.start()

            audioBuffer = ByteArrayOutputStream()
            isRecording = true

            recordingThread = Thread {
                val bufferSize = targetDataLine?.bufferSize ?: DEFAULT_BUFFER_SIZE
                val buffer = ByteArray(minOf(DEFAULT_BUFFER_SIZE, bufferSize))
                while (isRecording) {
                    val bytesRead = targetDataLine?.read(buffer, 0, buffer.size) ?: 0
                    if (bytesRead > 0) {
                        audioBuffer?.write(buffer, 0, bytesRead)
                        if (currentOptions.computeVolumeLevel) {
                            val level = AudioManager.computeRmsLevel(buffer.copyOf(bytesRead))
                            tryEmitEvent(RecorderEvent.RecordingLevel(level))
                        }
                    }
                }
            }

            recordingThread?.start()
            log.info("Recording started.")
        } catch (e: LineUnavailableException) {
            log.error("Failed to start recording: ${e.message}")
            cleanup()
        }
    }

    /**
     * Stops recording and returns the captured raw audio data.
     * @return ByteArray containing the raw PCM audio data, or null if not recording.
     */
    fun stopRecording(): ByteArray? {
        if (!isRecording) {
            log.warn("No recording in progress.")
            return null
        }

        isRecording = false

        try {
            recordingThread?.join(THREAD_JOIN_TIMEOUT_MS)
        } catch (e: InterruptedException) {
            log.warn("Recording thread interrupted: ${e.message}")
        }

        targetDataLine?.stop()
        targetDataLine?.close()

        val audioData = audioBuffer?.toByteArray()
        cleanup()

        log.info("Recording stopped. Captured ${audioData?.size ?: 0} bytes.")
        return audioData
    }

    private fun cleanup() {
        isRecording = false
        targetDataLine = null
        recordingThread = null
        audioBuffer = null
    }

    override fun disable() {
        if (isRecording) {
            stopRecording()
        }
        super.disable()
    }

    override fun shutdown() {
        if (isRecording) {
            stopRecording()
        }
        super.shutdown()
    }
}
