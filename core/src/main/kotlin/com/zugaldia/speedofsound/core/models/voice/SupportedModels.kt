package com.zugaldia.speedofsound.core.models.voice

import com.zugaldia.speedofsound.core.Language

/*
 * We support all the Whisper models for the initial walking skeleton, after we'll include some other options. See:
 * https://github.com/k2-fsa/sherpa-onnx/releases/tag/asr-models
 *
 * Currently on the radar:
 * - NVIDIA Canary & Parakeet
 * - Useful Sensors Moonshine
 * - Meta Omnilingual
 */

// Bundled with the JAR under core/src/main/resources/models/asr
const val DEFAULT_ASR_MODEL_ID = "sherpa-onnx-whisper-tiny"
private const val SHERPA_ONNX_ASR_MODELS_URL = "https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models"

val SUPPORTED_ASR_MODELS = mapOf(
    "sherpa-onnx-whisper-tiny" to VoiceModel(
        id = "sherpa-onnx-whisper-tiny",
        name = "OpenAI Whisper Tiny",
        encoder = "tiny-encoder.int8.onnx",
        decoder = "tiny-decoder.int8.onnx",
        tokens = "tiny-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-tiny.tar.bz2",
        archiveSha256sum = "c46116994e539aa165266d96b325252728429c12535eb9d8b6a2b10f129e66b1",
        dataSizeMegabytes = 99L
    ),
    "sherpa-onnx-whisper-tiny.en" to VoiceModel(
        id = "sherpa-onnx-whisper-tiny.en",
        name = "OpenAI Whisper Tiny (English only)",
        encoder = "tiny.en-encoder.int8.onnx",
        decoder = "tiny.en-decoder.int8.onnx",
        tokens = "tiny.en-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-tiny.en.tar.bz2",
        archiveSha256sum = "2bd6cf965c8bb3e068ef9fa2191387ee63a9dfa2a4e37582a8109641c20005dd",
        dataSizeMegabytes = 99L,
        languages = listOf(Language.ENGLISH)
    ),
    "sherpa-onnx-whisper-base" to VoiceModel(
        id = "sherpa-onnx-whisper-base",
        name = "OpenAI Whisper Base",
        encoder = "base-encoder.int8.onnx",
        decoder = "base-decoder.int8.onnx",
        tokens = "base-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-base.tar.bz2",
        archiveSha256sum = "911b2083efd7c0dca2ac3b358b75222660dc09fb716d64fbfc417ba6c99ff3de",
        dataSizeMegabytes = 154L
    ),
    "sherpa-onnx-whisper-base.en" to VoiceModel(
        id = "sherpa-onnx-whisper-base.en",
        name = "OpenAI Whisper Base (English only)",
        encoder = "base.en-encoder.int8.onnx",
        decoder = "base.en-decoder.int8.onnx",
        tokens = "base.en-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-base.en.tar.bz2",
        archiveSha256sum = "475bc7052ce299c007f6d5d5407ba8601f819a2867f6eecee510ed17df581542",
        dataSizeMegabytes = 154L,
        languages = listOf(Language.ENGLISH)
    ),
    "sherpa-onnx-whisper-small" to VoiceModel(
        id = "sherpa-onnx-whisper-small",
        name = "OpenAI Whisper Small",
        encoder = "small-encoder.int8.onnx",
        decoder = "small-decoder.int8.onnx",
        tokens = "small-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-small.tar.bz2",
        archiveSha256sum = "486a46afbb7ba798507190ffe02fea2dd726049af212e774537efac6afb210a6",
        dataSizeMegabytes = 359L
    ),
    "sherpa-onnx-whisper-small.en" to VoiceModel(
        id = "sherpa-onnx-whisper-small.en",
        name = "OpenAI Whisper Small (English only)",
        encoder = "small.en-encoder.int8.onnx",
        decoder = "small.en-decoder.int8.onnx",
        tokens = "small.en-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-small.en.tar.bz2",
        archiveSha256sum = "0cdba2b8aaab69e04847f3427cc9709574112e67913a1a84b7fec3a8729faa9a",
        dataSizeMegabytes = 359L,
        languages = listOf(Language.ENGLISH)
    ),
    "sherpa-onnx-whisper-medium" to VoiceModel(
        id = "sherpa-onnx-whisper-medium",
        name = "OpenAI Whisper Medium",
        encoder = "medium-encoder.int8.onnx",
        decoder = "medium-decoder.int8.onnx",
        tokens = "medium-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-medium.tar.bz2",
        archiveSha256sum = "614b1172557049069d846c29d9399640bce83a4dd6c580decebd9ce2a4f32c33",
        dataSizeMegabytes = 903L
    ),
    "sherpa-onnx-whisper-medium.en" to VoiceModel(
        id = "sherpa-onnx-whisper-medium.en",
        name = "OpenAI Whisper Medium (English only)",
        encoder = "medium.en-encoder.int8.onnx",
        decoder = "medium.en-decoder.int8.onnx",
        tokens = "medium.en-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-medium.en.tar.bz2",
        archiveSha256sum = "73d95c169a410b5f23a79f8901374b26e0a16a09ea7f02b5e1db983f4cdfdd67",
        dataSizeMegabytes = 903L,
        languages = listOf(Language.ENGLISH)
    ),
    "sherpa-onnx-whisper-large-v3" to VoiceModel(
        id = "sherpa-onnx-whisper-large-v3",
        name = "OpenAI Whisper Large v3",
        encoder = "large-v3-encoder.int8.onnx",
        decoder = "large-v3-decoder.int8.onnx",
        tokens = "large-v3-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-large-v3.tar.bz2",
        archiveSha256sum = "2d0e134b3b5fc4a0533baf24a0c9d473b629aa47f030af0a165a05f461df7a03",
        dataSizeMegabytes = 1694L
    ),
    "sherpa-onnx-whisper-turbo" to VoiceModel(
        id = "sherpa-onnx-whisper-turbo",
        name = "OpenAI Whisper Turbo",
        encoder = "turbo-encoder.int8.onnx",
        decoder = "turbo-decoder.int8.onnx",
        tokens = "turbo-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-turbo.tar.bz2",
        archiveSha256sum = "b11acbbcd660b44a8e0df33724feb5aaa709cf65668f2823d59f656312544f22",
        dataSizeMegabytes = 989L
    ),
    "sherpa-onnx-whisper-distil-small.en" to VoiceModel(
        id = "sherpa-onnx-whisper-distil-small.en",
        name = "OpenAI Whisper Small (English only, distilled)",
        encoder = "distil-small.en-encoder.int8.onnx",
        decoder = "distil-small.en-decoder.int8.onnx",
        tokens = "distil-small.en-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-distil-small.en.tar.bz2",
        archiveSha256sum = "1483a4ddd62ea2e892a366262740f4dd5e24fc6effecbdf277c5b2d34600f7f1",
        dataSizeMegabytes = 286L,
        languages = listOf(Language.ENGLISH)
    ),
    "sherpa-onnx-whisper-distil-medium.en" to VoiceModel(
        id = "sherpa-onnx-whisper-distil-medium.en",
        name = "OpenAI Whisper Medium (English only, distilled)",
        encoder = "distil-medium.en-encoder.int8.onnx",
        decoder = "distil-medium.en-decoder.int8.onnx",
        tokens = "distil-medium.en-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-distil-medium.en.tar.bz2",
        archiveSha256sum = "e35029f7196c3ea22b1c38f6ddadbf64699ce3dbff49ec20c7394c1bd03a4d02",
        dataSizeMegabytes = 547L,
        languages = listOf(Language.ENGLISH)
    ),
    "sherpa-onnx-whisper-distil-large-v3.5" to VoiceModel(
        id = "sherpa-onnx-whisper-distil-large-v3.5",
        name = "OpenAI Whisper Large v3.5 (English only, distilled)",
        encoder = "distil-large-v3.5-encoder.int8.onnx",
        decoder = "distil-large-v3.5-decoder.int8.onnx",
        tokens = "distil-large-v3.5-tokens.txt",
        archiveUrl = "$SHERPA_ONNX_ASR_MODELS_URL/sherpa-onnx-whisper-distil-large-v3.5.tar.bz2",
        archiveSha256sum = "ec874c7346d24ef8063e05430ede616d66d80a410360283099d0bdf659187b1d",
        dataSizeMegabytes = 939L,
        languages = listOf(Language.ENGLISH)
    )
)
