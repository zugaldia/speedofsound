package com.zugaldia.speedofsound.core.plugins.recorder

import com.zugaldia.speedofsound.core.audio.AudioInputDevice
import com.zugaldia.speedofsound.core.audio.AudioManager
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.TargetDataLine

/**
 * JVM-based audio recorder plugin.
 */
class JvmRecorder(
    options: RecorderOptions = RecorderOptions(),
) : RecorderPlugin<RecorderOptions>(initialOptions = options) {
    override val id: String = ID

    private var targetDataLine: TargetDataLine? = null
    private var recordingThread: Thread? = null
    private var audioBuffer: ByteArrayOutputStream? = null

    @Volatile
    private var isRecording = false

    /**
     * Returns whether a recording is currently in progress.
     */
    override fun isCurrentlyRecording(): Boolean = isRecording

    /**
     * Returns a list of all available audio input devices (microphones) in the system.
     * Only devices that support audio capture (TargetDataLine) are included.
     */
    override fun getAvailableDevices(): List<AudioInputDevice> {
        return AudioSystem.getMixerInfo()
            .filter { mixerInfo ->
                val mixer = AudioSystem.getMixer(mixerInfo)
                mixer.targetLineInfo.any { lineInfo ->
                    TargetDataLine::class.java.isAssignableFrom(lineInfo.lineClass)
                }
            }
            .map { mixerInfo ->
                AudioInputDevice(
                    deviceId = mixerInfo.name,
                    name = mixerInfo.name,
                    vendor = mixerInfo.vendor,
                    description = mixerInfo.description,
                    version = mixerInfo.version,
                )
            }
    }

    companion object {
        const val ID = "RECORDER_JVM"
        private const val DEFAULT_BUFFER_SIZE = 1024
        private const val THREAD_JOIN_TIMEOUT_MS = 1000L
    }

    /**
     * Starts recording audio from the default input device.
     * Audio is captured using the format specified in RecorderOptions.
     */
    override fun startRecording() {
        if (isRecording) {
            log.warn("Recording is already in progress.")
            return
        }

        val audioInfo = currentOptions.audioInfo
        val audioFormat = audioInfo.toAudioFormat()
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
     * Stops recording and returns the captured audio data.
     * @return Result containing RecorderResponse with the captured audio data, or an error.
     */
    override fun stopRecording(): Result<RecorderResponse> = runCatching {
        if (!isRecording) {
            throw IllegalStateException("No recording in progress.")
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

        if (audioData == null || audioData.isEmpty()) {
            throw IllegalStateException("No audio data captured.")
        }

        log.info("Recording stopped. Captured ${audioData.size} bytes.")
        RecorderResponse(audioData)
    }

    private fun cleanup() {
        isRecording = false
        targetDataLine = null
        recordingThread = null
        audioBuffer = null
    }

    override fun disable() {
        if (isRecording) {
            stopRecording().onFailure { error ->
                log.error("Failed to stop recording during disable: ${error.message}")
            }
        }
        super.disable()
    }
}
