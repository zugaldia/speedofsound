package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Supported ASR providers.
 */
enum class AsrProvider(val displayName: String) {
    ONNX("Local (ONNX)"),
    OPENAI("OpenAI (Cloud)"),
    SHERPA("Whisper (Local)");
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
    AsrProvider.ONNX -> OnnxAsr.ID
    AsrProvider.OPENAI -> OpenAiAsr.ID
    AsrProvider.SHERPA -> SherpaAsr.ID
}

/**
 * Returns the supported voice models for the given ASR provider.
 *
 * @param provider the ASR provider
 * @return a map of model IDs to VoiceModel objects
 */
fun getModelsForProvider(provider: AsrProvider): Map<String, VoiceModel> {
    return when (provider) {
        AsrProvider.ONNX -> SUPPORTED_ONNX_ASR_MODELS
        AsrProvider.OPENAI -> SUPPORTED_OPENAI_ASR_MODELS
        AsrProvider.SHERPA -> SUPPORTED_SHERPA_ASR_MODELS
    }
}
