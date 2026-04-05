package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE

const val DEFAULT_ASR_SHERPA_MOONSHINE_MODEL_ID = "sherpa-onnx-moonshine-tiny-en-quantized-2026-02-27"

/**
 * Options for configuring the Sherpa Moonshine ASR plugin.
 */
data class SherpaMoonshineAsrOptions(
    override val modelId: String = DEFAULT_ASR_SHERPA_MOONSHINE_MODEL_ID,
    override val language: Language = DEFAULT_LANGUAGE,
    override val enableDebug: Boolean = false,
) : AsrPluginOptions
