package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.models.voice.VoiceModelFile

const val DEFAULT_ASR_ONNX_WHISPER_MODEL_ID = "onnx_whisper_tiny_en"

val SUPPORTED_ONNX_WHISPER_ASR_MODELS = mapOf(
    DEFAULT_ASR_ONNX_WHISPER_MODEL_ID to VoiceModel(
        id = DEFAULT_ASR_ONNX_WHISPER_MODEL_ID,
        name = "OpenAI Whisper Tiny (English only)",
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 72L,
        components = listOf(
            VoiceModelFile(
                name = "whisper_cpu_int8_model.onnx",
                url = "https://github.com/microsoft/onnxruntime-inference-examples/raw/" +
                        "refs/heads/main/mobile/examples/whisper/local/android/app/src/main/res/raw/" +
                        "whisper_cpu_int8_model.onnx"
            )
        )
    )
)

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
