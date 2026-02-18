package com.zugaldia.speedofsound.core.plugins.llm

import com.anthropic.models.messages.Model
import com.zugaldia.speedofsound.core.models.text.TextModel

val DEFAULT_LLM_ANTHROPIC_MODEL_ID = Model.CLAUDE_SONNET_4_5.asString()

val SUPPORTED_ANTHROPIC_TEXT_MODELS = mapOf(
    Model.CLAUDE_OPUS_4_5.asString() to TextModel(
        id = Model.CLAUDE_OPUS_4_5.asString(),
        name = "Claude Opus 4.5",
        provider = LlmProvider.ANTHROPIC
    ),
    Model.CLAUDE_SONNET_4_5.asString() to TextModel(
        id = Model.CLAUDE_SONNET_4_5.asString(),
        name = "Claude Sonnet 4.5",
        provider = LlmProvider.ANTHROPIC
    ),
    Model.CLAUDE_HAIKU_4_5.asString() to TextModel(
        id = Model.CLAUDE_HAIKU_4_5.asString(),
        name = "Claude Haiku 4.5",
        provider = LlmProvider.ANTHROPIC
    ),
)

data class AnthropicLlmOptions(
    override val baseUrl: String? = null,
    override val apiKey: String? = null,
    override val modelId: String = DEFAULT_LLM_ANTHROPIC_MODEL_ID,
    val maxTokens: Long = 1024L,
) : LlmPluginOptions {
    companion object {
        val Default = AnthropicLlmOptions()
    }
}
