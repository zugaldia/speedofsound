package com.zugaldia.speedofsound.core.plugins.recorder

import com.zugaldia.speedofsound.core.plugins.AppPluginEvent

/**
 * Events emitted by the JvmRecorder plugin.
 */
sealed class RecorderEvent : AppPluginEvent() {
    /**
     * Emitted periodically during recording with the current audio level.
     * @param level Normalized volume level in range [0.0, 1.0]
     */
    data class RecordingLevel(val level: Float) : RecorderEvent()
}
