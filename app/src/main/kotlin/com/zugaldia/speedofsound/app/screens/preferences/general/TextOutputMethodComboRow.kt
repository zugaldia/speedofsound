package com.zugaldia.speedofsound.app.screens.preferences.general

import com.zugaldia.speedofsound.core.desktop.settings.TEXT_OUTPUT_METHOD_CLIPBOARD
import com.zugaldia.speedofsound.core.desktop.settings.TEXT_OUTPUT_METHOD_PORTAL
import org.gnome.adw.ComboRow
import org.gnome.gtk.PropertyExpression
import org.gnome.gtk.StringList
import org.gnome.gtk.StringObject

/**
 * Combo row for selecting the text output method (Desktop Portal or Clipboard).
 */
class TextOutputMethodComboRow(
    private val getMethod: () -> String,
    private val setMethod: (String) -> Unit,
) : ComboRow() {

    private var isRefreshing = false

    init {
        title = "Text output method"
        subtitle = "How transcribed text is delivered to the active application."
        model = StringList(DISPLAY_NAMES.toTypedArray())
        expression = PropertyExpression(StringObject.getType(), null, "string")
        refresh()
    }

    fun refresh() {
        isRefreshing = true
        try {
            val index = METHOD_VALUES.indexOf(getMethod())
            if (index >= 0) { selected = index }
        } finally {
            isRefreshing = false
        }
    }

    fun setupNotifications() {
        onNotify("selected") {
            if (isRefreshing) return@onNotify
            val index = selected
            if (index in METHOD_VALUES.indices) {
                setMethod(METHOD_VALUES[index])
            }
        }
    }

    companion object {
        private val METHOD_VALUES = listOf(TEXT_OUTPUT_METHOD_PORTAL, TEXT_OUTPUT_METHOD_CLIPBOARD)
        private val DISPLAY_NAMES = listOf("Keyboard simulation", "Clipboard")
    }
}
