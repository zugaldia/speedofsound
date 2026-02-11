package com.zugaldia.speedofsound.core.plugins.llm

import com.anthropic.models.messages.Model
import com.zugaldia.speedofsound.core.models.text.TextModel

const val ANTHROPIC_ENVIRONMENT_VARIABLE = "ANTHROPIC_API_KEY"
val DEFAULT_ANTHROPIC_MODEL_ID = Model.CLAUDE_SONNET_4_5.asString()

val SUPPORTED_ANTHROPIC_TEXT_MODELS = mapOf(
    Model.CLAUDE_OPUS_4_5.asString() to TextModel(
        id = Model.CLAUDE_OPUS_4_5.asString(),
        name = "Claude Opus 4.5"
    ),
    Model.CLAUDE_SONNET_4_5.asString() to TextModel(
        id = Model.CLAUDE_SONNET_4_5.asString(),
        name = "Claude Sonnet 4.5"
    ),
    Model.CLAUDE_HAIKU_4_5.asString() to TextModel(
        id = Model.CLAUDE_HAIKU_4_5.asString(),
        name = "Claude Haiku 4.5"
    ),
)

data class AnthropicLlmOptions(
    override val baseUrl: String? = null,
    override val apiKey: String? = null,
    override val model: String = DEFAULT_ANTHROPIC_MODEL_ID,
    val maxTokens: Long = 1024L,
) : LlmPluginOptions
