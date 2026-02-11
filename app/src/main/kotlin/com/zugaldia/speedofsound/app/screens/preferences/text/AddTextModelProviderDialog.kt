package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.app.DEFAULT_ADD_PROVIDER_DIALOG_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_ADD_PROVIDER_DIALOG_WIDTH
import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_MARGIN
import com.zugaldia.speedofsound.app.MAX_PROVIDER_CONFIG_NAME_LENGTH
import com.zugaldia.speedofsound.core.desktop.settings.CredentialSetting
import com.zugaldia.speedofsound.core.desktop.settings.TextModelProviderSetting
import com.zugaldia.speedofsound.core.generateUniqueId
import com.zugaldia.speedofsound.core.isValidUrl
import com.zugaldia.speedofsound.core.models.text.TextModel
import com.zugaldia.speedofsound.core.plugins.llm.DEFAULT_ANTHROPIC_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.llm.LlmProvider
import com.zugaldia.speedofsound.core.plugins.llm.getModelsForProvider
import org.gnome.adw.ComboRow
import org.gnome.adw.Dialog
import org.gnome.adw.EntryRow
import org.gnome.adw.PreferencesGroup
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Orientation
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

class AddTextModelProviderDialog(
    private val existingNames: Set<String>,
    private val existingCredentials: List<CredentialSetting>,
    private val onProviderAdded: (TextModelProviderSetting) -> Unit
) : Dialog() {
    private val logger = LoggerFactory.getLogger(AddTextModelProviderDialog::class.java)

    private val nameEntry: EntryRow
    private val providerComboRow: ProviderComboRow
    private val modelComboRow: ModelComboRow
    private val credentialComboRow: ComboRow
    private val baseUrlEntry: EntryRow
    private val addButton: Button

    // Default to Anthropic (first in alphabetical order)
    private var selectedProvider: LlmProvider = LlmProvider.ANTHROPIC
    private var selectedModelId: String = DEFAULT_ANTHROPIC_MODEL_ID
    private var selectedCredentialId: String? = null

    init {
        title = "Add Text Model Provider"
        contentWidth = DEFAULT_ADD_PROVIDER_DIALOG_WIDTH
        contentHeight = DEFAULT_ADD_PROVIDER_DIALOG_HEIGHT

        nameEntry = EntryRow().apply {
            title = "Configuration Name"
        }

        providerComboRow = ProviderComboRow(
            rowTitle = "Provider",
            rowSubtitle = "Select the LLM provider",
            getCurrentProvider = { selectedProvider },
            onProviderSelected = { provider: LlmProvider ->
                selectedProvider = provider
                refreshDialog()
            }
        )

        modelComboRow = ModelComboRow(
            rowTitle = "Model",
            rowSubtitle = "Select the model to use",
            getModels = { getModelsForProvider(selectedProvider) },
            getCurrentModelId = { selectedModelId },
            onModelIdSelected = { modelId: String ->
                selectedModelId = modelId
                updateAddButtonState()
            }
        )

        credentialComboRow = ComboRow().apply {
            title = "Credential"
            subtitle = "Select a credential (optional)"
            enableSearch = false
        }

        baseUrlEntry = EntryRow().apply {
            title = "Base URL (optional)"
        }

        val preferencesGroup = PreferencesGroup().apply {
            title = "Provider Configuration"
            description = "Configure a text model provider for text processing"
            vexpand = true
            add(nameEntry)
            add(providerComboRow)
            add(modelComboRow.comboRow)
            add(modelComboRow.customEntryRow)
            add(credentialComboRow)
            add(baseUrlEntry)
        }

        val cancelButton = Button.withLabel("Cancel").apply {
            onClicked { close() }
        }

        addButton = Button.withLabel("Add").apply {
            addCssClass("suggested-action")
            sensitive = false
            onClicked {
                if (validateAndCreateProvider()) {
                    close()
                }
            }
        }

        val buttonBox = Box(Orientation.HORIZONTAL, DEFAULT_BOX_SPACING).apply {
            halign = Align.END
            valign = Align.END
            append(cancelButton)
            append(addButton)
        }

        val contentBox = Box(Orientation.VERTICAL, DEFAULT_BOX_SPACING).apply {
            marginTop = DEFAULT_MARGIN
            marginBottom = DEFAULT_MARGIN
            marginStart = DEFAULT_MARGIN
            marginEnd = DEFAULT_MARGIN
            vexpand = true
            append(preferencesGroup)
            append(buttonBox)
        }

        child = contentBox

        // Initialize state
        loadCredentialList()
        refreshDialog()

        // Set up notifications after all widgets are initialized
        providerComboRow.setupNotifications()
        modelComboRow.setupNotifications()
        setupNotifications()
    }

    private fun setupNotifications() {
        nameEntry.onNotify("text") { updateAddButtonState() }
        baseUrlEntry.onNotify("text") { updateAddButtonState() }
        credentialComboRow.onNotify("selected") {
            selectedCredentialId = getSelectedCredentialId()
            updateAddButtonState()
        }
    }

    private fun refreshDialog() {
        baseUrlEntry.text = ""
        modelComboRow.refreshComboRows()
        updateAddButtonState()
    }

    private fun loadCredentialList() {
        val options = mutableListOf("None")
        options.addAll(existingCredentials.map { it.name })
        credentialComboRow.model = StringList(options.toTypedArray())
        credentialComboRow.selected = 0
    }

    private fun getSelectedCredentialId(): String? {
        val selectedIndex = credentialComboRow.selected
        return if (selectedIndex > 0 && selectedIndex <= existingCredentials.size) {
            existingCredentials[selectedIndex - 1].id
        } else {
            null // Index 0 is "None"
        }
    }

    private fun updateAddButtonState() {
        val name = nameEntry.text.trim()
        val baseUrl = baseUrlEntry.text.trim()
        val modelId = selectedModelId
        addButton.sensitive = validateInput(name, baseUrl, modelId)
    }

    @Suppress("ReturnCount")
    private fun validateInput(name: String, baseUrl: String, modelId: String?): Boolean {
        if (name.isEmpty()) { return false }
        if (name.length > MAX_PROVIDER_CONFIG_NAME_LENGTH) { return false }
        if (existingNames.contains(name)) { return false }
        if (modelId == null) { return false }
        if (baseUrl.isNotEmpty() && !isValidUrl(baseUrl)) { return false }
        return true
    }

    private fun validateAndCreateProvider(): Boolean {
        val name = nameEntry.text.trim()
        val baseUrl = baseUrlEntry.text.trim()
        val modelId = selectedModelId
        if (!validateInput(name, baseUrl, modelId)) { return false }

        val config = TextModelProviderSetting(
            id = generateUniqueId(),
            name = name,
            provider = selectedProvider,
            credentialId = selectedCredentialId,
            baseUrl = baseUrl.ifEmpty { null },
            model = modelId
        )

        onProviderAdded(config)
        return true
    }
}
