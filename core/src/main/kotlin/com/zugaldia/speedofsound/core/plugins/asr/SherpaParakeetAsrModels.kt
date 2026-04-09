package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.SHERPA_ONNX_ASR_MODELS_URL
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.models.voice.VoiceModelFile

val SUPPORTED_SHERPA_PARAKEET_ASR_MODELS = mapOf(
    DEFAULT_ASR_SHERPA_PARAKEET_MODEL_ID to VoiceModel(
        id = DEFAULT_ASR_SHERPA_PARAKEET_MODEL_ID,
        name = "Parakeet TDT 0.6B (25 EU languages)",
        provider = AsrProvider.SHERPA_PARAKEET,
        dataSizeMegabytes = 640L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-nemo-parakeet-tdt-0.6b-v3-int8",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-nemo-parakeet-tdt-0.6b-v3-int8.tar.bz2",
            sha256sum = "5793d0fd397c5778d2cf2126994d58e9d56b1be7c04d13c7a15bb1b4eafb16bf"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder.int8.onnx"),
            VoiceModelFile(name = "decoder.int8.onnx"),
            VoiceModelFile(name = "joiner.int8.onnx"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(
            Language.BULGARIAN,
            Language.CROATIAN,
            Language.CZECH,
            Language.DANISH,
            Language.DUTCH,
            Language.ENGLISH,
            Language.ESTONIAN,
            Language.FINNISH,
            Language.FRENCH,
            Language.GERMAN,
            Language.MODERN_GREEK,
            Language.HUNGARIAN,
            Language.ITALIAN,
            Language.LATVIAN,
            Language.LITHUANIAN,
            Language.MALTESE,
            Language.POLISH,
            Language.PORTUGUESE,
            Language.ROMANIAN,
            Language.RUSSIAN,
            Language.SLOVAK,
            Language.SLOVENIAN,
            Language.SPANISH,
            Language.SWEDISH,
            Language.UKRAINIAN,
        ),
    ),
    "sherpa-onnx-nemo-parakeet-tdt_ctc-0.6b-ja-35000-int8" to VoiceModel(
        id = "sherpa-onnx-nemo-parakeet-tdt_ctc-0.6b-ja-35000-int8",
        name = "Parakeet TDT-CTC 0.6B (Japanese only)",
        provider = AsrProvider.SHERPA_PARAKEET,
        dataSizeMegabytes = 626L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-nemo-parakeet-tdt_ctc-0.6b-ja-35000-int8",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-nemo-parakeet-tdt_ctc-0.6b-ja-35000-int8.tar.bz2",
            sha256sum = "4b0a800ef29f4f4c8667339bf6f60d5bfdc2852ddc9dc5741aea65b6f8d1306b"
        ),
        components = listOf(
            VoiceModelFile(name = "model.int8.onnx"),
            VoiceModelFile(name = "tokens.txt"),
        ),
        languages = listOf(Language.JAPANESE),
    ),
    "sherpa-onnx-nemo-parakeet_tdt_transducer_110m-en-36000" to VoiceModel(
        id = "sherpa-onnx-nemo-parakeet_tdt_transducer_110m-en-36000",
        name = "Parakeet TDT 110M (English only)",
        provider = AsrProvider.SHERPA_PARAKEET,
        dataSizeMegabytes = 456L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-nemo-parakeet_tdt_transducer_110m-en-36000",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-nemo-parakeet_tdt_transducer_110m-en-36000.tar.bz2",
            sha256sum = "4cb81f605eb7bc6fe0d69cd9d4045161e1691a64c2b7d4f1e7d5099e1d3bc024"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder.onnx"),
            VoiceModelFile(name = "decoder.onnx"),
            VoiceModelFile(name = "joiner.onnx"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.ENGLISH),
    ),
    "sherpa-onnx-nemo-parakeet_tdt_ctc_110m-en-36000-int8" to VoiceModel(
        id = "sherpa-onnx-nemo-parakeet_tdt_ctc_110m-en-36000-int8",
        name = "Parakeet TDT-CTC 110M (English only)",
        provider = AsrProvider.SHERPA_PARAKEET,
        dataSizeMegabytes = 126L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-nemo-parakeet_tdt_ctc_110m-en-36000-int8",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-nemo-parakeet_tdt_ctc_110m-en-36000-int8.tar.bz2",
            sha256sum = "17f945007b52ccd8b7200ffc7c5652e9e8e961dfdf479cefcabd06cf5703630b"
        ),
        components = listOf(
            VoiceModelFile(name = "model.int8.onnx"),
            VoiceModelFile(name = "tokens.txt"),
        ),
        languages = listOf(Language.ENGLISH),
    )
)
