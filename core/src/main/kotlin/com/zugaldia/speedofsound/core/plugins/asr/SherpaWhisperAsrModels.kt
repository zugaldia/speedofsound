package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import com.zugaldia.speedofsound.core.models.voice.VoiceModelFile

/*
 * We support all the Whisper models for the initial walking skeleton, after we'll include some other options. See:
 * https://github.com/k2-fsa/sherpa-onnx/releases/tag/asr-models
 *
 * Currently on the radar:
 * - Meta Omnilingual
 * - NVIDIA Canary & Parakeet
 * - Useful Sensors Moonshine (or use https://github.com/moonshine-ai/moonshine directly)
 */

private const val SHERPA_ONNX_ASR_MODELS_URL = "https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models"

val SUPPORTED_SHERPA_WHISPER_ASR_MODELS = mapOf(
    DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID to VoiceModel(
        id = DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID,
        name = "Whisper Tiny",
        provider = AsrProvider.SHERPA_WHISPER,
        dataSizeMegabytes = 99L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-tiny",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-tiny.tar.bz2",
            sha256sum = "c46116994e539aa165266d96b325252728429c12535eb9d8b6a2b10f129e66b1"
        ),
        components = listOf(
            VoiceModelFile(name = "tiny-encoder.int8.onnx"),
            VoiceModelFile(name = "tiny-decoder.int8.onnx"),
            VoiceModelFile(name = "tiny-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-tiny.en" to VoiceModel(
        id = "sherpa-onnx-whisper-tiny.en",
        name = "Whisper Tiny (English only)",
        provider = AsrProvider.SHERPA_WHISPER,
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 99L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-tiny.en",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-tiny.en.tar.bz2",
            sha256sum = "2bd6cf965c8bb3e068ef9fa2191387ee63a9dfa2a4e37582a8109641c20005dd"
        ),
        components = listOf(
            VoiceModelFile(name = "tiny.en-encoder.int8.onnx"),
            VoiceModelFile(name = "tiny.en-decoder.int8.onnx"),
            VoiceModelFile(name = "tiny.en-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-base" to VoiceModel(
        id = "sherpa-onnx-whisper-base",
        name = "Whisper Base",
        provider = AsrProvider.SHERPA_WHISPER,
        dataSizeMegabytes = 154L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-base",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-base.tar.bz2",
            sha256sum = "911b2083efd7c0dca2ac3b358b75222660dc09fb716d64fbfc417ba6c99ff3de"
        ),
        components = listOf(
            VoiceModelFile(name = "base-encoder.int8.onnx"),
            VoiceModelFile(name = "base-decoder.int8.onnx"),
            VoiceModelFile(name = "base-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-base.en" to VoiceModel(
        id = "sherpa-onnx-whisper-base.en",
        name = "Whisper Base (English only)",
        provider = AsrProvider.SHERPA_WHISPER,
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 154L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-base.en",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-base.en.tar.bz2",
            sha256sum = "475bc7052ce299c007f6d5d5407ba8601f819a2867f6eecee510ed17df581542"
        ),
        components = listOf(
            VoiceModelFile(name = "base.en-encoder.int8.onnx"),
            VoiceModelFile(name = "base.en-decoder.int8.onnx"),
            VoiceModelFile(name = "base.en-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-small" to VoiceModel(
        id = "sherpa-onnx-whisper-small",
        name = "Whisper Small",
        provider = AsrProvider.SHERPA_WHISPER,
        dataSizeMegabytes = 359L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-small",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-small.tar.bz2",
            sha256sum = "486a46afbb7ba798507190ffe02fea2dd726049af212e774537efac6afb210a6"
        ),
        components = listOf(
            VoiceModelFile(name = "small-encoder.int8.onnx"),
            VoiceModelFile(name = "small-decoder.int8.onnx"),
            VoiceModelFile(name = "small-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-small.en" to VoiceModel(
        id = "sherpa-onnx-whisper-small.en",
        name = "Whisper Small (English only)",
        provider = AsrProvider.SHERPA_WHISPER,
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 359L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-small.en",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-small.en.tar.bz2",
            sha256sum = "0cdba2b8aaab69e04847f3427cc9709574112e67913a1a84b7fec3a8729faa9a"
        ),
        components = listOf(
            VoiceModelFile(name = "small.en-encoder.int8.onnx"),
            VoiceModelFile(name = "small.en-decoder.int8.onnx"),
            VoiceModelFile(name = "small.en-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-medium" to VoiceModel(
        id = "sherpa-onnx-whisper-medium",
        name = "Whisper Medium",
        provider = AsrProvider.SHERPA_WHISPER,
        dataSizeMegabytes = 903L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-medium",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-medium.tar.bz2",
            sha256sum = "614b1172557049069d846c29d9399640bce83a4dd6c580decebd9ce2a4f32c33"
        ),
        components = listOf(
            VoiceModelFile(name = "medium-encoder.int8.onnx"),
            VoiceModelFile(name = "medium-decoder.int8.onnx"),
            VoiceModelFile(name = "medium-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-medium.en" to VoiceModel(
        id = "sherpa-onnx-whisper-medium.en",
        name = "Whisper Medium (English only)",
        provider = AsrProvider.SHERPA_WHISPER,
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 903L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-medium.en",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-medium.en.tar.bz2",
            sha256sum = "73d95c169a410b5f23a79f8901374b26e0a16a09ea7f02b5e1db983f4cdfdd67"
        ),
        components = listOf(
            VoiceModelFile(name = "medium.en-encoder.int8.onnx"),
            VoiceModelFile(name = "medium.en-decoder.int8.onnx"),
            VoiceModelFile(name = "medium.en-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-large-v3" to VoiceModel(
        id = "sherpa-onnx-whisper-large-v3",
        name = "Whisper Large v3",
        provider = AsrProvider.SHERPA_WHISPER,
        dataSizeMegabytes = 1694L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-large-v3",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-large-v3.tar.bz2",
            sha256sum = "2d0e134b3b5fc4a0533baf24a0c9d473b629aa47f030af0a165a05f461df7a03"
        ),
        components = listOf(
            VoiceModelFile(name = "large-v3-encoder.int8.onnx"),
            VoiceModelFile(name = "large-v3-decoder.int8.onnx"),
            VoiceModelFile(name = "large-v3-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-turbo" to VoiceModel(
        id = "sherpa-onnx-whisper-turbo",
        name = "Whisper Turbo",
        provider = AsrProvider.SHERPA_WHISPER,
        dataSizeMegabytes = 989L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-turbo",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-turbo.tar.bz2",
            sha256sum = "b11acbbcd660b44a8e0df33724feb5aaa709cf65668f2823d59f656312544f22"
        ),
        components = listOf(
            VoiceModelFile(name = "turbo-encoder.int8.onnx"),
            VoiceModelFile(name = "turbo-decoder.int8.onnx"),
            VoiceModelFile(name = "turbo-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-distil-small.en" to VoiceModel(
        id = "sherpa-onnx-whisper-distil-small.en",
        name = "Whisper Small (English only, optimized)",
        provider = AsrProvider.SHERPA_WHISPER,
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 286L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-distil-small.en",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-distil-small.en.tar.bz2",
            sha256sum = "1483a4ddd62ea2e892a366262740f4dd5e24fc6effecbdf277c5b2d34600f7f1"
        ),
        components = listOf(
            VoiceModelFile(name = "distil-small.en-encoder.int8.onnx"),
            VoiceModelFile(name = "distil-small.en-decoder.int8.onnx"),
            VoiceModelFile(name = "distil-small.en-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-distil-medium.en" to VoiceModel(
        id = "sherpa-onnx-whisper-distil-medium.en",
        name = "Whisper Medium (English only, optimized)",
        provider = AsrProvider.SHERPA_WHISPER,
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 547L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-distil-medium.en",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-distil-medium.en.tar.bz2",
            sha256sum = "e35029f7196c3ea22b1c38f6ddadbf64699ce3dbff49ec20c7394c1bd03a4d02"
        ),
        components = listOf(
            VoiceModelFile(name = "distil-medium.en-encoder.int8.onnx"),
            VoiceModelFile(name = "distil-medium.en-decoder.int8.onnx"),
            VoiceModelFile(name = "distil-medium.en-tokens.txt")
        )
    ),
    "sherpa-onnx-whisper-distil-large-v3.5" to VoiceModel(
        id = "sherpa-onnx-whisper-distil-large-v3.5",
        name = "Whisper Large v3.5 (English only, optimized)",
        provider = AsrProvider.SHERPA_WHISPER,
        languages = listOf(Language.ENGLISH),
        dataSizeMegabytes = 939L,
        archiveFile = VoiceModelFile(
            name = "sherpa-onnx-whisper-distil-large-v3.5",
            url = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-distil-large-v3.5.tar.bz2",
            sha256sum = "ec874c7346d24ef8063e05430ede616d66d80a410360283099d0bdf659187b1d"
        ),
        components = listOf(
            VoiceModelFile(name = "distil-large-v3.5-encoder.int8.onnx"),
            VoiceModelFile(name = "distil-large-v3.5-decoder.int8.onnx"),
            VoiceModelFile(name = "distil-large-v3.5-tokens.txt")
        )
    )
)
