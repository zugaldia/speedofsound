package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_WIDTH
import com.zugaldia.speedofsound.app.SIGNAL_RECORDING_LEVEL
import com.zugaldia.speedofsound.app.SIGNAL_STAGE_CHANGED
import com.zugaldia.speedofsound.app.screens.about.buildAboutDialog
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesDialog
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.AboutDialog
import org.slf4j.LoggerFactory
import org.gnome.adw.Application
import org.gnome.adw.ApplicationWindow
import org.gnome.gdk.Gdk
import org.gnome.gdk.ModifierType
import org.gnome.gtk.Box
import org.gnome.gtk.EventControllerKey
import org.gnome.gtk.Orientation
import org.gnome.gtk.Separator

class MainWindow(app: Application) : ApplicationWindow() {
    private val logger = LoggerFactory.getLogger(MainWindow::class.java)
    private val viewModel = MainViewModel()

    private val audioWidget: AudioWidget
    private val statusWidget: StatusWidget
    private val preferencesDialog: PreferencesDialog
    private val aboutDialog: AboutDialog

    init {
        application = app
        title = APPLICATION_NAME
        setDefaultSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT)
        resizable = false
        addController(EventControllerKey().apply {
            onKeyPressed { keyval, _, state -> keyPressed(keyval, state) }
        })

        preferencesDialog = PreferencesDialog()
        aboutDialog = buildAboutDialog()

        audioWidget = AudioWidget()
        statusWidget = StatusWidget(
            onSettingsClicked = { onOpenPreferences() },
            onAboutClicked = { onOpenAbout() },
            onQuitClicked = { onQuit() }
        )

        content = Box.builder()
            .setOrientation(Orientation.VERTICAL)
            .setHexpand(true)
            .setVexpand(true)
            .build()
            .apply {
                append(audioWidget)
                append(Separator(Orientation.HORIZONTAL))
                append(statusWidget)
            }

        viewModel.state.connect(SIGNAL_STAGE_CHANGED, MainState.StageChanged { stageOrdinal: Int ->
            val stage = MainState.stageFromOrdinal(stageOrdinal)
            audioWidget.setStage(stage)
        })

        viewModel.state.connect(SIGNAL_RECORDING_LEVEL, MainState.RecordingLevelChanged { level: Double ->
            audioWidget.setRecordingLevel(level)
        })

        viewModel.start()
    }

    private fun keyPressed(keyval: Int, state: Set<ModifierType>): Boolean {
        val key = Gdk.keyvalName(keyval)
        val ctrlPressed = state.contains(ModifierType.CONTROL_MASK)
        return when {
            key == "s" || key == "S" -> { onToggle(); true }
            key == "Escape" -> { onCancel(); true }
            (key == "q" || key == "Q") && ctrlPressed -> { onQuit(); true }
            else -> false
        }
    }

    private fun onOpenPreferences() {
        preferencesDialog.present(this)
    }

    private fun onOpenAbout() {
        aboutDialog.present(this)
    }

    private fun onToggle() {
        viewModel.toggleListening()
    }

    private fun onCancel() {
        viewModel.cancelListening()
        //visible = false
    }

    private fun onQuit() {
        viewModel.shutdown()
        close()
    }
}
