package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_MARGIN
import org.gnome.gio.Menu
import org.slf4j.LoggerFactory
import org.gnome.gio.SimpleAction
import org.gnome.gio.SimpleActionGroup
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Label
import org.gnome.gtk.MenuButton
import org.gnome.gtk.Orientation

class StatusWidget(
    private val onSettingsClicked: () -> Unit,
    private val onAboutClicked: () -> Unit,
    private val onQuitClicked: () -> Unit
) : Box() {
    private val logger = LoggerFactory.getLogger(StatusWidget::class.java)

    init {
        orientation = Orientation.HORIZONTAL
        hexpand = true
        vexpand = false
        spacing = 2 * DEFAULT_BOX_SPACING
        marginTop = DEFAULT_MARGIN
        marginBottom = DEFAULT_MARGIN
        marginStart = DEFAULT_MARGIN
        marginEnd = DEFAULT_MARGIN

        append(createShortcutGroup("s", "Start/Stop"))
        append(createShortcutGroup("Escape", "Cancel"))
        append(createShortcutGroup("Ctrl+Q", "Quit"))

        val mainSection = Menu()
        mainSection.append("Preferences", "status.preferences")
        mainSection.append("About", "status.about")

        val quitSection = Menu()
        quitSection.append("Quit", "status.quit")

        val menu = Menu()
        menu.appendSection(null, mainSection)
        menu.appendSection(null, quitSection)

        val menuButton = MenuButton()
        menuButton.iconName = "settings-symbolic"
        menuButton.hexpand = true
        menuButton.halign = Align.END
        menuButton.cssClasses = arrayOf("flat")
        menuButton.menuModel = menu

        val actionGroup = SimpleActionGroup()
        val preferencesAction = SimpleAction("preferences", null)
        preferencesAction.onActivate { onSettingsClicked() }
        actionGroup.addAction(preferencesAction)

        val aboutAction = SimpleAction("about", null)
        aboutAction.onActivate { onAboutClicked() }
        actionGroup.addAction(aboutAction)

        val quitAction = SimpleAction("quit", null)
        quitAction.onActivate { onQuitClicked() }
        actionGroup.addAction(quitAction)

        insertActionGroup("status", actionGroup)
        append(menuButton)
    }

    private fun createShortcutGroup(shortcut: String, description: String): Box {
        val box = Box(Orientation.HORIZONTAL, DEFAULT_BOX_SPACING / 2)
        box.append(Label(description).apply { cssClasses = arrayOf("caption") })
        box.append(Label("  $shortcut  ").apply { cssClasses = arrayOf("caption", "frame", "dim-label") })
        return box
    }
}
