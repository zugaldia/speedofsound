package com.zugaldia.speedofsound.app.screens.preferences

import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage

class GeneralPage : PreferencesPage() {
    init {
        title = "General"
        iconName = "preferences-system-symbolic"

        val group = PreferencesGroup().apply {
            title = "General Preferences"
            description = "Configure general application preferences"
        }
        add(group)
    }
}
