package com.zugaldia.speedofsound.app.screens.preferences

import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage

class InstructionsPage : PreferencesPage() {
    init {
        title = "Instructions"
        iconName = "document-edit-symbolic"

        val group = PreferencesGroup().apply {
            title = "Instructions"
            description = "Configure LLM instructions and vocabulary for transcription refinement"
        }
        add(group)
    }
}
