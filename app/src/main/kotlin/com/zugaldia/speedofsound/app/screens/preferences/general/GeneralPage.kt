package com.zugaldia.speedofsound.app.screens.preferences.general

import com.zugaldia.speedofsound.app.STYLE_CLASS_SUGGESTED_ACTION
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.APPLICATION_SHORTCUT_TRIGGER
import com.zugaldia.stargate.sdk.globalshortcuts.BoundShortcut
import kotlinx.coroutines.launch
import org.gnome.adw.ActionRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SwitchRow
import org.gnome.glib.GLib
import org.gnome.gtk.Align
import org.gnome.gtk.Button
import org.slf4j.LoggerFactory

class GeneralPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(GeneralPage::class.java)
    private val scope = viewModel.viewModelScope

    private val backgroundRecordingRow: SwitchRow
    private val primaryComboRow: LanguageComboRow
    private val secondaryComboRow: LanguageComboRow
    private val appendSpaceRow: SwitchRow
    private val shortcutManualRow: ActionRow
    private val shortcutSetupRow: ActionRow
    private val shortcutActiveRow: ActionRow

    private var shortcutManualRowInitialized = false
    private var shortcutActiveRowInitialized = false

    init {
        title = "General"
        iconName = "preferences-system-symbolic"

        shortcutManualRow = ActionRow().apply {
            title = "Manual Setup Required" // The system does NOT support the Global Shortcuts portal.
            visible = false
        }

        shortcutSetupRow = ActionRow().apply {
            title = "Set Up Shortcut" // The system does support the Global Shortcuts portal.
            subtitle = "We use [$APPLICATION_SHORTCUT_TRIGGER] by default, but you can assign any key during setup."
            val setupButton = Button.withLabel("Set Up").apply {
                valign = Align.CENTER
                addCssClass(STYLE_CLASS_SUGGESTED_ACTION)
                onClicked { scope.launch { bindAndShowShortcuts() } }
            }
            addSuffix(setupButton)
            visible = false
        }

        shortcutActiveRow = ActionRow().apply {
            title = "Active" // The shortcut was successfully bound to the Global Shortcuts portal.
            visible = false
        }

        val globalShortcutGroup = PreferencesGroup().apply {
            title = "Global Shortcut"
            description = "Assign a keyboard shortcut to start and stop voice typing from any application."
            add(shortcutManualRow)
            add(shortcutSetupRow)
            add(shortcutActiveRow)
        }

        primaryComboRow = LanguageComboRow(
            rowTitle = "Primary Language",
            rowSubtitle = "Used by default for speech recognition",
            getLanguage = { viewModel.getDefaultLanguage() },
            setLanguage = { viewModel.setDefaultLanguage(it) }
        )

        secondaryComboRow = LanguageComboRow(
            rowTitle = "Secondary Language",
            rowSubtitle = "Optional language to switch to (right Shift key)",
            getLanguage = { viewModel.getSecondaryLanguage() },
            setLanguage = { viewModel.setSecondaryLanguage(it) }
        )

        val languageGroup = PreferencesGroup().apply {
            title = "Language"
            description = "The primary language is used by default. Optionally, set a secondary language " +
                    "to switch between the two using left Shift (primary) and right Shift (secondary)."
            add(primaryComboRow)
            add(secondaryComboRow)
        }

        backgroundRecordingRow = SwitchRow().apply {
            title = "Record in background"
            subtitle = "Keep the main window hidden while listening."
            active = viewModel.getBackgroundRecording()
        }

        val behaviorGroup = PreferencesGroup().apply {
            title = "App Behavior"
            add(backgroundRecordingRow)
        }

        appendSpaceRow = SwitchRow().apply {
            title = "Append space after transcription"
            subtitle = "Useful when dictating consecutive sentences independently."
            active = viewModel.getAppendSpace()
        }

        val outputGroup = PreferencesGroup().apply {
            title = "Output"
            add(appendSpaceRow)
        }

        add(globalShortcutGroup)
        setupGlobalShortcutsSession()

        add(languageGroup)
        add(behaviorGroup)
        add(outputGroup)

        // Set up notifications after all widgets are initialized
        primaryComboRow.setupNotifications()
        secondaryComboRow.setupNotifications()
        backgroundRecordingRow.onNotify("active") { viewModel.setBackgroundRecording(backgroundRecordingRow.active) }
        appendSpaceRow.onNotify("active") { viewModel.setAppendSpace(appendSpaceRow.active) }
    }

    fun refresh() {
        primaryComboRow.refresh()
        secondaryComboRow.refresh()
        backgroundRecordingRow.active = viewModel.getBackgroundRecording()
        appendSpaceRow.active = viewModel.getAppendSpace()
    }

    /*
     * Step 0: Can we establish a session?
     */
    private fun setupGlobalShortcutsSession() {
        scope.launch {
            val sessionResult = viewModel.createGlobalShortcutsSession()
            if (sessionResult.isSuccess) {
                handleSessionCreated()
            } else {
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showManualSetupRow(); false }
            }
        }
    }

    /*
     * Step 1: List existing shortcuts
     */
    private suspend fun handleSessionCreated() {
        val listResult = viewModel.listGlobalShortcuts()
        if (listResult.isSuccess) {
            handleShortcutsListed(listResult.getOrDefault(emptyList()))
        } else {
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showManualSetupRow(); false }
        }
    }

    /*
     * Step 2: Decide whether to (1) show settings, (2) show setup, or (3) automatically bind
     */
    private suspend fun handleShortcutsListed(shortcuts: List<BoundShortcut>) {
        if (shortcuts.isNotEmpty()) {
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showShortcuts(shortcuts); false }
        } else if (!viewModel.getShortcutConfigured()) {
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { shortcutSetupRow.visible = true; false }
        } else {
            bindAndShowShortcuts()
        }
    }

    /*
     * Step 3: The user previously configured a shortcut, so we should be able to automatically bind it
     * without the user being prompted with any System UI.
     */
    private suspend fun bindAndShowShortcuts() {
        val shortcuts = if (viewModel.bindGlobalShortcuts().isSuccess) {
            viewModel.listGlobalShortcuts().getOrDefault(emptyList())
        } else {
            emptyList()
        }

        if (shortcuts.isNotEmpty()) {
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showShortcuts(shortcuts); false }
        } else {
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { shortcutSetupRow.visible = true; false }
        }
    }

    /*
     * Finally: We are able to see at least 1 shortcut.
     */
    private fun showShortcuts(shortcuts: List<BoundShortcut>) {
        val triggers = shortcuts.mapNotNull { it.triggerDescription }
        logger.info("Showing {} shortcut(s): {}", shortcuts.size, triggers)

        viewModel.setShortcutConfigured(true)
        shortcutSetupRow.visible = false
        shortcutActiveRow.subtitle = triggers.joinToString(", ").ifEmpty { "Configured" }

        // Recent spec addition
        if (!shortcutActiveRowInitialized && viewModel.globalShortcutsVersion >= 2) {
            val configureButton = Button.withLabel("Configure").apply {
                valign = Align.CENTER
                onClicked { viewModel.configureGlobalShortcuts() }
            }
            shortcutActiveRow.addSuffix(configureButton)
            shortcutActiveRowInitialized = true
        }

        shortcutActiveRow.visible = true
    }

    /*
     * Fallback
     */
    private fun showManualSetupRow() {
        if (!shortcutManualRowInitialized) {
            val docsButton = Button.withLabel("Help").apply {
                valign = Align.CENTER
                onClicked { scope.launch { viewModel.openDocumentationUri() } }
            }

            shortcutManualRow.subtitle = "Your system doesn't support automatic shortcut setup. " +
                    "See the documentation for manual configuration instructions."
            shortcutManualRow.addSuffix(docsButton)
            shortcutManualRowInitialized = true
        }

        shortcutManualRow.visible = true
    }
}
