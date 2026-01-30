package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.generateTmpWavFilePath
import com.zugaldia.speedofsound.core.plugins.recorder.JvmRecorder
import org.apache.logging.log4j.LogManager

class MainViewModel {
    private val logger = LogManager.getLogger()

    var state: MainState = MainState()
        private set

    private val recorder = JvmRecorder()

    init {
        logger.info("Initializing.")
        recorder.initialize()
        recorder.enable()

        val devices = JvmRecorder.getAvailableDevices()
        logger.info("Found ${devices.size} audio input device(s):")
        devices.forEach { device ->
            logger.info("- ${device.name}: ${device.description}")
        }
    }

    fun startRecording() {
        logger.info("Starting recording.")
        recorder.startRecording()
        state = state.copy(isRecording = true)
    }

    fun stopRecording() {
        logger.info("Stopping recording.")
        val audioData = recorder.stopRecording()
        logger.info("Recording complete. Captured ${audioData?.size ?: 0} bytes.")

        if (audioData != null && audioData.isNotEmpty()) {
            val filePath = generateTmpWavFilePath()
            val samples = AudioManager.convertPcm16ToFloat(audioData)
            AudioManager.saveToWav(samples, AudioInfo.Default.sampleRate, filePath)
            logger.info("Saved recording to: $filePath")
        }

        state = state.copy(isRecording = false)
    }

    fun shutdown() {
        logger.info("Shutting down.")
        recorder.disable()
        recorder.shutdown()
    }
}
