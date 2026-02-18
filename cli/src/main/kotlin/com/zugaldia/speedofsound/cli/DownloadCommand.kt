package com.zugaldia.speedofsound.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.zugaldia.speedofsound.core.getDataDir
import com.zugaldia.speedofsound.core.models.voice.ModelManager
import com.zugaldia.speedofsound.core.models.voice.ModelManagerEvent
import com.zugaldia.speedofsound.core.plugins.asr.DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.asr.SUPPORTED_SHERPA_WHISPER_ASR_MODELS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class DownloadCommand : CliktCommand(name = "download") {
    private val logger = LoggerFactory.getLogger(DownloadCommand::class.java)
    private val modelManager = ModelManager()

    private val modelId: String? by argument(
        name = "model",
        help = "ID of the model to download"
    ).optional()

    companion object {
        private const val COLLECTOR_START_DELAY_MS = 100L
        private const val COMPLETION_CHECK_DELAY_MS = 100L
    }

    override fun run() {
        modelId?.let { downloadModel(it) } ?: listAvailableModels()
    }

    private fun listAvailableModels() {
        logger.info("Data folder: ${getDataDir()}")
        logger.info("Available models:")
        SUPPORTED_SHERPA_WHISPER_ASR_MODELS.values.sortedBy { it.id }.forEach { model ->
            val downloaded = if (modelManager.isModelDownloaded(model.id)) { "[x]" } else { "[ ]" }
            logger.info("$downloaded [${model.id}] ${model.name} (${model.dataSizeMegabytes} MB)")
        }
    }

    private fun downloadModel(id: String) {
        val model = SUPPORTED_SHERPA_WHISPER_ASR_MODELS[id]
        if (model == null) {
            logger.error("Model not found: $id")
            return
        }

        if (modelManager.isModelDownloaded(id)) {
            logger.info("Model '$id' is already downloaded.")
            return
        }

        logger.info("Downloading model: ${model.name} (${model.dataSizeMegabytes} MB)")
        if (id == DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID) {
            modelManager.extractDefaultModel().onSuccess {
                logger.info("Default model extracted successfully.")
            }.onFailure {
                logger.error("Failed to extract default model: ${it.message}", it)
            }
        } else {
            runBlocking {
                var downloadCompleted = false
                val collectorJob = launch {
                    modelManager.events.collect { event ->
                        when (event) {
                            is ModelManagerEvent.Progress -> {
                                val message = event.percentage ?: event.message
                                logger.info("${event.operation}: $message")
                            }
                            is ModelManagerEvent.Completed -> {
                                logger.info("Model downloaded successfully: ${model.name}")
                                downloadCompleted = true
                            }
                            is ModelManagerEvent.Error -> {
                                logger.error("Failed to download model: ${event.message}", event.exception)
                                downloadCompleted = true
                            }
                        }
                    }
                }

                delay(COLLECTOR_START_DELAY_MS)
                modelManager.downloadModel(id)
                while (!downloadCompleted) {
                    delay(COMPLETION_CHECK_DELAY_MS)
                }

                collectorJob.cancel()
            }
        }
    }
}
