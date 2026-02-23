package com.zugaldia.speedofsound.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.generateTmpWavFilePath
import com.zugaldia.speedofsound.core.plugins.recorder.JvmRecorder
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderOptions
import org.slf4j.LoggerFactory

class RecordCommand : CliktCommand(name = "record") {
    private val logger = LoggerFactory.getLogger(RecordCommand::class.java)

    companion object {
        private const val RECORDING_DURATION_MS = 10_000L
    }

    override fun run() {
        val recorder = JvmRecorder(RecorderOptions(enableDebug = true))
        recorder.initialize()
        recorder.enable()

        logger.info("Starting 10-second recording from default device...")
        recorder.startRecording()
        Thread.sleep(RECORDING_DURATION_MS)

        val result = recorder.stopRecording()
        result.onSuccess { response ->
            logger.info("Recording complete. Captured ${response.audioData.size} bytes.")
            val filePath = generateTmpWavFilePath()
            val success = AudioManager.saveToWav(response.audioData, AudioInfo.Default, filePath)
            if (success) {
                logger.info("Saved recording to: $filePath")
            } else {
                logger.error("Failed to save recording to: $filePath")
            }
        }.onFailure { error ->
            logger.error("Recording failed: ${error.message}", error)
        }

        recorder.disable()
        recorder.shutdown()
    }
}
