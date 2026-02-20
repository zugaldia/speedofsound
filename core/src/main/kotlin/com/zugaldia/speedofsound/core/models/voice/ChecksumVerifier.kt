package com.zugaldia.speedofsound.core.models.voice

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.security.MessageDigest

/**
 * Handles SHA256 checksum computation and verification.
 */
class ChecksumVerifier {
    private val log: Logger = LoggerFactory.getLogger(ChecksumVerifier::class.java)

    companion object {
        private const val BUFFER_SIZE = 8192
    }

    /**
     * Compute the SHA256 checksum of a file.
     * Returns the checksum as a lowercase hexadecimal string.
     */
    fun computeSha256(file: File): String {
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
     * Returns [Result.success] if checksums match, [Result.failure] otherwise.
     */
    fun verifySha256(file: File, expectedChecksum: String): Result<Unit> = runCatching {
        val actualChecksum = computeSha256(file)
        if (actualChecksum != expectedChecksum.lowercase()) {
            throw IllegalStateException(
                "SHA256 checksum mismatch for ${file.name}. Expected: $expectedChecksum, Actual: $actualChecksum"
            )
        }

        log.info("SHA256 checksum verified for ${file.name}")
    }
}
