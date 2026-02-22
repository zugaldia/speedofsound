@file:Suppress("DEPRECATION")

package com.zugaldia.speedofsound.app.screens.shortcuts

// We need to migrate this to Adw ShortcutsDialog (once we require a libadwaita 1.8 minimum).
// See: https://gnome.pages.gitlab.gnome.org/libadwaita/doc/1-latest/class.ShortcutsDialog.html

import org.gnome.gtk.GtkBuilder
import org.gnome.gtk.ShortcutsWindow

private const val SHORTCUTS_UI_RESOURCE = "/shortcuts.xml"

fun buildShortcutsWindow(): ShortcutsWindow {
    val shortcutsUi = object {}.javaClass.getResourceAsStream(SHORTCUTS_UI_RESOURCE)
        ?.bufferedReader()
        ?.readText()
        ?: throw IllegalStateException("Could not load shortcuts XML resource: $SHORTCUTS_UI_RESOURCE")

    val builder = GtkBuilder()
    builder.addFromString(shortcutsUi, shortcutsUi.length.toLong())
    return builder.getObject("shortcuts_window") as ShortcutsWindow
}
