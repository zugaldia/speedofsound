package com.zugaldia.speedofsound.app.screens.preferences.general

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage

class GeneralPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val primaryComboRow: LanguageComboRow
    private val secondaryComboRow: LanguageComboRow

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

        val group = PreferencesGroup().apply {
            title = "Language"
            description = "The primary language is used by default. Optionally, set a secondary language " +
                "to switch between the two using left Shift (primary) and right Shift (secondary)."
            add(primaryComboRow)
            add(secondaryComboRow)
        }

        add(group)

        // Set up notifications after all widgets are initialized
        primaryComboRow.setupNotifications()
        secondaryComboRow.setupNotifications()
    }

    fun refresh() {
        primaryComboRow.refresh()
        secondaryComboRow.refresh()
    }
}
