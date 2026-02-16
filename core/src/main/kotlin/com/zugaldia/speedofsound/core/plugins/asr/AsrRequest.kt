package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.audio.AudioInfo

data class AsrRequest(
    val audioData: ByteArray,
    val audioInfo: AudioInfo = AudioInfo.Default,
)
