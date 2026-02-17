package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Supported ASR providers.
 */
enum class AsrProvider(val displayName: String) {
    ONNX_WHISPER("Whisper (ONNX)"),
    OPENAI("OpenAI"),
    SHERPA_WHISPER("Whisper (Sherpa)");
}

/**
 * Base interface for ASR plugin options.
 * All implementations must override these common fields with their specific defaults.
 */
interface AsrPluginOptions : AppPluginOptions {
    val modelId: String
    val language: Language
    val enableDebug: Boolean
}

/**
 * Maps an AsrProvider to the corresponding plugin ID.
 */
fun pluginIdForProvider(provider: AsrProvider): String = when (provider) {
    AsrProvider.ONNX_WHISPER -> OnnxWhisperAsr.ID
    AsrProvider.OPENAI -> OpenAiAsr.ID
    AsrProvider.SHERPA_WHISPER -> SherpaWhisperAsr.ID
}

/**
 * Returns the supported voice models for the given ASR provider.
 *
 * @param provider the ASR provider
 * @return a map of model IDs to VoiceModel objects
 */
fun getModelsForProvider(provider: AsrProvider): Map<String, VoiceModel> {
    return when (provider) {
        AsrProvider.ONNX_WHISPER -> SUPPORTED_ONNX_WHISPER_ASR_MODELS
        AsrProvider.OPENAI -> SUPPORTED_OPENAI_ASR_MODELS
        AsrProvider.SHERPA_WHISPER -> SUPPORTED_SHERPA_WHISPER_ASR_MODELS
    }
}
