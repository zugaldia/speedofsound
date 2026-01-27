package com.zugaldia.speedofsound.core.audio

import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_CHANNELS_MONO
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_FORMAT_PCM
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_RATE_16KHZ
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_WIDTH_16BIT

data class AudioInfo(
    val format: String,
    val channels: Int,
    val sampleRate: Int,
    val sampleWidth: Int
) {

    companion object {
        val Default = AudioInfo(
            AUDIO_FORMAT_PCM,
            AUDIO_CHANNELS_MONO,
            AUDIO_SAMPLE_RATE_16KHZ,
            AUDIO_SAMPLE_WIDTH_16BIT
        )
    }
}
