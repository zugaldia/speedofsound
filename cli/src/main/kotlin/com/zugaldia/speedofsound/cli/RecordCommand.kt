package com.zugaldia.speedofsound.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.generateTmpWavFilePath
import com.zugaldia.speedofsound.core.plugins.recorder.JvmRecorder
import org.apache.logging.log4j.LogManager

class RecordCommand : CliktCommand(name = "record") {
    private val logger = LogManager.getLogger(RecordCommand::class.java)

    companion object {
        private const val RECORDING_DURATION_MS = 10_000L
    }

    override fun run() {
        val devices = JvmRecorder.getAvailableDevices()
        logger.info("Found ${devices.size} audio input device(s):")
        devices.forEach { device ->
            logger.info("- ${device.name}: ${device.description}")
        }

        val recorder = JvmRecorder()
        recorder.initialize()
        recorder.enable()

        logger.info("Starting 10-second recording from default device...")
        recorder.startRecording()

        Thread.sleep(RECORDING_DURATION_MS)

        val audioData = recorder.stopRecording()
        logger.info("Recording complete. Captured ${audioData?.size ?: 0} bytes.")

        if (audioData != null && audioData.isNotEmpty()) {
            val filePath = generateTmpWavFilePath()
            val samples = AudioManager.convertPcm16ToFloat(audioData)
            val success = AudioManager.saveToWav(samples, AudioInfo.Default.sampleRate, filePath)
            if (success) {
                logger.info("Saved recording to: $filePath (${samples.size} samples)")
            } else {
                logger.error("Failed to save recording to: $filePath")
            }
        }

        recorder.disable()
        recorder.shutdown()
    }
}
