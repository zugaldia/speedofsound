package com.zugaldia.speedofsound.core.plugins.llm

import com.zugaldia.speedofsound.core.plugins.AppPlugin

/**
 * Base class for LLM plugins.
 *
 * Provides a common interface for different LLM providers (Anthropic, Google, OpenAI).
 */
abstract class LlmPlugin<Options : LlmPluginOptions>(initialOptions: Options) : AppPlugin<Options>(initialOptions) {
    abstract fun generate(request: LlmRequest): Result<LlmResponse>
}
