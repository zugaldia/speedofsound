package com.zugaldia.speedofsound.core.plugins.llm

import com.zugaldia.speedofsound.core.plugins.AppPlugin

/**
 * Base class for LLM plugins.
 *
 * Provides a common interface for different LLM providers (Anthropic, Google, OpenAI).
 */
abstract class LlmPlugin<Options : LlmPluginOptions>(
    initialOptions: Options,
    initialState: LlmState = LlmState(),
) : AppPlugin<LlmState, Options>(initialOptions, initialState) {

    /**
     * Sends a prompt to the LLM and returns the response.
     */
    abstract fun generate(request: LlmRequest): Result<LlmResponse>
}
