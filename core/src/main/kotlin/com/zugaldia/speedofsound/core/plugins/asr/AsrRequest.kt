package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.audio.AudioInfo

data class AsrRequest(
    val audioData: FloatArray,
    val audioInfo: AudioInfo = AudioInfo.Default,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AsrRequest

        if (!audioData.contentEquals(other.audioData)) return false
        if (audioInfo != other.audioInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = audioData.contentHashCode()
        result = 31 * result + audioInfo.hashCode()
        return result
    }
}
