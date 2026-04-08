package com.zugaldia.speedofsound.core.models.voice

import com.zugaldia.speedofsound.core.desktop.settings.SUPPORTED_LOCAL_ASR_MODELS
import com.zugaldia.speedofsound.core.plugins.asr.DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID

/**
 * Catalog for looking up voice models.
 */
interface VoiceModelCatalog {
    fun getModel(modelId: String): VoiceModel?
    fun getDefaultModelId(): String
}

/**
 * Production implementation that uses the global SUPPORTED_LOCAL_ASR_MODELS.
 */
class DefaultVoiceModelCatalog : VoiceModelCatalog {
    override fun getModel(modelId: String): VoiceModel? {
        return SUPPORTED_LOCAL_ASR_MODELS[modelId]
    }

    override fun getDefaultModelId(): String {
        return DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
    }
}
