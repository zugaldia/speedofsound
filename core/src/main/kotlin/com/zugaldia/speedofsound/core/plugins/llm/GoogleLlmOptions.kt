package com.zugaldia.speedofsound.core.plugins.llm

import com.zugaldia.speedofsound.core.models.text.TextModel

const val DEFAULT_LLM_GOOGLE_MODEL_ID = "gemini-2.5-flash-lite"

val SUPPORTED_GOOGLE_TEXT_MODELS = mapOf(
    "gemini-3-pro-preview" to TextModel(
        id = "gemini-3-pro-preview",
        name = "Gemini 3 Pro Preview",
        provider = LlmProvider.GOOGLE
    ),
    "gemini-3-flash-preview" to TextModel(
        id = "gemini-3-flash-preview",
        name = "Gemini 3 Flash Preview",
        provider = LlmProvider.GOOGLE
    ),
    "gemini-2.5-pro" to TextModel(
        id = "gemini-2.5-pro",
        name = "Gemini 2.5 Pro",
        provider = LlmProvider.GOOGLE
    ),
    "gemini-2.5-flash" to TextModel(
        id = "gemini-2.5-flash",
        name = "Gemini 2.5 Flash",
        provider = LlmProvider.GOOGLE
    ),
    "gemini-2.5-flash-lite" to TextModel(
        id = "gemini-2.5-flash-lite",
        name = "Gemini 2.5 Flash Lite",
        provider = LlmProvider.GOOGLE
    ),
)

data class GoogleLlmOptions(
    override val baseUrl: String? = null,
    override val apiKey: String? = null,
    override val modelId: String = DEFAULT_LLM_GOOGLE_MODEL_ID,
) : LlmPluginOptions {
    companion object {
        val Default = GoogleLlmOptions()
    }
}
