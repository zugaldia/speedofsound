package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Supported ASR providers.
 */
enum class AsrProvider(val displayName: String) {
    ONNX("Local (ONNX)"),
    SHERPA("Local (Sherpa)");
}

/**
 * Base interface for ASR plugin options.
 */
interface AsrPluginOptions : AppPluginOptions
