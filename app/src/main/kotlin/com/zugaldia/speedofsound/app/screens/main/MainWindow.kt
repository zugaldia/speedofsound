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
import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.APPLICATION_URL
import org.gnome.adw.Banner
import org.gnome.adw.Application
import org.gnome.adw.ApplicationWindow
import org.gnome.adw.HeaderBar
import org.gnome.adw.ToolbarView
import org.gnome.gdk.Gdk
import org.gnome.gdk.ModifierType
import org.gnome.gio.Menu
import org.gnome.gio.SimpleAction
import org.gnome.gio.SimpleActionGroup
import org.gnome.gtk.Box
import org.gnome.gtk.EventControllerKey
import org.gnome.gtk.MenuButton
import org.gnome.gtk.Orientation
import org.gnome.gtk.Separator

class MainWindow(
    app: Application,
    private val viewModel: MainViewModel,
    private val settingsClient: SettingsClient,
    private val portalsClient: PortalsClient,
) : ApplicationWindow() {
    private val audioWidget: AudioWidget
    private val portalsBanner: Banner
    private val statusWidget: StatusWidget

    // Track whether we should hide the window on pipeline completion
    // Normally, we should unless we're opening a sub-window (e.g., preferences, or shortcuts)
    // A better way is likely to prevent the pipeline to trigger to start with:
    // https://github.com/zugaldia/speedofsound/issues/29
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
        statusWidget = StatusWidget()

        val contentBox = Box.builder()
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

        content = ToolbarView().apply {
            addTopBar(HeaderBar().apply { packEnd(buildMenuButton()) })
            content = contentBox
        }

        connectSignals()
        viewModel.start()
    }

    private fun buildMenuButton(): MenuButton {
        val mainSection = Menu()
        mainSection.append("Preferences", "win.preferences")
        mainSection.append("Keyboard Shortcuts", "win.shortcuts")
        mainSection.append("Help", "win.help")
        mainSection.append("About", "win.about")

        val quitSection = Menu()
        quitSection.append("Quit", "win.quit")

        val menu = Menu()
        menu.appendSection(null, mainSection)
        menu.appendSection(null, quitSection)

        val actionGroup = SimpleActionGroup()
        SimpleAction("preferences", null).also { it.onActivate { onOpenPreferences() }; actionGroup.addAction(it) }
        SimpleAction("shortcuts", null).also { it.onActivate { onOpenShortcuts() }; actionGroup.addAction(it) }
        SimpleAction("help", null).also { it.onActivate { onOpenHelp() }; actionGroup.addAction(it) }
        SimpleAction("about", null).also { it.onActivate { onOpenAbout() }; actionGroup.addAction(it) }
        SimpleAction("quit", null).also { it.onActivate { onQuit() }; actionGroup.addAction(it) }
        insertActionGroup("win", actionGroup)

        return MenuButton().apply {
            iconName = "open-menu-symbolic"
            menuModel = menu
            canFocus = false  // Avoid focus to prevent keyboard/typing accidental activations
        }
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

    private fun keyPressed(keyVal: Int, state: Set<ModifierType>): Boolean {
        val key = Gdk.keyvalName(keyVal)
        val ctrlPressed = state.contains(ModifierType.CONTROL_MASK)
        val superPressed = state.contains(ModifierType.SUPER_MASK)
        return when {
            key == "Shift_L" -> { viewModel.onPrimaryLanguageSelected(); true }
            key == "Shift_R" -> { viewModel.onSecondaryLanguageSelected(); true }
            (key == "z" || key == "Z") && superPressed -> { viewModel.toggleListening(); true }
            (key == "m" || key == "M") && ctrlPressed -> { goAway(); true }
            key == "Escape" -> { viewModel.cancelListening(); true }
            (key == "q" || key == "Q") && ctrlPressed -> { onQuit(); true }
            else -> false
        }
    }

    private fun goAway() {
        if (settingsClient.getHideInsteadOfMinimize()) {
            // Hide the window so it restores on the current workspace (where the target app is),
            // rather than on the workspace the SOS window was originally in. Useful for multi-workspace setups.
            visible = false
        } else {
            // Minimize by default so the window remains accessible from the dock
            // (e.g. to access preferences, or quit the app).
            minimize()
        }
    }

    private fun onOpenPreferences() {
        shouldHideOnCompletion = false
        viewModel.cancelListening()
        PreferencesDialog(settingsClient, portalsClient).apply {
            onClosed { shouldHideOnCompletion = true }
            present(this@MainWindow)
        }
    }

    private fun onOpenShortcuts() {
        shouldHideOnCompletion = false
        viewModel.cancelListening()
        buildShortcutsWindow().apply {
            transientFor = this@MainWindow
            onCloseRequest { shouldHideOnCompletion = true; false }
            present()
        }
    }

    private fun onOpenAbout() {
        shouldHideOnCompletion = false
        viewModel.cancelListening()
        buildAboutDialog().apply {
            onClosed { shouldHideOnCompletion = true }
            present(this@MainWindow)
        }
    }

    private fun onOpenHelp() {
        viewModel.openUri(APPLICATION_URL)
    }

    private fun onQuit() {
        viewModel.cancelListening()
        close()
    }
}
