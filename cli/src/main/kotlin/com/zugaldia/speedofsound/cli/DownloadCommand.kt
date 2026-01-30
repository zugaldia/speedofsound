package com.zugaldia.speedofsound.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.zugaldia.speedofsound.core.getDataDir
import com.zugaldia.speedofsound.core.models.DEFAULT_ASR_MODEL_ID
import com.zugaldia.speedofsound.core.models.ModelManager
import com.zugaldia.speedofsound.core.models.SUPPORTED_ASR_MODELS
import org.apache.logging.log4j.LogManager

class DownloadCommand : CliktCommand(name = "download") {
    private val logger = LogManager.getLogger(DownloadCommand::class.java)
    private val modelManager = ModelManager()

    private val modelId: String? by argument(
        name = "model",
        help = "ID of the model to download"
    ).optional()

    override fun run() {
        modelId?.let { downloadModel(it) } ?: listAvailableModels()
    }

    private fun listAvailableModels() {
        logger.info("Data folder: ${getDataDir()}")
        logger.info("Available models:")
        SUPPORTED_ASR_MODELS.values.sortedBy { it.id }.forEach { model ->
            val downloaded = if (modelManager.isModelDownloaded(model.id)) { "[x]" } else { "[ ]" }
            logger.info("$downloaded [${model.id}] ${model.name} (${model.dataSizeMegabytes} MB)")
        }
    }

    private fun downloadModel(id: String) {
        val model = SUPPORTED_ASR_MODELS[id]
        if (model == null) {
            logger.error("Model not found: $id")
            return
        }

        if (modelManager.isModelDownloaded(id)) {
            logger.info("Model '$id' is already downloaded.")
            return
        }

        logger.info("Downloading model: ${model.name} (${model.dataSizeMegabytes} MB)")
        if (id == DEFAULT_ASR_MODEL_ID) {
            modelManager.extractDefaultModel().onSuccess {
                logger.info("Default model extracted successfully.")
            }.onFailure {
                logger.error("Failed to extract default model: ${it.message}", it)
            }
        } else {
            modelManager.downloadModel(id).onSuccess {
                logger.info("Model downloaded successfully: ${model.name}")
            }.onFailure { error ->
                logger.error("Failed to download model: ${error.message}", error)
            }
        }
    }
}
