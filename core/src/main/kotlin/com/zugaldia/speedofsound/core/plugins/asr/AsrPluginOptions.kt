package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.plugins.AppPluginOptions
import com.zugaldia.speedofsound.core.plugins.SelectableProvider

/**
 * Supported ASR providers.
 *
 * @param displayName Human-readable name for the provider
 * @param isLocallyManaged Whether the provider uses models managed in the model library
 */
enum class AsrProvider(
    override val displayName: String,
    val isLocallyManaged: Boolean
) : SelectableProvider {
    ONNX_WHISPER("Whisper (ONNX)", isLocallyManaged = true),
    OPENAI("OpenAI", isLocallyManaged = false),
    SHERPA_WHISPER("Whisper (Local)", isLocallyManaged = true);

    companion object {
        /**
         * Returns only providers that are not locally managed (i.e., custom/remote providers).
         * Locally managed providers will be configured in the model library page.
         */
        fun getCustomProviders(): List<AsrProvider> = entries.filter { !it.isLocallyManaged }
    }
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
