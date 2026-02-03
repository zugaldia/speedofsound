package com.zugaldia.speedofsound.core.plugins.director

import com.zugaldia.speedofsound.core.plugins.AppPluginEvent

/**
 * Events emitted by the DirectorPlugin during the recording -> transcription -> polishing pipeline.
 */
sealed class DirectorEvent : AppPluginEvent() {
    data object RecordingStarted : DirectorEvent()
    data object TranscriptionStarted : DirectorEvent()
    data object PolishingStarted : DirectorEvent()
    data class PipelineCompleted(val rawTranscription: String, val polishedText: String) : DirectorEvent()
    data class PipelineError(val stage: PipelineStage, val error: Throwable) : DirectorEvent()
    data object PipelineCancelled : DirectorEvent()
}

/**
 * Stages in the recording pipeline where errors can occur.
 */
enum class PipelineStage {
    RECORDING,
    TRANSCRIPTION,
    POLISHING
}
