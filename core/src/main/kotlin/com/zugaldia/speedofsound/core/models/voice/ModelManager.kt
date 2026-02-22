package com.zugaldia.speedofsound.core.models.voice

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path

@Suppress("TooManyFunctions")
class ModelManager(
    private val pathProvider: PathProvider = DefaultPathProvider(),
    private val voiceModelCatalog: VoiceModelCatalog = DefaultVoiceModelCatalog(),
    private val fileSystem: FileSystemOperations = DefaultFileSystemOperations(),
    private val resourceLoader: ResourceLoader = ClasspathResourceLoader(),
    private val idGenerator: IdGenerator = DefaultIdGenerator(),
    private val checksumVerifier: ChecksumVerifier = ChecksumVerifier(),
    private val archiveExtractor: ArchiveExtractor = ArchiveExtractor(),
    private val modelFileManager: ModelFileManager = ModelFileManager(pathProvider, fileSystem),
    private val modelDownloaderFactory: () -> ModelDownloader = { ModelDownloader() }
) {
    private val log: Logger = LoggerFactory.getLogger(ModelManager::class.java)

    private val _events = MutableSharedFlow<ModelManagerEvent>(replay = 0, extraBufferCapacity = 64)
    val events: SharedFlow<ModelManagerEvent> = _events.asSharedFlow()

    /**
     * Returns a Path under the data directory in the format "models/modelId" and creates it if it doesn't exist.
     * Note that currently we are not creating subfolders per provider. We are counting on the model ID to include
     * the provider name to avoid clashing names (e.g., sherpa-onnx-whisper-tiny vs. onnx_whisper_tiny_en).
     * In other words, **model IDs are globally unique to the application.**
     */
    fun getModelPath(modelId: String): Path {
        return modelFileManager.getModelPath(modelId)
    }

    fun isModelDownloaded(modelId: String): Boolean {
        val model = voiceModelCatalog.getModel(modelId) ?: return false
        return modelFileManager.isModelDownloaded(modelId, model)
    }

    suspend fun deleteModel(modelId: String): Result<Unit> = runCatching {
        emitProgress(
            modelId = modelId,
            operation = ModelManagerEvent.Progress.Operation.DELETING,
            message = "Deleting $modelId files"
        )

        val modelPath = getModelPath(modelId)
        if (!fileSystem.exists(modelPath)) {
            emitCompleted(modelId, ModelManagerEvent.Completed.Operation.DELETE)
            return@runCatching
        }

        fileSystem.deleteRecursively(modelPath)
        emitCompleted(modelId, ModelManagerEvent.Completed.Operation.DELETE)
    }.onFailure { exception ->
        emitError(modelId, ModelManagerEvent.Error.Operation.DELETE, exception)
    }

    fun extractDefaultModel(): Result<Unit> = runCatching {
        val defaultModelId = voiceModelCatalog.getDefaultModelId()
        if (isModelDownloaded(defaultModelId)) {
            log.info("Default model already extracted: $defaultModelId")
            return@runCatching
        }

        log.info("Extracting default model: $defaultModelId")
        val model = voiceModelCatalog.getModel(defaultModelId)
            ?: throw IllegalStateException("Default model not found in supported models")

        modelFileManager.extractDefaultModelFromResources(defaultModelId, model, resourceLoader).getOrThrow()
    }

    suspend fun downloadModel(modelId: String): Result<Unit> = runCatching {
        if (isModelDownloaded(modelId)) {
            emitCompleted(modelId, ModelManagerEvent.Completed.Operation.DOWNLOAD)
            return@runCatching
        }

        log.info("Starting download for model: $modelId")
        val model = voiceModelCatalog.getModel(modelId) ?: throw IllegalArgumentException("Model not found: $modelId")
        val archiveFile = model.archiveFile ?: throw IllegalArgumentException("Model $modelId needs an archive file")
        val tempDir = createTempDirectory()

        try {
            val downloadedFile = downloadArchive(modelId, archiveFile, tempDir)
            verifyArchive(modelId, downloadedFile, archiveFile)
            extractArchive(modelId, downloadedFile, tempDir)
            copyFiles(modelId, model, tempDir)
            emitCompleted(modelId, ModelManagerEvent.Completed.Operation.DOWNLOAD)
        } finally {
            cleanupTempDirectory(tempDir)
        }
    }.onFailure { exception ->
        emitError(modelId, ModelManagerEvent.Error.Operation.DOWNLOAD, exception)
    }

    private fun createTempDirectory(): Path {
        val tempDir = pathProvider.getTmpDataDir().resolve(idGenerator.generateUniqueId())
        fileSystem.mkdirs(tempDir)
        return tempDir
    }

    private suspend fun downloadArchive(
        modelId: String, archiveFile: VoiceModelFile, tempDir: Path
    ): File {
        val downloadedFile = tempDir.resolve("${modelId}.tar.bz2").toFile()
        val archiveUrl = archiveFile.url ?: throw IllegalArgumentException("Archive URL not available")
        modelDownloaderFactory().use { downloader ->
            coroutineScope {
                val progressJob = launch {
                    downloader.progressFlow.collect { progress ->
                        emitProgress(
                            modelId = modelId,
                            operation = ModelManagerEvent.Progress.Operation.DOWNLOADING,
                            message = "Downloading $modelId archive",
                            bytesProcessed = progress.bytesDownloaded,
                            totalBytes = progress.totalBytes,
                            percentage = progress.percentage
                        )
                    }
                }

                try {
                    downloader.downloadFile(archiveUrl, downloadedFile).getOrThrow()
                } finally {
                    progressJob.cancel()
                }
            }
        }
        return downloadedFile
    }

    private suspend fun verifyArchive(
        modelId: String, downloadedFile: File, archiveFile: VoiceModelFile
    ) {
        val archiveSha256 = archiveFile.sha256sum ?: throw IllegalArgumentException("Archive SHA256 not available")
        emitProgress(
            modelId = modelId,
            operation = ModelManagerEvent.Progress.Operation.VERIFYING_CHECKSUM,
            message = "Verifying $modelId archive integrity"
        )

        checksumVerifier.verifySha256(downloadedFile, archiveSha256).getOrThrow()
    }

    private suspend fun extractArchive(
        modelId: String, downloadedFile: File, tempDir: Path
    ) {
        emitProgress(
            modelId = modelId,
            operation = ModelManagerEvent.Progress.Operation.EXTRACTING,
            message = "Extracting $modelId archive"
        )

        archiveExtractor.extractTarBz2(downloadedFile, tempDir.toFile()).getOrThrow()
    }

    private suspend fun copyFiles(
        modelId: String, model: VoiceModel, tempDir: Path
    ) {
        emitProgress(
            modelId = modelId,
            operation = ModelManagerEvent.Progress.Operation.COPYING_FILES,
            message = "Copying $modelId files"
        )

        modelFileManager.copyModelFiles(tempDir.toFile(), modelId, model).getOrThrow()
    }

    private fun cleanupTempDirectory(tempDir: Path) {
        fileSystem.deleteRecursively(tempDir)
    }

    // Helper methods for event emission
    private suspend fun emitProgress(
        modelId: String,
        operation: ModelManagerEvent.Progress.Operation,
        message: String,
        bytesProcessed: Long? = null,
        totalBytes: Long? = null,
        percentage: Float? = null
    ) {
        _events.emit(
            ModelManagerEvent.Progress(
                modelId = modelId,
                operation = operation,
                message = message,
                bytesProcessed = bytesProcessed,
                totalBytes = totalBytes,
                percentage = percentage
            )
        )
    }

    private suspend fun emitCompleted(
        modelId: String, operation: ModelManagerEvent.Completed.Operation
    ) {
        _events.emit(
            ModelManagerEvent.Completed(
                modelId = modelId, operation = operation
            )
        )
    }

    private suspend fun emitError(
        modelId: String, operation: ModelManagerEvent.Error.Operation, exception: Throwable
    ) {
        _events.emit(
            ModelManagerEvent.Error(
                modelId = modelId,
                operation = operation,
                message = exception.message ?: "Operation failed",
                exception = exception
            )
        )
    }
}
