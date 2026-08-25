package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.plugins.director.WHISPER_MAX_RECORDING_DURATION_MS

/**
 * Whether a speech recognition backend can only transcribe the first 30 seconds of a recording.
 *
 * The Sherpa ONNX offline Whisper recognizer truncates anything past that, it is a limitation of the model
 * itself. Transducer models (Parakeet), Canary, and cloud providers accept longer audio.
 */
fun isLimitedToThirtySeconds(provider: AsrProvider?): Boolean = provider == AsrProvider.SHERPA_WHISPER

/**
 * Caps a configured recording timeout to what the active backend can actually transcribe, so that we never
 * record audio that would be silently discarded.
 */
fun clampRecordingDurationMs(configuredMs: Long, provider: AsrProvider?): Long =
    if (isLimitedToThirtySeconds(provider)) {
        minOf(configuredMs, WHISPER_MAX_RECORDING_DURATION_MS)
    } else {
        configuredMs
    }
