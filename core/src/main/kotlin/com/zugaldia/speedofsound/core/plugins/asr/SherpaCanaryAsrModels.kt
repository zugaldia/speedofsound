package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.SHERPA_ONNX_ASR_MODELS_URL
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.models.voice.VoiceModelFile

val SUPPORTED_SHERPA_CANARY_ASR_MODELS = mapOf(
    DEFAULT_ASR_SHERPA_CANARY_MODEL_ID to VoiceModel(
        id = DEFAULT_ASR_SHERPA_CANARY_MODEL_ID,
        name = "Canary (English, Spanish, German, French)",
        provider = AsrProvider.SHERPA_CANARY,
        dataSizeMegabytes = 198L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-nemo-canary-180m-flash-en-es-de-fr-int8",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-nemo-canary-180m-flash-en-es-de-fr-int8.tar.bz2",
            sha256sum = "7a38ed8b13f014ad632b09ff8d22e0c6f1359dd046af9235d281dfae841b9ab9"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder.int8.onnx"),
            VoiceModelFile(name = "decoder.int8.onnx"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.ENGLISH, Language.SPANISH, Language.GERMAN, Language.FRENCH),
    )
)
