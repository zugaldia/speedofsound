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
    private val onShortcutsClicked: () -> Unit,
    private val onAboutClicked: () -> Unit,
    private val onQuitClicked: () -> Unit
) : Box() {
    private val logger = LoggerFactory.getLogger(StatusWidget::class.java)
    private val languageLabel: Label

    init {
        orientation = Orientation.HORIZONTAL
        hexpand = true
        vexpand = false
        spacing = DEFAULT_BOX_SPACING
        marginTop = DEFAULT_MARGIN
        marginBottom = DEFAULT_MARGIN
        marginStart = DEFAULT_MARGIN
        marginEnd = DEFAULT_MARGIN

        append(createStatusLabel("OpenAI Whisper Tiny", isDimmed = true))
        append(createStatusLabel("·", isDimmed = true))
        languageLabel = createStatusLabel("English", isDimmed = true)
        append(languageLabel)

        val mainSection = Menu()
        mainSection.append("Preferences", "status.preferences")
        mainSection.append("Keyboard Shortcuts", "status.shortcuts")
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
        menuButton.canFocus = false  // Avoid focus to prevent keyboard/typing accidental activations

        val actionGroup = SimpleActionGroup()
        val preferencesAction = SimpleAction("preferences", null)
        preferencesAction.onActivate { onSettingsClicked() }
        actionGroup.addAction(preferencesAction)

        val shortcutsAction = SimpleAction("shortcuts", null)
        shortcutsAction.onActivate { onShortcutsClicked() }
        actionGroup.addAction(shortcutsAction)

        val aboutAction = SimpleAction("about", null)
        aboutAction.onActivate { onAboutClicked() }
        actionGroup.addAction(aboutAction)

        val quitAction = SimpleAction("quit", null)
        quitAction.onActivate { onQuitClicked() }
        actionGroup.addAction(quitAction)

        insertActionGroup("status", actionGroup)
        append(menuButton)
    }

    fun setLanguage(language: String) {
        languageLabel.label = language
    }

    private fun createStatusLabel(text: String, isFramed: Boolean = false, isDimmed: Boolean = false): Label {
        val classes = mutableListOf("caption")
        if (isFramed) classes.add("frame")
        if (isDimmed) classes.add("dim-label")
        val finalText = if (isFramed) "  $text  " else text
        return Label(finalText).apply { cssClasses = classes.toTypedArray() }
    }
}
