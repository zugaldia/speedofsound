package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.plugins.AppPlugin

/**
 * Base class for ASR plugins.
 *
 * Provides a common interface for different ASR implementations (Sherpa ONNX, ONNX Runtime).
 */
abstract class AsrPlugin<Options : AsrPluginOptions>(initialOptions: Options) : AppPlugin<Options>(initialOptions) {
    abstract fun transcribe(request: AsrRequest): Result<AsrResponse>
}
