package com.zugaldia.speedofsound.core.plugins.asr

import com.openai.models.audio.AudioModel
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.models.voice.VoiceModel

val DEFAULT_ASR_OPENAI_MODEL_ID = AudioModel.GPT_4O_TRANSCRIBE.asString()

val SUPPORTED_OPENAI_ASR_MODELS = mapOf(
    AudioModel.GPT_4O_TRANSCRIBE.asString() to VoiceModel(
        id = AudioModel.GPT_4O_TRANSCRIBE.asString(),
        name = "GPT-4o Transcribe"
    ),
    AudioModel.GPT_4O_MINI_TRANSCRIBE.asString() to VoiceModel(
        id = AudioModel.GPT_4O_MINI_TRANSCRIBE.asString(),
        name = "GPT-4o Transcribe Mini"
    ),
    AudioModel.WHISPER_1.asString() to VoiceModel(
        id = AudioModel.WHISPER_1.asString(),
        name = "Whisper"
    )
)

data class OpenAiAsrOptions(
    override val modelId: String = DEFAULT_ASR_OPENAI_MODEL_ID,
    override val language: Language = DEFAULT_LANGUAGE,
    override val enableDebug: Boolean = false,
    val baseUrl: String? = null,
    val apiKey: String? = null,
    ) : AsrPluginOptions {
    companion object {
        val Default = OpenAiAsrOptions()
    }
}
