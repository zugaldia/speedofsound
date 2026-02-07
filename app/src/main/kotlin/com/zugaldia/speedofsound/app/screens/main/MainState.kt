package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.SIGNAL_LANGUAGE_CHANGED
import com.zugaldia.speedofsound.app.SIGNAL_PIPELINE_COMPLETED
import com.zugaldia.speedofsound.app.SIGNAL_PORTALS_RESTORE_TOKEN_MISSING
import com.zugaldia.speedofsound.app.SIGNAL_RECORDING_LEVEL
import com.zugaldia.speedofsound.app.SIGNAL_STAGE_CHANGED
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import org.gnome.gobject.GObject
import org.javagi.gobject.annotations.Signal

enum class AppStage {
    LOADING,
    IDLE,
    LISTENING,
    TRANSCRIBING,
    POLISHING
}

class MainState : GObject() {
    private var stageOrdinal: Int = AppStage.LOADING.ordinal
    private var maxRecordingLevel: Float = MIN_RECORDING_LEVEL
    private var portalsRestoreTokenMissing: Boolean = true
    private var currentLanguage: Language = DEFAULT_LANGUAGE

    fun currentStage(): AppStage = AppStage.entries[stageOrdinal]

    fun updateStage(value: AppStage) {
        stageOrdinal = value.ordinal
        if (value == AppStage.IDLE) { maxRecordingLevel = MIN_RECORDING_LEVEL } // Reset the max recording level
        emit(SIGNAL_STAGE_CHANGED, value.ordinal)
    }

    fun updateRecordingLevel(level: Float) {
        if (level > maxRecordingLevel) { maxRecordingLevel = level }
        val scaledLevel = level / maxRecordingLevel
        emit(SIGNAL_RECORDING_LEVEL, scaledLevel.toDouble())
    }

    fun updatePortalsRestoreTokenMissing(value: Boolean) {
        portalsRestoreTokenMissing = value
        emit(SIGNAL_PORTALS_RESTORE_TOKEN_MISSING, value)
    }

    fun emitPipelineCompleted() {
        emit(SIGNAL_PIPELINE_COMPLETED)
    }

    fun currentLanguage(): Language = currentLanguage

    fun updateLanguage(value: Language) {
        currentLanguage = value
        emit(SIGNAL_LANGUAGE_CHANGED, value.name)
    }

    @Signal(name = SIGNAL_STAGE_CHANGED)
    fun interface StageChanged {
        fun run(stageOrdinal: Int)
    }

    @Signal(name = SIGNAL_RECORDING_LEVEL)
    fun interface RecordingLevelChanged {
        fun run(level: Double)
    }

    @Signal(name = SIGNAL_PORTALS_RESTORE_TOKEN_MISSING)
    fun interface PortalsRestoreTokenMissingChanged {
        fun run(missing: Boolean)
    }

    @Signal(name = SIGNAL_PIPELINE_COMPLETED)
    fun interface PipelineCompleted {
        fun run()
    }

    @Signal(name = SIGNAL_LANGUAGE_CHANGED)
    fun interface LanguageChanged {
        fun run(languageName: String)
    }

    companion object {
        private const val MIN_RECORDING_LEVEL = 0.01f

        fun stageFromOrdinal(ordinal: Int): AppStage = AppStage.entries[ordinal]
    }
}
