package com.zugaldia.speedofsound.core.plugins.asr

/**
 * Options for configuring the ONNX ASR plugin.
 *
 * Currently, this plugin uses a bundled model and doesn't require configuration,
 * but this options class is provided for consistency and future extensibility.
 */
data class OnnxAsrOptions(
    val enableDebug: Boolean = false,
) : AsrPluginOptions
