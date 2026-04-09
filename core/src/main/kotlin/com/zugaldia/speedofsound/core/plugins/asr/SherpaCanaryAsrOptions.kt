package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE

const val DEFAULT_ASR_SHERPA_CANARY_MODEL_ID = "sherpa-onnx-nemo-canary-180m-flash-en-es-de-fr-int8"

/**
 * Options for configuring the Sherpa Canary ASR plugin.
 */
data class SherpaCanaryAsrOptions(
    override val modelId: String = DEFAULT_ASR_SHERPA_CANARY_MODEL_ID,
    override val language: Language = DEFAULT_LANGUAGE,
    override val enableDebug: Boolean = false,
) : AsrPluginOptions
