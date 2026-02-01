package com.zugaldia.speedofsound.core.plugins.llm

import com.anthropic.models.messages.Model

const val ANTHROPIC_ENVIRONMENT_VARIABLE = "ANTHROPIC_API_KEY"
val DEFAULT_ANTHROPIC_MODEL_ID = Model.CLAUDE_SONNET_4_5.asString()

data class AnthropicLlmOptions(
    override val baseUrl: String? = null,
    override val apiKey: String? = null,
    override val model: String? = DEFAULT_ANTHROPIC_MODEL_ID,
    val maxTokens: Long = 1024L,
) : LlmPluginOptions
