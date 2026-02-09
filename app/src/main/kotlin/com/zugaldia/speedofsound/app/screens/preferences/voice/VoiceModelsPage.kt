package com.zugaldia.speedofsound.app.screens.preferences.voice

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage

class VoiceModelsPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    init {
        title = "Voice Models"
        iconName = "applications-science-symbolic"

        val group = PreferencesGroup().apply {
            title = "Voice Models"
            description = "Choose which voice recognition model to use for converting your speech into text"
        }
        add(group)
    }
}
