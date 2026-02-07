package com.zugaldia.speedofsound.core.plugins.llm

const val GOOGLE_ENVIRONMENT_VARIABLE = "GOOGLE_API_KEY"
const val DEFAULT_GOOGLE_MODEL_ID = "gemini-2.5-flash"

data class GoogleLlmOptions(
    override val baseUrl: String? = null,
    override val apiKey: String? = null,
    override val model: String? = DEFAULT_GOOGLE_MODEL_ID,
) : LlmPluginOptions
