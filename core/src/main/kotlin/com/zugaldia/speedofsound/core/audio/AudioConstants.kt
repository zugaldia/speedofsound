@file:Suppress("MatchingDeclarationName")

package com.zugaldia.speedofsound.core.audio

/**
 * Audio-related constants shared across all modules.
 */
object AudioConstants {
    /**
     * Audio format constants
     */
    const val AUDIO_FORMAT_PCM = "PCM"

    // Audio channel values
    const val AUDIO_CHANNELS_MONO = 1
    const val AUDIO_CHANNELS_STEREO = 2

    // Audio sample width values (in bytes)
    const val AUDIO_SAMPLE_WIDTH_16BIT = 2
    const val AUDIO_SAMPLE_WIDTH_32BIT = 4

    // Audio sample rate values (in Hz)
    const val AUDIO_SAMPLE_RATE_16KHZ = 16000
    const val AUDIO_SAMPLE_RATE_24KHZ = 24000

    // Audio format and parameters
    const val DEFAULT_AUDIO_FORMAT = AUDIO_FORMAT_PCM
    const val DEFAULT_AUDIO_CHANNELS = AUDIO_CHANNELS_MONO
    const val DEFAULT_AUDIO_SAMPLE_WIDTH = AUDIO_SAMPLE_WIDTH_16BIT
    const val DEFAULT_AUDIO_SAMPLE_RATE = AUDIO_SAMPLE_RATE_16KHZ

    // Microphone & VAD
    const val AUDIO_FRAME_LENGTH_512 = 512  // Preferred by VAD providers
    const val DEFAULT_FRAME_LENGTH = AUDIO_FRAME_LENGTH_512
}
