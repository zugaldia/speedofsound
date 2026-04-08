package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.SHERPA_ONNX_ASR_MODELS_URL
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.models.voice.VoiceModelFile

val SUPPORTED_SHERPA_MOONSHINE_ASR_MODELS = mapOf(
    DEFAULT_ASR_SHERPA_MOONSHINE_MODEL_ID to VoiceModel(
        id = DEFAULT_ASR_SHERPA_MOONSHINE_MODEL_ID,
        name = "Moonshine Tiny (English only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 43L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-tiny-en-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-tiny-en-quantized-2026-02-27.tar.bz2",
            sha256sum = "9ec31b342d8fa3240c3b81b8f82e1cf7e3ac467c93ca5a999b741d5887164f8d"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.ENGLISH),
    ),
    "sherpa-onnx-moonshine-tiny-ja-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-tiny-ja-quantized-2026-02-27",
        name = "Moonshine Tiny (Japanese only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 69L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-tiny-ja-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-tiny-ja-quantized-2026-02-27.tar.bz2",
            sha256sum = "880305c9a6c33572ab269ff9731977ea42ca34c8cffcdd5d99558a9ea2b47cc2"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.JAPANESE),
    ),
    "sherpa-onnx-moonshine-tiny-ko-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-tiny-ko-quantized-2026-02-27",
        name = "Moonshine Tiny (Korean only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 69L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-tiny-ko-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-tiny-ko-quantized-2026-02-27.tar.bz2",
            sha256sum = "d3b6c5390a7859c9ef20ff4f20b0766fcbad1dc06c0f509fe4840a3a302112dc"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.KOREAN),
    ),
    "sherpa-onnx-moonshine-base-en-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-base-en-quantized-2026-02-27",
        name = "Moonshine Base (English only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 135L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-base-en-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-base-en-quantized-2026-02-27.tar.bz2",
            sha256sum = "43232c1d13013d37317163baec3135bd771a186a4356f28c889bab453bb0e891"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.ENGLISH),
    ),
    "sherpa-onnx-moonshine-base-ja-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-base-ja-quantized-2026-02-27",
        name = "Moonshine Base (Japanese only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 135L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-base-ja-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-base-ja-quantized-2026-02-27.tar.bz2",
            sha256sum = "c66eb87d229a52d43bbe3b7c52a1d23f6208c0fd7c2e81802ecbda83b192798a"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.JAPANESE),
    ),
    "sherpa-onnx-moonshine-base-es-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-base-es-quantized-2026-02-27",
        name = "Moonshine Base (Spanish only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 63L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-base-es-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-base-es-quantized-2026-02-27.tar.bz2",
            sha256sum = "850c3dcc5dfccc8b1feb10bb221b11d6039b6f5c626241729f46863771016383"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.SPANISH),
    ),
    "sherpa-onnx-moonshine-base-zh-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-base-zh-quantized-2026-02-27",
        name = "Moonshine Base (Chinese only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 135L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-base-zh-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-base-zh-quantized-2026-02-27.tar.bz2",
            sha256sum = "6495d4240f66bcdd2bbbbfefaa687c463150d467854001270934e88940b733c1"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.CHINESE),
    ),
    "sherpa-onnx-moonshine-base-vi-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-base-vi-quantized-2026-02-27",
        name = "Moonshine Base (Vietnamese only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 135L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-base-vi-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-base-vi-quantized-2026-02-27.tar.bz2",
            sha256sum = "97b53f5fb75a2a9dd6327440a34f45fc06c927ed2fa7217e5ebe54058e269416"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.VIETNAMESE),
    ),
    "sherpa-onnx-moonshine-base-uk-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-base-uk-quantized-2026-02-27",
        name = "Moonshine Base (Ukrainian only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 135L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-base-uk-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-base-uk-quantized-2026-02-27.tar.bz2",
            sha256sum = "d8489b159afabf4cfb734002ae8a5361781ff8a381fa18d284d32d1eeb1c0376"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.UKRAINIAN),
    ),
    "sherpa-onnx-moonshine-base-ar-quantized-2026-02-27" to VoiceModel(
        id = "sherpa-onnx-moonshine-base-ar-quantized-2026-02-27",
        name = "Moonshine Base (Arabic only)",
        provider = AsrProvider.SHERPA_MOONSHINE,
        dataSizeMegabytes = 135L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-moonshine-base-ar-quantized-2026-02-27",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-moonshine-base-ar-quantized-2026-02-27.tar.bz2",
            sha256sum = "4d3f16fe354d8536d3323d3eaf4c12fafb1976b5dc0a12862ab25934da94285b"
        ),
        components = listOf(
            VoiceModelFile(name = "encoder_model.ort"),
            VoiceModelFile(name = "decoder_model_merged.ort"),
            VoiceModelFile(name = "tokens.txt")
        ),
        languages = listOf(Language.ARABIC),
    ),
)
