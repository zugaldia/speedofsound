package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.SIGNAL_RECORDING_LEVEL
import com.zugaldia.speedofsound.app.SIGNAL_STAGE_CHANGED
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

    @Signal(name = SIGNAL_STAGE_CHANGED)
    fun interface StageChanged {
        fun run(stageOrdinal: Int)
    }

    @Signal(name = SIGNAL_RECORDING_LEVEL)
    fun interface RecordingLevelChanged {
        fun run(level: Double)
    }

    companion object {
        private const val MIN_RECORDING_LEVEL = 0.01f

        fun stageFromOrdinal(ordinal: Int): AppStage = AppStage.entries[ordinal]
    }
}
