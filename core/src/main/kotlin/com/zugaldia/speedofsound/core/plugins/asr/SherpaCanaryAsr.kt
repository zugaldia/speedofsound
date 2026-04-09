package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineCanaryModelConfig
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import java.nio.file.Path

class SherpaCanaryAsr(
    options: SherpaCanaryAsrOptions = SherpaCanaryAsrOptions(),
) : SherpaOfflineAsr<SherpaCanaryAsrOptions>(initialOptions = options) {
    override val id: String = ID

    companion object {
        const val ID = "ASR_SHERPA_CANARY"
    }

    override fun supportedModels(): Map<String, VoiceModel> = SUPPORTED_SHERPA_CANARY_ASR_MODELS

    override fun applyModelSpecificConfig(
        builder: OfflineModelConfig.Builder,
        model: VoiceModel,
        modelPath: Path,
        language: Language,
    ): OfflineModelConfig.Builder {
        val encoder = modelPath.resolve(model.components[0].name).toString()
        val decoder = modelPath.resolve(model.components[1].name).toString()
        val canary = OfflineCanaryModelConfig.builder()
            .setEncoder(encoder)
            .setDecoder(decoder)
            .setSrcLang(language.iso2)
            .setTgtLang(language.iso2)
            .setUsePnc(true)
            .build()
        return builder.setCanary(canary)
    }
}
