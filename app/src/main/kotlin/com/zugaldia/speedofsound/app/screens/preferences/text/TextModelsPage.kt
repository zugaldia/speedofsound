package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.MAX_TEXT_MODEL_PROVIDERS
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.desktop.settings.TextModelProviderSetting
import org.gnome.adw.ActionRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SwitchRow
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Label
import org.gnome.gtk.ListBox
import org.gnome.gtk.Orientation
import org.gnome.gtk.SelectionMode
import org.slf4j.LoggerFactory

class TextModelsPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(TextModelsPage::class.java)

    private val enableSwitch: SwitchRow
    private val activeProviderComboRow: ActiveProviderComboRow
    private val providersListBox: ListBox
    private val placeholderBox: Box
    private val addProviderButton: Button

    init {
        title = "Text Models"
        iconName = "accessories-text-editor-symbolic"

        enableSwitch = SwitchRow().apply {
            title = "Enable text processing"
            subtitle = "Process transcriptions with an LLM for improved results"
            active = viewModel.getTextProcessingEnabled()
        }

        activeProviderComboRow = ActiveProviderComboRow(
            getSelectedProviderId = { viewModel.getSelectedTextModelProviderId() },
            setSelectedProviderId = { viewModel.setSelectedTextModelProviderId(it) }
        )

        val textProcessingGroup = PreferencesGroup().apply {
            title = "Text Processing"
            description = "(Optional) Process transcriptions using a Large Language Model (LLM) to improve " +
                        "accuracy, grammar, and formatting."
            add(enableSwitch)
            add(activeProviderComboRow)
        }

        addProviderButton = Button.withLabel("Add Provider").apply {
            addCssClass("suggested-action")
            onClicked { showAddProviderDialog() }
        }

        providersListBox = ListBox().apply {
            addCssClass("boxed-list")
            marginTop = DEFAULT_BOX_SPACING
            selectionMode = SelectionMode.NONE
        }

        val placeholderLabel = Label("No providers configured").apply {
            addCssClass("dim-label")
            halign = Align.CENTER
        }

        placeholderBox = Box(Orientation.VERTICAL, 0).apply {
            vexpand = true
            halign = Align.FILL
            valign = Align.FILL

            // Add expanding spacers above and below to center the label vertically
            append(Box(Orientation.VERTICAL, 0).apply { vexpand = true })
            append(placeholderLabel)
            append(Box(Orientation.VERTICAL, 0).apply { vexpand = true })
        }

        val providersGroup = PreferencesGroup().apply {
            title = "Provider Configurations"
            description = "Configure LLM providers for text processing."
            add(addProviderButton)
            add(providersListBox)
            add(placeholderBox)
        }

        add(textProcessingGroup)
        add(providersGroup)
        loadInitialProviders()
        setupNotifications()
    }

    private fun loadInitialProviders() {
        val providers = viewModel.getTextModelProviders()
        providers.sortedBy { it.name.lowercase() }.forEach { provider -> addProviderToUI(provider) }
        activeProviderComboRow.updateProviders(providers)
        updatePlaceholderVisibility()
        updateActiveProviderSensitivity()
    }

    private fun setupNotifications() {
        activeProviderComboRow.setupNotifications()
        enableSwitch.onNotify("active") {
            val enabled = enableSwitch.active
            logger.info("Text processing enabled: $enabled")
            viewModel.setTextProcessingEnabled(enabled)
            updateActiveProviderSensitivity()
        }
    }

    private fun addProviderToUI(providerSetting: TextModelProviderSetting) {
        val providerLabel = providerSetting.provider.displayName
        val modelLabel = providerSetting.model
        val subtitle = "$providerLabel • $modelLabel"

        val row = ActionRow().apply {
            title = providerSetting.name
            this.subtitle = subtitle
        }

        // Credential indicator
        if (providerSetting.credentialId != null) {
            row.addSuffix(Button.fromIconName("dialog-password-symbolic").apply {
                addCssClass("flat")
                valign = Align.CENTER
                sensitive = false
                tooltipText = "Custom Credentials Set"
            })
        }

        // Base URL indicator
        if (providerSetting.baseUrl != null) {
            row.addSuffix(Button.fromIconName("network-server-symbolic").apply {
                addCssClass("flat")
                valign = Align.CENTER
                sensitive = false
                tooltipText = "Custom URL: ${providerSetting.baseUrl}"
            })
        }

        // Delete button
        val deleteButton = Button.fromIconName("user-trash-symbolic").apply {
            addCssClass("flat")
            valign = Align.CENTER
            addCssClass("destructive-action")
            onClicked {
                providersListBox.remove(row)
                onProviderDeleted(providerSetting.id)
            }
        }

        row.addSuffix(deleteButton)
        providersListBox.append(row)
    }

    private fun onProviderDeleted(providerId: String) {
        val currentProviders = viewModel.getTextModelProviders()
        val updatedProviders = currentProviders.filter { it.id != providerId }
        logger.info("Removing provider, total is now ${updatedProviders.size} entries.")
        viewModel.setTextModelProviders(updatedProviders)
        activeProviderComboRow.updateProviders(updatedProviders)
        updatePlaceholderVisibility()
        updateActiveProviderSensitivity()
    }

    private fun updatePlaceholderVisibility() {
        val providers = viewModel.getTextModelProviders()
        val hasProviders = providers.isNotEmpty()
        val atLimit = providers.size >= MAX_TEXT_MODEL_PROVIDERS
        providersListBox.visible = hasProviders
        placeholderBox.visible = !hasProviders
        addProviderButton.sensitive = !atLimit
    }

    private fun updateActiveProviderSensitivity() {
        val textProcessingEnabled = viewModel.getTextProcessingEnabled()
        val hasProviders = viewModel.getTextModelProviders().isNotEmpty()
        activeProviderComboRow.sensitive = textProcessingEnabled && hasProviders
    }

    /*
     * Dialog logic
     */

    private fun showAddProviderDialog() {
        val existingNames = viewModel.getTextModelProviders().map { it.name }.toSet()
        val existingCredentials = viewModel.getCredentials()
        val dialog = AddTextModelProviderDialog(existingNames, existingCredentials) { provider ->
            onProviderAdded(provider)
        }

        dialog.present(this)
    }

    private fun onProviderAdded(provider: TextModelProviderSetting) {
        val currentProviders = viewModel.getTextModelProviders()
        val updatedProviders = currentProviders + provider
        logger.info("Adding provider, total is now ${updatedProviders.size} entries.")
        viewModel.setTextModelProviders(updatedProviders)
        addProviderToUI(provider)
        activeProviderComboRow.updateProviders(updatedProviders)
        updatePlaceholderVisibility()
        updateActiveProviderSensitivity()
    }
}
