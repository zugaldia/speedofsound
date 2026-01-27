package com.zugaldia.speedofsound.core.plugins.recorder

import com.zugaldia.speedofsound.core.plugins.AppPluginState

/**
 * State of the JVM audio recorder.
 */
data class RecorderState(
    val isRecording: Boolean = false,
) : AppPluginState
