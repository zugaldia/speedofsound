package com.zugaldia.speedofsound.cli.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.plugins.asr.AsrPluginOptions

/**
 * Default model ID for CLI-only ONNX Whisper implementation.
 * This is an experimental feature not available in the main application.
 */
const val DEFAULT_ASR_ONNX_WHISPER_MODEL_ID = "onnx_whisper_tiny_en"

/**
 * Options for configuring the ONNX Whisper ASR plugin.
 *
 * Currently, this plugin uses a bundled model and doesn't require configuration,
 * but this options class is provided for consistency and future extensibility.
 */
data class OnnxWhisperAsrOptions(
    override val modelId: String = DEFAULT_ASR_ONNX_WHISPER_MODEL_ID,
    override val language: Language = DEFAULT_LANGUAGE,
    override val enableDebug: Boolean = false,
) : AsrPluginOptions
