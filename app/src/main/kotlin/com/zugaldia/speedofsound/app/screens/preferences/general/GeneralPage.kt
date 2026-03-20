package com.zugaldia.speedofsound.app.screens.preferences.general

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SwitchRow

class GeneralPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val backgroundRecordingRow: SwitchRow
    private val primaryComboRow: LanguageComboRow
    private val secondaryComboRow: LanguageComboRow
    private val appendSpaceRow: SwitchRow

    init {
        title = "General"
        iconName = "preferences-system-symbolic"

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
}
