package com.zugaldia.speedofsound.core.plugins.llm

import com.openai.models.ChatModel

const val OPENAI_ENVIRONMENT_VARIABLE = "OPENAI_API_KEY"
val DEFAULT_OPENAI_MODEL_ID = ChatModel.GPT_5_2.asString()

data class OpenAiLlmOptions(
    override val baseUrl: String? = null,
    override val apiKey: String? = null,
    override val model: String? = DEFAULT_OPENAI_MODEL_ID,
) : LlmPluginOptions
