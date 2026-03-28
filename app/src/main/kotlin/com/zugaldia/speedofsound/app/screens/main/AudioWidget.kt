package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_PROGRESS_BAR_WIDTH
import com.zugaldia.speedofsound.app.ICON_SOUND_WAVE
import com.zugaldia.speedofsound.app.ICON_STOP
import com.zugaldia.speedofsound.app.SEPARATOR_CHARACTER
import com.zugaldia.speedofsound.core.APPLICATION_SHORTCUT_TRIGGER
import org.gnome.adw.ButtonContent
import org.gnome.glib.GLib
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Justification
import org.gnome.gtk.Label
import org.gnome.gtk.Orientation
import org.gnome.gtk.ProgressBar

class AudioWidget(
    private val onToggle: () -> Unit,
) : Box(Orientation.VERTICAL, DEFAULT_BOX_SPACING) {
    private val progressBar = ProgressBar().apply {
        setSizeRequest(DEFAULT_PROGRESS_BAR_WIDTH, -1)
        fraction = 0.0
    }

    private var isPulsating: Boolean = false
    private var hasGrabbedFocus: Boolean = false
    private var isPortalsReady: Boolean = false
    private var currentStage: AppStage = AppStage.LOADING

    private val startButtonContent = ButtonContent().apply {
        iconName = ICON_SOUND_WAVE
        label = "Start"
    }

    private val startButton = Button().apply {
        child = startButtonContent
        tooltipText = "Window will minimize automatically after transcription"
        onClicked { onToggle() }
    }

    private val stopButton = Button().apply {
        child = ButtonContent().apply {
            iconName = ICON_STOP
            label = "Stop"
        }
        sensitive = false
        onClicked { onToggle() }
    }

    private val controlsBox = Box(Orientation.HORIZONTAL, DEFAULT_BOX_SPACING).apply {
        halign = Align.CENTER
        append(startButton)
        append(stopButton)
    }

    private val statusLabel = Label(INITIAL_LOADING_MESSAGE).apply {
        cssClasses = arrayOf("dim-label")
        justify = Justification.CENTER
        useMarkup = true
    }

    init {
        vexpand = true
        hexpand = false
        halign = Align.CENTER
        valign = Align.CENTER
        spacing = 2 * DEFAULT_BOX_SPACING
        append(progressBar)
        append(controlsBox)
        append(statusLabel)
    }

    fun setStage(stage: AppStage) {
        currentStage = stage
        startButtonContent.label = when (stage) {
            AppStage.LOADING, AppStage.IDLE -> "Start"
            AppStage.LISTENING -> "Listening..."
            AppStage.TRANSCRIBING -> "Transcribing..."
            AppStage.POLISHING -> "${polishingMessages.random()}..."
        }

        statusLabel.label = when (stage) {
            AppStage.LOADING -> INITIAL_LOADING_MESSAGE
            AppStage.IDLE -> "Or use <tt>$APPLICATION_SHORTCUT_TRIGGER</tt> to start and stop"
            else -> "<tt>Esc</tt> to cancel $SEPARATOR_CHARACTER the window minimizes when done"
        }

        startButton.sensitive = stage == AppStage.IDLE && isPortalsReady
        stopButton.sensitive = stage == AppStage.LISTENING

        if (stage == AppStage.IDLE && !hasGrabbedFocus) {
            hasGrabbedFocus = true
            startButton.grabFocus()
        }

        val shouldPulsate = stage in listOf(AppStage.LOADING, AppStage.TRANSCRIBING, AppStage.POLISHING)
        setPulsating(shouldPulsate)
        if (!shouldPulsate) setRecordingLevel(0.0)
    }

    fun setPulsating(active: Boolean) {
        if (isPulsating == active) return
        isPulsating = active
        if (active) {
            GLib.timeoutAdd(GLib.PRIORITY_DEFAULT, PULSE_INTERVAL_MS) {
                if (isPulsating) {
                    progressBar.pulse()
                    true
                } else {
                    false // Timeout is destroyed and not called again
                }
            }
        }
    }

    fun setRecordingLevel(level: Double) {
        progressBar.fraction = level.coerceIn(0.0, 1.0)
    }

    fun setPortalsReady(ready: Boolean) {
        isPortalsReady = ready
        startButton.sensitive = currentStage == AppStage.IDLE && isPortalsReady
    }

    companion object {
        private const val PULSE_INTERVAL_MS = 100
        private const val INITIAL_LOADING_MESSAGE = "Loading..."

        private val polishingMessages = listOf(
            "Assembling", "Brewing", "Composing",
            "Computing", "Contemplating", "Crafting",
            "Deliberating", "Distilling", "Formulating",
            "Harmonizing", "Marinating", "Modulating",
            "Oscillating", "Percolating", "Polishing",
            "Pondering", "Refining", "Soundifying",
            "Speedifying", "Supersonicking", "Synthesizing",
            "Thinking", "Velocifying", "Weaving",
        )
    }
}
