@file:Suppress("DEPRECATION")

package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_WIDTH
import com.zugaldia.speedofsound.app.SIGNAL_ASR_MODEL_CHANGED
import com.zugaldia.speedofsound.app.SIGNAL_LANGUAGE_CHANGED
import com.zugaldia.speedofsound.app.SIGNAL_LLM_MODEL_CHANGED
import com.zugaldia.speedofsound.app.SIGNAL_PIPELINE_COMPLETED
import com.zugaldia.speedofsound.app.SIGNAL_PORTALS_RESTORE_TOKEN_MISSING
import com.zugaldia.speedofsound.app.SIGNAL_RECORDING_LEVEL
import com.zugaldia.speedofsound.app.SIGNAL_STAGE_CHANGED
import com.zugaldia.speedofsound.app.screens.about.buildAboutDialog
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesDialog
import com.zugaldia.speedofsound.app.screens.shortcuts.buildShortcutsWindow
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.Banner
import org.slf4j.LoggerFactory
import org.gnome.adw.Application
import org.gnome.adw.ApplicationWindow
import org.gnome.gdk.Gdk
import org.gnome.gdk.ModifierType
import org.gnome.gtk.Box
import org.gnome.gtk.EventControllerKey
import org.gnome.gtk.Orientation
import org.gnome.gtk.Separator

class MainWindow(
    app: Application,
    private val viewModel: MainViewModel,
    private val settingsClient: SettingsClient
) : ApplicationWindow() {
    private val logger = LoggerFactory.getLogger(MainWindow::class.java)

    private val audioWidget: AudioWidget
    private val portalsBanner: Banner
    private val statusWidget: StatusWidget

    // Track whether we should hide the window on pipeline completion
    // Normally, we should unless we're opening a sub-window (e.g., preferences, or shortcuts)
    private var shouldHideOnCompletion = true

    init {
        application = app
        title = APPLICATION_NAME
        setDefaultSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT)
        resizable = false
        addController(EventControllerKey().apply {
            onKeyPressed { keyval, _, state -> keyPressed(keyval, state) }
        })

        portalsBanner = buildBannerWidget { viewModel.startPortalsSession() }
        audioWidget = AudioWidget()
        statusWidget = StatusWidget(
            onSettingsClicked = { onOpenPreferences() },
            onShortcutsClicked = { onOpenShortcuts() },
            onAboutClicked = { onOpenAbout() },
            onQuitClicked = { onQuit() }
        )

        content = Box.builder()
            .setOrientation(Orientation.VERTICAL)
            .setHexpand(true)
            .setVexpand(true)
            .build()
            .apply {
                append(portalsBanner)
                append(audioWidget)
                append(Separator(Orientation.HORIZONTAL))
                append(statusWidget)
            }

        connectSignals()
        viewModel.start()
    }

    private fun connectSignals() {
        viewModel.state.connect(SIGNAL_STAGE_CHANGED, MainState.StageChanged { stageOrdinal: Int ->
            val stage = MainState.stageFromOrdinal(stageOrdinal)
            audioWidget.setStage(stage)
        })

        viewModel.state.connect(SIGNAL_RECORDING_LEVEL, MainState.RecordingLevelChanged { level: Double ->
            audioWidget.setRecordingLevel(level)
        })

        viewModel.state.connect(
            SIGNAL_PORTALS_RESTORE_TOKEN_MISSING,
            MainState.PortalsRestoreTokenMissingChanged { missing: Boolean ->
                portalsBanner.revealed = missing
            }
        )

        viewModel.state.connect(SIGNAL_PIPELINE_COMPLETED, MainState.PipelineCompleted {
            if (shouldHideOnCompletion) { goAway() }
            shouldHideOnCompletion = true // Reset flag for next pipeline
        })

        viewModel.state.connect(SIGNAL_LANGUAGE_CHANGED, MainState.LanguageChanged { languageName: String ->
            statusWidget.setLanguage(languageName)
        })

        viewModel.state.connect(SIGNAL_ASR_MODEL_CHANGED, MainState.AsrModelChanged { modelName: String ->
            statusWidget.setAsrModel(modelName)
        })

        viewModel.state.connect(SIGNAL_LLM_MODEL_CHANGED, MainState.LlmModelChanged { modelName: String ->
            statusWidget.setLlmModel(modelName)
        })
    }

    private fun keyPressed(keyval: Int, state: Set<ModifierType>): Boolean {
        val key = Gdk.keyvalName(keyval)
        val ctrlPressed = state.contains(ModifierType.CONTROL_MASK)
        return when {
            key == "Shift_L" -> { viewModel.onPrimaryLanguageSelected(); true }
            key == "Shift_R" -> { viewModel.onSecondaryLanguageSelected(); true }
            key == "s" || key == "S" -> { viewModel.toggleListening(); true }
            key == "m" || key == "M" -> { goAway(); true }
            key == "Escape" -> { viewModel.cancelListening(); true }
            (key == "q" || key == "Q") && ctrlPressed -> { onQuit(); true }
            else -> false
        }
    }

    private fun goAway() {
        // We use visible = false (not minimize/iconify) to hide the window. This allows the window
        // to be restored on the current workspace when the user relaunches the app. If we used
        // `minimize` instead, GNOME would remember the original workspace and restore the window there,
        // which doesn't work to type into arbitrary apps in arbitrary workspaces.
        visible = false
    }

    private fun onOpenPreferences() {
        shouldHideOnCompletion = false
        viewModel.cancelListening()
        PreferencesDialog(settingsClient).present(this)
    }

    private fun onOpenShortcuts() {
        shouldHideOnCompletion = false
        viewModel.cancelListening()
        buildShortcutsWindow().apply {
            transientFor = this@MainWindow
            present()
        }
    }

    private fun onOpenAbout() {
        shouldHideOnCompletion = false
        viewModel.cancelListening()
        buildAboutDialog().present(this)
    }

    private fun onQuit() {
        viewModel.cancelListening()
        close()
    }
}
