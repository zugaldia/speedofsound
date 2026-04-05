package com.zugaldia.speedofsound.core.models.voice

import com.zugaldia.speedofsound.core.plugins.asr.DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.asr.SUPPORTED_SHERPA_MOONSHINE_ASR_MODELS
import com.zugaldia.speedofsound.core.plugins.asr.SUPPORTED_SHERPA_WHISPER_ASR_MODELS

/**
 * Catalog for looking up voice models.
 */
interface VoiceModelCatalog {
    fun getModel(modelId: String): VoiceModel?
    fun getDefaultModelId(): String
}

/**
 * Production implementation that uses the global model maps.
 */
class DefaultVoiceModelCatalog : VoiceModelCatalog {
    override fun getModel(modelId: String): VoiceModel? {
        return SUPPORTED_SHERPA_WHISPER_ASR_MODELS[modelId]
            ?: SUPPORTED_SHERPA_MOONSHINE_ASR_MODELS[modelId]
    }

    override fun getDefaultModelId(): String {
        return DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
    }
}
