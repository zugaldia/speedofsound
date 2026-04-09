package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE

const val DEFAULT_ASR_SHERPA_PARAKEET_MODEL_ID = "sherpa-onnx-nemo-parakeet-tdt-0.6b-v3-int8"

/**
 * Options for configuring the Sherpa Parakeet ASR plugin.
 */
data class SherpaParakeetAsrOptions(
    override val modelId: String = DEFAULT_ASR_SHERPA_PARAKEET_MODEL_ID,
    override val language: Language = DEFAULT_LANGUAGE,
    override val enableDebug: Boolean = false,
) : AsrPluginOptions
