package com.zugaldia.speedofsound.core.models.voice

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File

/**
 * Handles downloading files from URLs with progress reporting.
 * Uses a shared Ktor HTTP client with CIO engine for coroutine-based downloads.
 * This class implements Closeable and must be closed when no longer needed to release resources.
 */
class ModelDownloader : Closeable {
    private val log: Logger = LoggerFactory.getLogger(ModelDownloader::class.java)

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MINUTES * SECONDS_IN_MINUTE * MILLIS_IN_SECOND
            connectTimeoutMillis = CONNECTION_TIMEOUT_SECONDS * MILLIS_IN_SECOND
            socketTimeoutMillis = SOCKET_TIMEOUT_SECONDS * MILLIS_IN_SECOND
        }
    }

    private val _progressFlow = MutableSharedFlow<DownloadProgress>(replay = 0)
    val progressFlow: SharedFlow<DownloadProgress> = _progressFlow.asSharedFlow()

    companion object {
        private const val BUFFER_SIZE = 8192
        private const val SECONDS_IN_MINUTE = 60L
        private const val MILLIS_IN_SECOND = 1000L
        private const val REQUEST_TIMEOUT_MINUTES = 30L
        private const val CONNECTION_TIMEOUT_SECONDS = 30L
        private const val SOCKET_TIMEOUT_SECONDS = 30L
        private const val PERCENTAGE_MULTIPLIER = 100
        private const val PROGRESS_EMISSION_THRESHOLD = 1
    }

    /**
     * Represents the current state of a download operation.
     *
     * @property bytesDownloaded The number of bytes downloaded so far
     * @property totalBytes The total size of the file in bytes, or null if unknown
     * @property percentage The download percentage (0-100), or null if the total size is unknown
     */
    data class DownloadProgress(
        val bytesDownloaded: Long,
        val totalBytes: Long?,
        val percentage: Float?
    )

    /**
     * Downloads a file from the specified URL to the destination file.
     * Progress updates are emitted to the [progressFlow].
     *
     * @param url The URL to download from
     * @param destination The local file to save the download to
     * @return Result indicating success or failure
     */
    suspend fun downloadFile(
        url: String,
        destination: File
    ): Result<Unit> = runCatching {
        if (destination.exists()) {
            log.info("File already exists at: ${destination.absolutePath}")
            return@runCatching
        }

        log.info("Downloading file from $url to ${destination.absolutePath}")

        // Ensure parent directory exists
        destination.parentFile?.mkdirs()

        withContext(Dispatchers.IO) {
            client.prepareGet(url).execute { response ->
                val totalBytes = response.contentLength()
                log.info("Content-Length for $url: $totalBytes bytes")
                val channel = response.bodyAsChannel()
                var bytesDownloaded = 0L
                var lastEmittedPercentage = 0
                destination.outputStream().use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (!channel.isClosedForRead) {
                        val bytesRead = channel.readAvailable(buffer, 0, BUFFER_SIZE)
                        if (bytesRead > 0) {
                            output.write(buffer, 0, bytesRead)
                            bytesDownloaded += bytesRead
                            val percentage =
                                totalBytes?.let { (bytesDownloaded.toFloat() / it * PERCENTAGE_MULTIPLIER) }
                            val shouldEmit = percentage?.toInt()?.let { currentPercentage ->
                                currentPercentage >= lastEmittedPercentage + PROGRESS_EMISSION_THRESHOLD ||
                                        currentPercentage >= PERCENTAGE_MULTIPLIER
                            } ?: true
                            if (shouldEmit) {
                                _progressFlow.emit(
                                    DownloadProgress(
                                        bytesDownloaded = bytesDownloaded,
                                        totalBytes = totalBytes,
                                        percentage = percentage
                                    )
                                )
                                lastEmittedPercentage = percentage?.toInt() ?: -1
                            }
                        }
                    }
                }
            }
        }
        }

    override fun close() {
        client.close()
    }
}
