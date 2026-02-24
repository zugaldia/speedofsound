package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_PREFERENCES_DIALOG_WIDTH
import com.zugaldia.speedofsound.app.screens.preferences.credentials.CloudCredentialsPage
import com.zugaldia.speedofsound.app.screens.preferences.general.GeneralPage
import com.zugaldia.speedofsound.app.screens.preferences.importexport.ImportExportPage
import com.zugaldia.speedofsound.app.screens.preferences.library.ModelLibraryPage
import com.zugaldia.speedofsound.app.screens.preferences.personalization.PersonalizationPage
import com.zugaldia.speedofsound.app.screens.preferences.text.TextModelsPage
import com.zugaldia.speedofsound.app.screens.preferences.voice.VoiceModelsPage
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import org.gnome.adw.Banner
import org.gnome.adw.Dialog
import org.gnome.adw.HeaderBar
import org.gnome.adw.ToolbarView
import org.gnome.gtk.Box
import org.gnome.gtk.Orientation
import org.gnome.gtk.Stack
import org.gnome.gtk.StackSidebar
import org.slf4j.LoggerFactory

class PreferencesDialog(private val settingsClient: SettingsClient) : Dialog() {
    private val logger = LoggerFactory.getLogger(PreferencesDialog::class.java)
    private val viewModel = PreferencesViewModel(settingsClient)

    private val operationsBanner: Banner
    private val stack: Stack
    private val sidebar: StackSidebar
    private val generalPage: GeneralPage
    private val cloudCredentialsPage: CloudCredentialsPage
    private val voiceModelsPage: VoiceModelsPage
    private val textModelsPage: TextModelsPage
    private val modelLibraryPage: ModelLibraryPage
    private val personalizationPage: PersonalizationPage
    private val importExportPage: ImportExportPage

    init {
        title = "Preferences"
        contentWidth = DEFAULT_PREFERENCES_DIALOG_WIDTH
        contentHeight = DEFAULT_PREFERENCES_DIALOG_HEIGHT

        operationsBanner = Banner("Model operations in progress. Please keep this window open.").apply {
            revealed = false
        }

        generalPage = GeneralPage(viewModel)
        cloudCredentialsPage = CloudCredentialsPage(viewModel)
        voiceModelsPage = VoiceModelsPage(viewModel)
        textModelsPage = TextModelsPage(viewModel)
        modelLibraryPage = ModelLibraryPage(viewModel) { hasOperations -> operationsBanner.revealed = hasOperations }
        personalizationPage = PersonalizationPage(viewModel)
        importExportPage = ImportExportPage(viewModel) { refreshAllPages() }

        stack = Stack().apply {
            hexpand = true
            vexpand = true
            addTitled(generalPage, "general", "General")
            addTitled(modelLibraryPage, "model_library", "Model Library")
            addTitled(cloudCredentialsPage, "cloud_credentials", "Cloud Credentials")
            addTitled(voiceModelsPage, "voice_models", "Voice Models")
            addTitled(textModelsPage, "text_models", "Text Models")
            addTitled(personalizationPage, "personalization", "Personalization")
            addTitled(importExportPage, "import_export", "Import / Export")

            // This is to make sure the voice models page includes any models the user just downloaded
            // in the library page, not only the ones that had been downloaded before.
            onNotify("visible-child") {
                if (visibleChild == voiceModelsPage) {
                    voiceModelsPage.refreshProviders()
                }
            }
        }

        sidebar = StackSidebar().apply {
            stack = this@PreferencesDialog.stack
        }

        val contentBox = Box(Orientation.HORIZONTAL, 0).apply {
            append(sidebar)
            append(stack)
        }

        val contentWithBanner = Box(Orientation.VERTICAL, 0).apply {
            append(operationsBanner)
            append(contentBox)
        }

        val toolbarView = ToolbarView().apply {
            addTopBar(HeaderBar())
            content = contentWithBanner
        }

        child = toolbarView

        onCloseAttempt {
            if (modelLibraryPage.hasOperationsInProgress()) {
                logger.info("Close attempt blocked: model operations in progress")
            } else {
                close()
            }
        }

        onClosed {
            personalizationPage.forceSaveInstructions()
            modelLibraryPage.shutdown()
            importExportPage.shutdown()
        }
    }

    private fun refreshAllPages() {
        logger.info("Refreshing all preferences pages after import")
        generalPage.refresh()
        cloudCredentialsPage.refresh()
        voiceModelsPage.refreshProviders()
        textModelsPage.refreshProviders()
        personalizationPage.refresh()
    }
}
