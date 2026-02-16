package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE

// Bundled with the JAR under core/src/main/resources/models/asr
const val DEFAULT_ASR_SHERPA_MODEL_ID = "sherpa-onnx-whisper-tiny"

/**
 * Options for configuring the Sherpa ASR plugin.
 */
data class SherpaAsrOptions(
    val modelID: String = DEFAULT_ASR_SHERPA_MODEL_ID,
    val language: Language = DEFAULT_LANGUAGE,
    val enableDebug: Boolean = false,
) : AsrPluginOptions
