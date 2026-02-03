package com.zugaldia.speedofsound.app.screens.preferences

import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage

class ModelsPage : PreferencesPage() {
    init {
        title = "Models"
        iconName = "applications-science-symbolic"

        val group = PreferencesGroup().apply {
            title = "Models"
            description = "Configure AI models for transcription and refinement"
        }
        add(group)
    }
}
