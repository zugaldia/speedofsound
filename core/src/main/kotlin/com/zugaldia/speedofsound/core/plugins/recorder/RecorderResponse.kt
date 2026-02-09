package com.zugaldia.speedofsound.core.plugins.recorder

data class RecorderResponse(
    val audioData: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecorderResponse

        return audioData.contentEquals(other.audioData)
    }

    override fun hashCode(): Int {
        return audioData.contentHashCode()
    }
}
