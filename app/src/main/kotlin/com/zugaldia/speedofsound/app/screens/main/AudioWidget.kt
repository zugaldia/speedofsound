package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_PROGRESS_BAR_WIDTH
import org.apache.logging.log4j.LogManager
import org.gnome.glib.GLib
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Label
import org.gnome.gtk.Orientation
import org.gnome.gtk.ProgressBar

class AudioWidget : Box(Orientation.VERTICAL, DEFAULT_BOX_SPACING) {
    private val logger = LogManager.getLogger()

    private val progressBar = ProgressBar().apply {
        setSizeRequest(DEFAULT_PROGRESS_BAR_WIDTH, -1)
        fraction = 0.0
    }

    private var isPulsating: Boolean = false

    private val statusLabel = Label(INITIAL_LOADING_MESSAGE).apply {
        cssClasses = arrayOf("dim-label")
    }

    init {
        vexpand = true
        hexpand = false
        halign = Align.CENTER
        valign = Align.CENTER
        spacing = 2 * DEFAULT_BOX_SPACING
        append(progressBar)
        append(statusLabel)
    }

    fun setStage(stage: AppStage) {
        statusLabel.label = when (stage) {
            AppStage.LOADING -> INITIAL_LOADING_MESSAGE
            AppStage.IDLE -> "Ready"
            AppStage.LISTENING -> "Listening..."
            AppStage.TRANSCRIBING -> "Transcribing..."
            AppStage.POLISHING -> "${polishingMessages.random()}..."
        }

        val shouldPulsate = stage in listOf(AppStage.TRANSCRIBING, AppStage.POLISHING)
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

    companion object {
        private const val PULSE_INTERVAL_MS = 100
        private const val INITIAL_LOADING_MESSAGE = "Loading..."
        private val polishingMessages = listOf(
            "Assembling", "Brewing", "Composing",
            "Computing", "Conjuring", "Contemplating",
            "Crafting", "Deliberating", "Distilling",
            "Formulating", "Manifesting", "Marinating",
            "Percolating", "Polishing", "Pondering",
            "Refining", "Summoning", "Synthesizing",
            "Thinking", "Weaving",
        )
    }
}
