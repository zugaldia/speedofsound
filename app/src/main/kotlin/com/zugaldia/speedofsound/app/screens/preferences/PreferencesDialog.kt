package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_WIDTH
import org.gnome.adw.Dialog
import org.slf4j.LoggerFactory
import org.gnome.adw.HeaderBar
import org.gnome.adw.ToolbarView
import org.gnome.gtk.Box
import org.gnome.gtk.Orientation
import org.gnome.gtk.Stack
import org.gnome.gtk.StackSidebar

class PreferencesDialog : Dialog() {
    private val logger = LoggerFactory.getLogger(PreferencesDialog::class.java)
    private val viewModel = PreferencesViewModel()

    private val stack: Stack
    private val sidebar: StackSidebar
    private val generalPage: GeneralPage
    private val instructionsPage: InstructionsPage
    private val modelsPage: ModelsPage

    init {
        title = "Preferences"
        contentWidth = DEFAULT_PREFERENCES_DIALOG_WIDTH
        contentHeight = DEFAULT_PREFERENCES_DIALOG_HEIGHT

        generalPage = GeneralPage()
        instructionsPage = InstructionsPage()
        modelsPage = ModelsPage()

        stack = Stack().apply {
            hexpand = true
            vexpand = true
            addTitled(generalPage, "general", "General")
            addTitled(instructionsPage, "instructions", "Instructions")
            addTitled(modelsPage, "models", "Models")
        }

        sidebar = StackSidebar().apply {
            stack = this@PreferencesDialog.stack
        }

        val contentBox = Box(Orientation.HORIZONTAL, 0).apply {
            append(sidebar)
            append(stack)
        }

        val toolbarView = ToolbarView().apply {
            addTopBar(HeaderBar())
            content = contentBox
        }

        child = toolbarView
    }
}
