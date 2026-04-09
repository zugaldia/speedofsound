package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import com.zugaldia.speedofsound.core.FatalStartupException
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.voice.ModelManager
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import java.nio.file.Path

class SherpaWhisperAsr(
    options: SherpaWhisperAsrOptions = SherpaWhisperAsrOptions(),
) : SherpaOfflineAsr<SherpaWhisperAsrOptions>(initialOptions = options) {
    override val id: String = ID

    companion object {
        const val ID = "ASR_SHERPA_WHISPER"
    }

    override fun supportedModels(): Map<String, VoiceModel> = SUPPORTED_SHERPA_WHISPER_ASR_MODELS

    override fun applyModelSpecificConfig(
        builder: OfflineModelConfig.Builder,
        model: VoiceModel,
        modelPath: Path,
        language: Language,
    ): OfflineModelConfig.Builder {
        val encoder = modelPath.resolve(model.components[0].name).toString()
        val decoder = modelPath.resolve(model.components[1].name).toString()
        val whisper = OfflineWhisperModelConfig.builder()
            .setEncoder(encoder)
            .setDecoder(decoder)
            .setLanguage(language.iso2)
            .setTask("transcribe")
            .build()
        return builder.setWhisper(whisper)
    }

    override fun onBeforeCreateRecognizer(modelManager: ModelManager) {
        if (currentOptions.modelId == DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID) {
            modelManager.extractDefaultModel().onFailure { error ->
                if (error is FatalStartupException) throw error
                throw IllegalStateException("Failed to extract default model: ${error.message}", error)
            }
        }
    }
}
