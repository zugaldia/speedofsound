package com.zugaldia.speedofsound.app.screens.preferences

import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage

class GeneralPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    init {
        title = "General"
        iconName = "preferences-system-symbolic"

        val primaryComboRow = LanguageComboRow(
            rowTitle = "Primary Language",
            rowSubtitle = "Used by default for speech recognition",
            getLanguage = { viewModel.getDefaultLanguage() },
            setLanguage = { viewModel.setDefaultLanguage(it) }
        )

        val secondaryComboRow = LanguageComboRow(
            rowTitle = "Secondary Language",
            rowSubtitle = "Optional language to quickly switch to (right Shift)",
            getLanguage = { viewModel.getSecondaryLanguage() },
            setLanguage = { viewModel.setSecondaryLanguage(it) }
        )

        val group = PreferencesGroup().apply {
            title = "Language"
            description = "The primary language is used by default. Set an optional secondary language " +
                "to quickly switch between the two using left Shift (primary) and right Shift (secondary)."
            add(primaryComboRow)
            add(secondaryComboRow)
        }

        add(group)
    }
}
