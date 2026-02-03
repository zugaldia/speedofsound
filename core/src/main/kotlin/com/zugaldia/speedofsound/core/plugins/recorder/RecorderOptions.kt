package com.zugaldia.speedofsound.core.plugins.recorder

import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Options for configuring the JVM audio recorder.
 *
 * @param audioInfo Audio format configuration
 * @param computeVolumeLevel When true, the recorder will compute and emit volume level events
 *                           during recording. Default is false for performance reasons.
 */
data class RecorderOptions(
    val audioInfo: AudioInfo = AudioInfo.Default,
    val computeVolumeLevel: Boolean = false,
) : AppPluginOptions
