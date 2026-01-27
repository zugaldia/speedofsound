package com.zugaldia.speedofsound.core.plugins.recorder

import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Options for configuring the JVM audio recorder.
 */
data class RecorderOptions(
    val audioInfo: AudioInfo = AudioInfo.Default,
) : AppPluginOptions
