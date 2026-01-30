package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.plugins.AppPluginState

/**
 * State of the ASR plugin.
 */
data class AsrState(
    val isProcessing: Boolean = false,
) : AppPluginState
