package com.zugaldia.speedofsound.app.screens.preferences.voice

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.MAX_VOICE_MODEL_PROVIDERS
import com.zugaldia.speedofsound.app.STYLE_CLASS_BOXED_LIST
import com.zugaldia.speedofsound.app.STYLE_CLASS_DESTRUCTIVE_ACTION
import com.zugaldia.speedofsound.app.STYLE_CLASS_FLAT
import com.zugaldia.speedofsound.app.STYLE_CLASS_SUGGESTED_ACTION
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_VOICE_MODEL_PROVIDER_ID
import com.zugaldia.speedofsound.core.desktop.settings.VoiceModelProviderSetting
import org.gnome.adw.ActionRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.gtk.Align
import org.gnome.gtk.Button
import org.gnome.gtk.ListBox
import org.gnome.gtk.SelectionMode
import org.slf4j.LoggerFactory

class VoiceModelsPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(VoiceModelsPage::class.java)

    private val activeProviderComboRow: ActiveProviderComboRow
    private val providersListBox: ListBox
    private val addProviderButton: Button

    init {
        title = "Voice Models"
        iconName = "audio-input-microphone-symbolic"

        activeProviderComboRow = ActiveProviderComboRow(
            getSelectedProviderId = { viewModel.getSelectedVoiceModelProviderId() },
            setSelectedProviderId = { viewModel.setSelectedVoiceModelProviderId(it) }
        )

        val textProcessingGroup = PreferencesGroup().apply {
            title = "Speech Recognition"
            description = "Configure speech recognition providers to transcribe audio into text."
            add(activeProviderComboRow)
        }

        addProviderButton = Button.withLabel("Add Provider").apply {
            addCssClass(STYLE_CLASS_SUGGESTED_ACTION)
            onClicked { showAddProviderDialog() }
        }

        providersListBox = ListBox().apply {
            addCssClass(STYLE_CLASS_BOXED_LIST)
            marginTop = DEFAULT_BOX_SPACING
            selectionMode = SelectionMode.NONE
        }

        val providersGroup = PreferencesGroup().apply {
            title = "Provider Configurations"
            description = "Configure ASR providers for voice transcription."
            add(addProviderButton)
            add(providersListBox)
        }

        add(textProcessingGroup)
        add(providersGroup)
        loadInitialProviders()
        setupNotifications()
    }

    private fun loadInitialProviders() {
        val providers = viewModel.getVoiceModelProviders()
        providers.sortedBy { it.name.lowercase() }.forEach { provider -> addProviderToUI(provider) }
        activeProviderComboRow.updateProviders(providers)
        updateAddProviderButtonState()
    }

    private fun setupNotifications() {
        activeProviderComboRow.setupNotifications()
    }

    private fun addProviderToUI(providerSetting: VoiceModelProviderSetting) {
        val providerLabel = providerSetting.provider.displayName
        val modelLabel = providerSetting.modelId
        val subtitle = "$providerLabel • $modelLabel"

        val row = ActionRow().apply {
            title = providerSetting.name
            this.subtitle = subtitle
        }

        // Credential indicator
        if (providerSetting.credentialId != null) {
            row.addSuffix(Button.fromIconName("dialog-password-symbolic").apply {
                addCssClass(STYLE_CLASS_FLAT)
                valign = Align.CENTER
                sensitive = false
                tooltipText = "Custom Credentials Set"
            })
        }

        // Base URL indicator
        if (providerSetting.baseUrl != null) {
            row.addSuffix(Button.fromIconName("network-server-symbolic").apply {
                addCssClass(STYLE_CLASS_FLAT)
                valign = Align.CENTER
                sensitive = false
                tooltipText = "Custom URL: ${providerSetting.baseUrl}"
            })
        }

        // Delete button (only for non-default providers)
        if (providerSetting.id != DEFAULT_VOICE_MODEL_PROVIDER_ID) {
            val deleteButton = Button.fromIconName("user-trash-symbolic").apply {
                addCssClass(STYLE_CLASS_FLAT)
                valign = Align.CENTER
                addCssClass(STYLE_CLASS_DESTRUCTIVE_ACTION)
                onClicked {
                    providersListBox.remove(row)
                    onProviderDeleted(providerSetting.id)
                }
            }
            row.addSuffix(deleteButton)
        }

        providersListBox.append(row)
    }

    private fun onProviderDeleted(providerId: String) {
        val currentProviders = viewModel.getVoiceModelProviders()
        val updatedProviders = currentProviders.filter { it.id != providerId }
        logger.info("Removing provider, total is now ${updatedProviders.size} entries.")
        viewModel.setVoiceModelProviders(updatedProviders)
        activeProviderComboRow.updateProviders(updatedProviders)
        updateAddProviderButtonState()
    }

    private fun updateAddProviderButtonState() {
        val providers = viewModel.getVoiceModelProviders()
        // Subtract 1 to account for the default provider when checking the limit
        val customProviderCount = providers.count { it.id != DEFAULT_VOICE_MODEL_PROVIDER_ID }
        val atLimit = customProviderCount >= MAX_VOICE_MODEL_PROVIDERS
        addProviderButton.sensitive = !atLimit
    }

    /*
     * Dialog logic
     */

    private fun showAddProviderDialog() {
        val existingNames = viewModel.getVoiceModelProviders().map { it.name }.toSet()
        val existingCredentials = viewModel.getCredentials()
        val dialog = AddVoiceModelProviderDialog(existingNames, existingCredentials) { provider ->
            onProviderAdded(provider)
        }

        dialog.present(this)
    }

    private fun onProviderAdded(provider: VoiceModelProviderSetting) {
        val currentProviders = viewModel.getVoiceModelProviders()
        val updatedProviders = currentProviders + provider
        logger.info("Adding provider, total is now ${updatedProviders.size} entries.")
        viewModel.setVoiceModelProviders(updatedProviders)
        addProviderToUI(provider)
        activeProviderComboRow.updateProviders(updatedProviders)
        updateAddProviderButtonState()
    }
}
