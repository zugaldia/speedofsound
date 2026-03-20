package com.zugaldia.speedofsound.app.screens.preferences.basicsetup

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage

class BasicSetupPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    init {
        title = "Basic Setup"
        iconName = "starred-symbolic"

        val globalShortcutGroup = PreferencesGroup().apply {
            title = "Global Shortcut"
        }

        val desktopPermissionsGroup = PreferencesGroup().apply {
            title = "Desktop Permissions"
        }

        add(globalShortcutGroup)
        add(desktopPermissionsGroup)
    }
}
