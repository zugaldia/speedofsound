package com.zugaldia.speedofsound.core.plugins.llm

import com.openai.models.ChatModel
import com.zugaldia.speedofsound.core.models.text.TextModel

const val OPENAI_ENVIRONMENT_VARIABLE = "OPENAI_API_KEY"
val DEFAULT_OPENAI_MODEL_ID = ChatModel.GPT_5_2.asString()

val SUPPORTED_OPENAI_TEXT_MODELS = mapOf(
    ChatModel.GPT_5_2_PRO.asString() to TextModel(
        id = ChatModel.GPT_5_2_PRO.asString(),
        name = "GPT 5.2 Pro"
    ),
    ChatModel.GPT_5_2.asString() to TextModel(
        id = ChatModel.GPT_5_2.asString(),
        name = "GPT 5.2"
    ),
    ChatModel.GPT_5_1_MINI.asString() to TextModel(
        id = ChatModel.GPT_5_1_MINI.asString(),
        name = "GPT 5.1 Mini"
    ),
    ChatModel.GPT_5_NANO.asString() to TextModel(
        id = ChatModel.GPT_5_NANO.asString(),
        name = "GPT 5.0 Nano"
    ),
)

data class OpenAiLlmOptions(
    override val baseUrl: String? = null,
    override val apiKey: String? = null,
    override val model: String = DEFAULT_OPENAI_MODEL_ID,
) : LlmPluginOptions {
    companion object {
        val Default = OpenAiLlmOptions()
    }
}
