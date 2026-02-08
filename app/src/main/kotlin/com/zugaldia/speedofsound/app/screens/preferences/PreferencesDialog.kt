package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_WIDTH
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import org.gnome.adw.Dialog
import org.slf4j.LoggerFactory
import org.gnome.adw.HeaderBar
import org.gnome.adw.ToolbarView
import org.gnome.gtk.Box
import org.gnome.gtk.Orientation
import org.gnome.gtk.Stack
import org.gnome.gtk.StackSidebar

class PreferencesDialog(private val settingsClient: SettingsClient) : Dialog() {
    private val logger = LoggerFactory.getLogger(PreferencesDialog::class.java)
    private val viewModel = PreferencesViewModel(settingsClient)

    private val stack: Stack
    private val sidebar: StackSidebar
    private val generalPage: GeneralPage
    private val cloudProvidersPage: CloudProvidersPage
    private val voiceModelsPage: VoiceModelsPage
    private val textModelsPage: TextModelsPage
    private val personalizationPage: PersonalizationPage

    init {
        title = "Preferences"
        contentWidth = DEFAULT_PREFERENCES_DIALOG_WIDTH
        contentHeight = DEFAULT_PREFERENCES_DIALOG_HEIGHT

        generalPage = GeneralPage(viewModel)
        cloudProvidersPage = CloudProvidersPage(viewModel)
        voiceModelsPage = VoiceModelsPage(viewModel)
        textModelsPage = TextModelsPage(viewModel)
        personalizationPage = PersonalizationPage(viewModel)

        stack = Stack().apply {
            hexpand = true
            vexpand = true
            addTitled(generalPage, "general", "General")
            addTitled(cloudProvidersPage, "cloud_providers", "Cloud Providers")
            addTitled(voiceModelsPage, "voice_models", "Voice Models")
            addTitled(textModelsPage, "text_models", "Text Models")
            addTitled(personalizationPage, "personalization", "Personalization")
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

        onClosed {
            personalizationPage.forceSaveInstructions()
        }
    }
}
