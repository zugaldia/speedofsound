package com.zugaldia.speedofsound.core.models.voice

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.File

/**
 * Handles extraction of tar.bz2 archives.
 */
class ArchiveExtractor {
    private val log: Logger = LoggerFactory.getLogger(ArchiveExtractor::class.java)

    /**
     * Extract a tar.bz2 archive to a destination directory.
     */
    @Suppress("NestedBlockDepth")
    fun extractTarBz2(sourceFile: File, destinationDir: File): Result<Unit> = runCatching {
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
}
