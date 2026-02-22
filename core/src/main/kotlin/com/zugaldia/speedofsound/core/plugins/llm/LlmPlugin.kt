package com.zugaldia.speedofsound.core.plugins.llm

import com.zugaldia.speedofsound.core.models.text.TextModel
import com.zugaldia.speedofsound.core.plugins.AppPlugin

/**
 * Base class for LLM plugins.
 *
 * Provides a common interface for different LLM providers (Anthropic, Google, OpenAI).
 */
abstract class LlmPlugin<Options : LlmPluginOptions>(initialOptions: Options) : AppPlugin<Options>(initialOptions) {
    abstract fun generate(request: LlmRequest): Result<LlmResponse>

    /**
     * Lists available models from the LLM provider.
     *
     * @return Result containing a list of available TextModel instances, or an error if the operation fails
     */
    abstract fun listModels(): Result<List<TextModel>>
}
