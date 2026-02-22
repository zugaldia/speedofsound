package com.zugaldia.speedofsound.app.screens.preferences.shared

import com.zugaldia.speedofsound.app.STYLE_CLASS_FLAT
import org.gnome.adw.EntryRow
import org.gnome.gio.Menu
import org.gnome.gio.SimpleAction
import org.gnome.gio.SimpleActionGroup
import org.gnome.gtk.MenuButton

/**
 * Generic entry row for base URL input with preset service selection.
 */
class BaseUrlEntryRow(
    private val servicePresets: List<CustomServicePreset>,
    private val onTextChanged: () -> Unit
) : EntryRow() {
    init {
        title = "Base URL (optional)"
        setupSuffixButton()
    }

    private fun setupSuffixButton() {
        val menu = Menu()
        servicePresets.forEach { preset ->
            menu.append(preset.displayName, "baseurl.${preset.actionName}")
        }

        val menuButton = MenuButton().apply {
            iconName = "image-auto-adjust-symbolic"
            tooltipText = "Select a common local service"
            cssClasses = arrayOf(STYLE_CLASS_FLAT)
            menuModel = menu
        }

        val actionGroup = SimpleActionGroup()
        servicePresets.forEach { preset ->
            val action = SimpleAction(preset.actionName, null)
            action.onActivate { text = preset.url }
            actionGroup.addAction(action)
        }

        insertActionGroup("baseurl", actionGroup)
        addSuffix(menuButton)
    }

    fun setupNotifications() {
        onNotify("text") { onTextChanged() }
    }

    fun getBaseUrl(): String? = text.trim().ifEmpty { null }

    fun clear() {
        text = ""
    }
}
