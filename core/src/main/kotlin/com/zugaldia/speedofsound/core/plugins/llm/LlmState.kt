package com.zugaldia.speedofsound.core.plugins.llm

import com.zugaldia.speedofsound.core.plugins.AppPluginState

/**
 * State of the LLM plugin.
 */
data class LlmState(
    val isProcessing: Boolean = false,
) : AppPluginState
