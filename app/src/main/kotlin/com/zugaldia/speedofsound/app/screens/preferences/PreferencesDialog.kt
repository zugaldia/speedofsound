package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_WIDTH
import com.zugaldia.speedofsound.app.screens.preferences.credentials.CloudCredentialsPage
import com.zugaldia.speedofsound.app.screens.preferences.general.GeneralPage
import com.zugaldia.speedofsound.app.screens.preferences.personalization.PersonalizationPage
import com.zugaldia.speedofsound.app.screens.preferences.text.TextModelsPage
import com.zugaldia.speedofsound.app.screens.preferences.voice.VoiceModelsPage
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
    private val cloudCredentialsPage: CloudCredentialsPage
    private val voiceModelsPage: VoiceModelsPage
    private val textModelsPage: TextModelsPage
    private val personalizationPage: PersonalizationPage

    init {
        title = "Preferences"
        contentWidth = DEFAULT_PREFERENCES_DIALOG_WIDTH
        contentHeight = DEFAULT_PREFERENCES_DIALOG_HEIGHT

        generalPage = GeneralPage(viewModel)
        cloudCredentialsPage = CloudCredentialsPage(viewModel)
        voiceModelsPage = VoiceModelsPage(viewModel)
        textModelsPage = TextModelsPage(viewModel)
        personalizationPage = PersonalizationPage(viewModel)

        stack = Stack().apply {
            hexpand = true
            vexpand = true
            addTitled(generalPage, "general", "General")
            addTitled(cloudCredentialsPage, "cloud_credentials", "Cloud Credentials")
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
