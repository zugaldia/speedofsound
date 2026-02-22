package com.zugaldia.speedofsound.core.models.voice

import java.io.File
import java.nio.file.Path

/**
 * Manages model file paths and operations.
 */
class ModelFileManager(private val pathProvider: PathProvider, private val fileSystem: FileSystemOperations) {
    /**
     * Returns a Path under the data directory in the format "models/modelId" and creates it if it doesn't exist.
     * Note that currently we are not creating subfolders per provider. We are counting on the model ID to include
     * the provider name to avoid clashing names (e.g., sherpa-onnx-whisper-tiny vs. onnx_whisper_tiny_en).
     * In other words, **model IDs are globally unique to the application.**
     */
    fun getModelPath(modelId: String): Path {
        val dataDir = pathProvider.getDataDir()
        val modelPath = dataDir.resolve("models").resolve(modelId)
        if (!fileSystem.exists(modelPath)) {
            fileSystem.mkdirs(modelPath)
        }
        return modelPath
    }

    /**
     * Check if all required model components are present and valid.
     */
    fun isModelDownloaded(modelId: String, model: VoiceModel): Boolean {
        val modelPath = getModelPath(modelId)
        val modelPathExists = fileSystem.exists(modelPath) && fileSystem.isDirectory(modelPath)
        if (!modelPathExists) {
            return false
        }

        return model.components.all { component ->
            val filePath = modelPath.resolve(component.name)
            fileSystem.exists(filePath) && fileSystem.fileLength(filePath.toFile()) > 0
        }
    }

    /**
     * Copy model files from the extracted archive to the model destination.
     * The archive structure is expected to be: {tempDir}/{modelId}/{component files}
     */
    fun copyModelFiles(tempDir: File, modelId: String, model: VoiceModel): Result<Unit> = runCatching {
        val modelPath = getModelPath(modelId)
        val extractedModelDir = File(tempDir, modelId)
        if (!extractedModelDir.exists() || !extractedModelDir.isDirectory) {
            throw IllegalStateException("Expected directory not found in archive: $modelId")
        }

        for (component in model.components) {
            val sourceFile = File(extractedModelDir, component.name)
            if (!sourceFile.exists()) {
                throw IllegalStateException("Required file not found in archive: $modelId/${component.name}")
            }

            val destFile = modelPath.resolve(component.name).toFile()
            fileSystem.copyFile(sourceFile, destFile, overwrite = true)
        }
    }

    /**
     * Extract default model components from resources to the model directory.
     */
    fun extractDefaultModelFromResources(
        modelId: String, model: VoiceModel, resourceLoader: ResourceLoader
    ): Result<Unit> = runCatching {
        val modelPath = getModelPath(modelId)
        for (component in model.components) {
            val resourcePath = "/models/asr/${component.name}"
            val inputStream = resourceLoader.loadResource(resourcePath)
                ?: throw IllegalStateException("Resource not found: $resourcePath")

            val outputFile = modelPath.resolve(component.name).toFile()
            inputStream.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}
