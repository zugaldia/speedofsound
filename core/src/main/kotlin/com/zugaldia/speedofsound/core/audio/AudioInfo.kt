package com.zugaldia.speedofsound.core.audio

import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_CHANNELS_MONO
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_FORMAT_PCM
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_RATE_16KHZ
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_WIDTH_16BIT
import javax.sound.sampled.AudioFormat

data class AudioInfo(
    val format: String,
    val channels: Int,
    val sampleRate: Int,
    val sampleWidth: Int
) {
    /**
     * Converts this AudioInfo to a javax.sound.sampled.AudioFormat
     * Note: sampleWidth is stored in bytes and must be converted to bits
     */
    fun toAudioFormat(): AudioFormat {
        return AudioFormat(
            sampleRate.toFloat(),
            sampleWidth * BITS_PER_BYTE,
            channels,
            true, // signed
            false // little-endian
        )
    }

    companion object {
        private const val BITS_PER_BYTE = 8

        val Default = AudioInfo(
            AUDIO_FORMAT_PCM,
            AUDIO_CHANNELS_MONO,
            AUDIO_SAMPLE_RATE_16KHZ,
            AUDIO_SAMPLE_WIDTH_16BIT
        )

        /**
         * Creates an AudioInfo from a javax.sound.sampled.AudioFormat
         * Note: AudioFormat.sampleSizeInBits is converted to bytes for sampleWidth
         */
        fun from(audioFormat: AudioFormat): AudioInfo {
            return AudioInfo(
                format = AUDIO_FORMAT_PCM,
                channels = audioFormat.channels,
                sampleRate = audioFormat.sampleRate.toInt(),
                sampleWidth = audioFormat.sampleSizeInBits / BITS_PER_BYTE
            )
        }
    }
}
