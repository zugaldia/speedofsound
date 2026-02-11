package com.zugaldia.speedofsound.core.plugins.llm

import com.zugaldia.speedofsound.core.models.text.TextModel
import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Supported LLM providers.
 */
enum class LlmProvider(val displayName: String) {
    ANTHROPIC("Anthropic"),
    GOOGLE("Google"),
    OPENAI("OpenAI");
}

/**
 * Base interface for LLM plugin options.
 */
interface LlmPluginOptions : AppPluginOptions {
    val model: String
    val apiKey: String?
    val baseUrl: String?
}

/**
 * Returns the supported text models for the given LLM provider.
 *
 * @param provider the LLM provider
 * @return a map of model IDs to TextModel objects
 */
fun getModelsForProvider(provider: LlmProvider): Map<String, TextModel> {
    return when (provider) {
        LlmProvider.ANTHROPIC -> SUPPORTED_ANTHROPIC_TEXT_MODELS
        LlmProvider.GOOGLE -> SUPPORTED_GOOGLE_TEXT_MODELS
        LlmProvider.OPENAI -> SUPPORTED_OPENAI_TEXT_MODELS
    }
}
