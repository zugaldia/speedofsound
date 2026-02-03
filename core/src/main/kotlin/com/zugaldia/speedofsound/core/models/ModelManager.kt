package com.zugaldia.speedofsound.core.models

import com.zugaldia.speedofsound.core.getDataDir
import com.zugaldia.speedofsound.core.getTmpDataDir
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

class ModelManager {
    private val log: Logger = LoggerFactory.getLogger(ModelManager::class.java)

    companion object {
        private const val BUFFER_SIZE = 8192
    }

    /**
     * Get the path for a model with the given ID.
     * Returns a Path under the data directory in the format "models/modelId"
     * Creates the directory if it doesn't exist.
     */
    fun getModelPath(modelId: String): Path {
        val dataDir = getDataDir()
        val modelPath = dataDir.resolve("models").resolve(modelId)
        val dir = modelPath.toFile()
        if (!dir.exists()) {
            dir.mkdirs() // Ensure the directory exists
        }

        return modelPath
    }

    fun isModelDownloaded(modelId: String): Boolean {
        val model = SUPPORTED_ASR_MODELS[modelId] ?: return false
        val modelPath = getModelPath(modelId)
        val modelDir = modelPath.toFile()
        val encoderFile = modelPath.resolve(model.encoder).toFile()
        val decoderFile = modelPath.resolve(model.decoder).toFile()
        val tokensFile = modelPath.resolve(model.tokens).toFile()
        return modelDir.exists() && modelDir.isDirectory &&
               encoderFile.exists() && encoderFile.length() > 0 &&
               decoderFile.exists() && decoderFile.length() > 0 &&
               tokensFile.exists() && tokensFile.length() > 0
    }

    fun extractDefaultModel(): Result<Unit> = runCatching {
        if (isModelDownloaded(DEFAULT_ASR_MODEL_ID)) {
            log.info("Default model already extracted: $DEFAULT_ASR_MODEL_ID")
            return@runCatching
        }
        log.info("Extracting default model: $DEFAULT_ASR_MODEL_ID")
        val model = SUPPORTED_ASR_MODELS[DEFAULT_ASR_MODEL_ID]
            ?: throw IllegalStateException("Default model not found in supported models")

        val modelPath = getModelPath(DEFAULT_ASR_MODEL_ID)
        val filesToExtract = listOf(model.encoder, model.decoder, model.tokens)
        for (filename in filesToExtract) {
            val resourcePath = "/models/asr/$filename"
            val inputStream = this::class.java.getResourceAsStream(resourcePath)
                ?: throw IllegalStateException("Resource not found: $resourcePath")

            val outputFile = modelPath.resolve(filename).toFile()
            inputStream.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    fun downloadModel(modelId: String): Result<Unit> = runCatching {
        if (isModelDownloaded(modelId)) {
            log.info("Model already downloaded: $modelId")
            return@runCatching
        }

        log.info("Starting download for model: $modelId")
        val model = SUPPORTED_ASR_MODELS[modelId] ?: throw IllegalArgumentException("Model not found: $modelId")
        val tempDir = getTmpDataDir()

        try {
            // Download the compressed file
            val downloadedFile = tempDir.resolve("${modelId}.tar.bz2").toFile()
            downloadFile(model.archiveUrl, downloadedFile)
            verifySha256(downloadedFile, model.archiveSha256sum)

            // Extract the tar.bz2 archive
            log.info("Extracting archive for model: $modelId")
            extractTarBz2(downloadedFile, tempDir.toFile())

            // Copy the required model files to the destination
            copyModelFiles(tempDir.toFile(), modelId, model)
        } finally {
            // Clean up the temporary directory and all its contents
            tempDir.deleteRecursively()
        }
    }

    /**
     * Copy model files from the extracted archive to the model destination.
     * The archive structure is expected to be: {tempDir}/{modelId}/{encoder/decoder/tokens}
     */
    private fun copyModelFiles(tempDir: File, modelId: String, model: AsrModel) {
        val modelPath = getModelPath(modelId)
        val extractedModelDir = File(tempDir, modelId)
        if (!extractedModelDir.exists() || !extractedModelDir.isDirectory) {
            throw IllegalStateException("Expected directory not found in archive: $modelId")
        }

        val filesToCopy = listOf(model.encoder, model.decoder, model.tokens)
        for (filename in filesToCopy) {
            val sourceFile = File(extractedModelDir, filename)
            if (!sourceFile.exists()) {
                throw IllegalStateException("Required file not found in archive: $modelId/$filename")
            }

            val destFile = modelPath.resolve(filename).toFile()
            sourceFile.copyTo(destFile, overwrite = true)
        }
    }

    /**
     * Download a file from a URL to a local destination.
     */
    private fun downloadFile(url: String, destination: File) {
        if (destination.exists()) {
            log.info("File already downloaded at: ${destination.absolutePath}")
            return
        }

        log.info("Downloading file from $url to ${destination.absolutePath}")
        URI(url).toURL().openStream().use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    /**
     * Extract a tar.bz2 archive to a destination directory.
     */
    @Suppress("NestedBlockDepth")
    private fun extractTarBz2(sourceFile: File, destinationDir: File) {
        destinationDir.mkdirs()
        sourceFile.inputStream().use { fileInput ->
            BufferedInputStream(fileInput).use { bufferedInput ->
                BZip2CompressorInputStream(bufferedInput).use { bzipInput ->
                    TarArchiveInputStream(bzipInput).use { tarInput ->
                        while (true) {
                            val entry = tarInput.nextEntry ?: break
                            val outputFile = File(destinationDir, entry.name)
                            if (entry.isDirectory) {
                                log.info("Creating directory: ${outputFile.absolutePath}")
                                outputFile.mkdirs()
                            } else {
                                log.info("Extracting file: ${outputFile.absolutePath}")
                                outputFile.parentFile?.mkdirs()
                                outputFile.outputStream().use { output ->
                                    tarInput.copyTo(output)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Compute the SHA256 checksum of a file.
     * Returns the checksum as a lowercase hexadecimal string.
     */
    private fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead = input.read(buffer)
            while (bytesRead != -1) {
                digest.update(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Verify that a file's SHA256 checksum matches the expected value.
     * Throws IllegalStateException if the checksums don't match.
     */
    private fun verifySha256(file: File, expectedChecksum: String) {
        val actualChecksum = computeSha256(file)
        if (actualChecksum != expectedChecksum.lowercase()) {
            throw IllegalStateException(
                "SHA256 checksum mismatch for ${file.name}. " +
                "Expected: $expectedChecksum, Actual: $actualChecksum"
            )
        } else {
            log.info("SHA256 checksum verified for ${file.name}")
        }
    }
}
