package com.zugaldia.speedofsound.app.screens.preferences.general

import com.zugaldia.speedofsound.app.ICON_PREFERENCES_SYSTEM
import com.zugaldia.speedofsound.app.STYLE_CLASS_SUGGESTED_ACTION
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.APPLICATION_SHORTCUT_TRIGGER
import com.zugaldia.stargate.sdk.globalshortcuts.BoundShortcut
import com.zugaldia.stargate.sdk.isSandboxed
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

    private val stayHiddenOnActivationRow: SwitchRow
    private val backgroundRecordingRow: SwitchRow
    private val hideInsteadOfMinimizeRow: SwitchRow
    private val primaryComboRow: LanguageComboRow
    private val secondaryComboRow: LanguageComboRow
    private val textOutputMethodRow: TextOutputMethodComboRow
    private val appendSpaceRow: SwitchRow
    private val shortcutManualRow: ActionRow
    private val shortcutSetupRow: ActionRow
    private val shortcutActiveRow: ActionRow

    private var shortcutManualRowInitialized = false
    private var shortcutActiveRowInitialized = false

    init {
        title = "General"
        iconName = ICON_PREFERENCES_SYSTEM

        shortcutManualRow = ActionRow().apply {
            title = "Manual Setup Required" // The system does NOT support the Global Shortcuts portal.
            visible = false
        }

        shortcutSetupRow = ActionRow().apply {
            title = "Set Up Shortcut" // The system does support the Global Shortcuts portal.
            subtitle = "We use <tt>$APPLICATION_SHORTCUT_TRIGGER</tt> by default, " +
                "but you can assign any key during setup."
            val setupButton = Button.withLabel("Set Up").apply {
                valign = Align.CENTER
                addCssClass(STYLE_CLASS_SUGGESTED_ACTION)
                onClicked { scope.launch { bindAndShowShortcuts() } }
            }
            addSuffix(setupButton)
            addSuffix(createHelpButton())
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

        textOutputMethodRow = TextOutputMethodComboRow(
            getMethod = { viewModel.getTextOutputMethod() },
            setMethod = { viewModel.setTextOutputMethod(it) },
        )

        appendSpaceRow = SwitchRow().apply {
            title = "Append space after transcription"
            subtitle = "Useful when dictating consecutive sentences independently."
            active = viewModel.getAppendSpace()
        }

        val outputGroup = PreferencesGroup().apply {
            title = "Output"
            add(textOutputMethodRow)
            add(appendSpaceRow)
        }

        stayHiddenOnActivationRow = SwitchRow().apply {
            title = "Stay hidden on activation"
            subtitle = "Launch without showing the main window. Use the shortcut to start voice typing."
            active = viewModel.getStayHiddenOnActivation()
        }

        backgroundRecordingRow = SwitchRow().apply {
            title = "Record in background"
            subtitle = "Keep the main window hidden while listening."
            active = viewModel.getBackgroundRecording()
        }

        hideInsteadOfMinimizeRow = SwitchRow().apply {
            title = "Hide instead of minimize"
            subtitle = "Useful on multi-workspace setups where the window should restore on the current workspace."
            active = viewModel.getHideInsteadOfMinimize()
        }

        val behaviorGroup = PreferencesGroup().apply {
            title = "App Behavior"
            add(stayHiddenOnActivationRow)
            add(backgroundRecordingRow)
            add(hideInsteadOfMinimizeRow)
        }

        add(globalShortcutGroup)
        if (isSandboxed()) setupGlobalShortcutsSession() else showManualSetupRow()

        add(languageGroup)
        add(outputGroup)
        add(behaviorGroup)

        // Set up notifications after all widgets are initialized
        primaryComboRow.setupNotifications()
        secondaryComboRow.setupNotifications()
        textOutputMethodRow.setupNotifications()
        appendSpaceRow.onNotify("active") { viewModel.setAppendSpace(appendSpaceRow.active) }
        stayHiddenOnActivationRow.onNotify("active") {
            viewModel.setStayHiddenOnActivation(stayHiddenOnActivationRow.active)
        }
        backgroundRecordingRow.onNotify("active") { viewModel.setBackgroundRecording(backgroundRecordingRow.active) }
        hideInsteadOfMinimizeRow.onNotify("active") {
            viewModel.setHideInsteadOfMinimize(hideInsteadOfMinimizeRow.active)
        }
    }

    fun refresh() {
        primaryComboRow.refresh()
        secondaryComboRow.refresh()
        textOutputMethodRow.refresh()
        appendSpaceRow.active = viewModel.getAppendSpace()
        stayHiddenOnActivationRow.active = viewModel.getStayHiddenOnActivation()
        backgroundRecordingRow.active = viewModel.getBackgroundRecording()
        hideInsteadOfMinimizeRow.active = viewModel.getHideInsteadOfMinimize()
    }

    /*
     * Step 0: Can we establish a session?
     */
    private fun setupGlobalShortcutsSession() {
        logger.info("[Shortcut] isSandboxed=true, attempting to create global shortcuts session")
        scope.launch {
            val sessionResult = viewModel.createGlobalShortcutsSession()
            if (sessionResult.isSuccess) {
                logger.info("[Shortcut] Session created successfully, proceeding to list shortcuts")
                handleSessionCreated()
            } else {
                logger.warn("[Shortcut] Session creation failed: {}", sessionResult.exceptionOrNull()?.message)
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showManualSetupRow(); false }
            }
        }
    }

    /*
     * Step 1: List existing shortcuts
     */
    private suspend fun handleSessionCreated() {
        logger.info("[Shortcut] Listing existing global shortcuts")
        val listResult = viewModel.listGlobalShortcuts()
        if (listResult.isSuccess) {
            val shortcuts = listResult.getOrDefault(emptyList())
            logger.info("[Shortcut] Listed {} shortcut(s)", shortcuts.size)
            handleShortcutsListed(shortcuts)
        } else {
            logger.warn("[Shortcut] Failed to list shortcuts: {}", listResult.exceptionOrNull()?.message)
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showManualSetupRow(); false }
        }
    }

    /*
     * Step 2: Decide whether to (1) show settings, (2) show setup, or (3) automatically bind
     */
    private suspend fun handleShortcutsListed(shortcuts: List<BoundShortcut>) {
        val shortcutConfigured = viewModel.getShortcutConfigured()
        logger.info("[Shortcut] Decision: shortcuts.size={}, shortcutConfigured={}", shortcuts.size, shortcutConfigured)
        if (shortcuts.isNotEmpty()) {
            logger.info("[Shortcut] Existing shortcuts found, showing them directly")
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showShortcuts(shortcuts); false }
        } else if (!shortcutConfigured) {
            logger.info("[Shortcut] No shortcuts and not previously configured, showing setup row")
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { shortcutSetupRow.visible = true; false }
        } else {
            logger.info("[Shortcut] Previously configured but no shortcuts returned, attempting auto-bind")
            bindAndShowShortcuts()
        }
    }

    /*
     * Step 3: The user previously configured a shortcut, so we should be able to automatically bind it
     * without the user being prompted with any System UI.
     */
    private suspend fun bindAndShowShortcuts() {
        logger.info("[Shortcut] Binding global shortcuts via portal")
        val bindResult = viewModel.bindGlobalShortcuts()
        val shortcuts = if (bindResult.isSuccess) {
            logger.info("[Shortcut] Bind succeeded, listing shortcuts")
            viewModel.listGlobalShortcuts().getOrDefault(emptyList())
        } else {
            logger.warn("[Shortcut] Bind failed: {}", bindResult.exceptionOrNull()?.message)
            emptyList()
        }

        if (shortcuts.isNotEmpty()) {
            logger.info("[Shortcut] Got {} shortcut(s) after bind, showing them", shortcuts.size)
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) { showShortcuts(shortcuts); false }
        } else {
            logger.warn("[Shortcut] No shortcuts after bind, falling back to setup row")
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
        val subtitle = triggers.joinToString(", ").ifEmpty { "Configured" }
        shortcutActiveRow.subtitle = GLib.markupEscapeText(subtitle, subtitle.length.toLong())

        if (!shortcutActiveRowInitialized) {
            // Recent spec addition
            if (viewModel.globalShortcutsVersion >= 2) {
                val configureButton = Button.withLabel("Configure").apply {
                    valign = Align.CENTER
                    onClicked { viewModel.configureGlobalShortcuts() }
                }
                shortcutActiveRow.addSuffix(configureButton)
            }
            shortcutActiveRow.addSuffix(createHelpButton())
            shortcutActiveRowInitialized = true
        }

        shortcutActiveRow.visible = true
    }

    /*
     * Fallback
     */
    private fun showManualSetupRow() {
        logger.warn("[Shortcut] Falling back to manual setup row (portal not supported or failed)")
        if (!shortcutManualRowInitialized) {
            shortcutManualRow.subtitle = "Your system doesn't support automatic shortcut setup. " +
                    "See the documentation for manual configuration instructions."
            shortcutManualRow.addSuffix(createHelpButton())
            shortcutManualRowInitialized = true
        }

        shortcutManualRow.visible = true
    }

    private fun createHelpButton(): Button = Button.withLabel("Help").apply {
        valign = Align.CENTER
        onClicked { scope.launch { viewModel.openDocumentationUri() } }
    }
}
