package com.zugaldia.speedofsound.core.models.voice

/**
 * Events emitted by ModelManager during model operations.
 */
sealed interface ModelManagerEvent {
    val modelId: String

    /**
     * Emitted to report progress during model operations.
     */
    data class Progress(
        override val modelId: String,
        val operation: Operation,
        val message: String,
        val bytesProcessed: Long? = null,
        val totalBytes: Long? = null,
        val percentage: Float? = null
    ) : ModelManagerEvent {
        enum class Operation {
            DOWNLOADING,
            VERIFYING_CHECKSUM,
            EXTRACTING,
            COPYING_FILES,
            DELETING
        }
    }

    /**
     * Emitted when a model operation completes successfully.
     */
    data class Completed(
        override val modelId: String,
        val operation: Operation
    ) : ModelManagerEvent {
        enum class Operation {
            DOWNLOAD,
            DELETE
        }
    }

    /**
     * Emitted when a model operation fails.
     */
    data class Error(
        override val modelId: String,
        val operation: Operation,
        val message: String,
        val exception: Throwable? = null
    ) : ModelManagerEvent {
        enum class Operation {
            DOWNLOAD,
            DELETE
        }
    }
}
