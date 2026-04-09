package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineNemoEncDecCtcModelConfig
import com.k2fsa.sherpa.onnx.OfflineTransducerModelConfig
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import java.nio.file.Path

class SherpaParakeetAsr(
    options: SherpaParakeetAsrOptions = SherpaParakeetAsrOptions(),
) : SherpaOfflineAsr<SherpaParakeetAsrOptions>(initialOptions = options) {
    override val id: String = ID

    companion object {
        const val ID = "ASR_SHERPA_PARAKEET"
        private const val TRANSDUCER_COMPONENT_COUNT = 4
    }

    override fun supportedModels(): Map<String, VoiceModel> = SUPPORTED_SHERPA_PARAKEET_ASR_MODELS

    override fun applyModelSpecificConfig(
        builder: OfflineModelConfig.Builder,
        model: VoiceModel,
        modelPath: Path,
        language: Language,
    ): OfflineModelConfig.Builder {
        return if (model.components.size == TRANSDUCER_COMPONENT_COUNT) {
            // Transducer model: encoder, decoder, joiner, tokens
            val transducer = OfflineTransducerModelConfig.builder()
                .setEncoder(modelPath.resolve(model.components[0].name).toString())
                .setDecoder(modelPath.resolve(model.components[1].name).toString())
                .setJoiner(modelPath.resolve(model.components[2].name).toString())
                .build()
            builder.setTransducer(transducer)
        } else {
            // CTC model: model, tokens
            val nemo = OfflineNemoEncDecCtcModelConfig.builder()
                .setModel(modelPath.resolve(model.components[0].name).toString())
                .build()
            builder.setNemo(nemo)
        }
    }
}
