package com.zugaldia.speedofsound.core.plugins.llm

import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Supported LLM providers.
 */
enum class LlmProvider {
    ANTHROPIC,
    GOOGLE,
    OPENAI;

    companion object {
        fun fromString(value: String): LlmProvider {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown LLM provider: $value")
        }
    }
}

/**
 * Base interface for LLM plugin options.
 */
interface LlmPluginOptions : AppPluginOptions {
    val baseUrl: String?
    val apiKey: String?
    val model: String?
}
