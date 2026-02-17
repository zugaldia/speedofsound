package com.zugaldia.speedofsound.app.screens.preferences.voice

import com.zugaldia.speedofsound.app.DEFAULT_ADD_PROVIDER_DIALOG_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_ADD_PROVIDER_DIALOG_WIDTH
import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_MARGIN
import com.zugaldia.speedofsound.app.MAX_PROVIDER_CONFIG_NAME_LENGTH
import com.zugaldia.speedofsound.app.STYLE_CLASS_ACCENT
import com.zugaldia.speedofsound.app.STYLE_CLASS_ERROR
import com.zugaldia.speedofsound.app.STYLE_CLASS_SUCCESS
import com.zugaldia.speedofsound.app.STYLE_CLASS_SUGGESTED_ACTION
import com.zugaldia.speedofsound.app.STYLE_CLASS_WARNING
import com.zugaldia.speedofsound.core.desktop.settings.CredentialSetting
import com.zugaldia.speedofsound.core.desktop.settings.VoiceModelProviderSetting
import com.zugaldia.speedofsound.core.generateUniqueId
import com.zugaldia.speedofsound.core.isValidUrl
import com.zugaldia.speedofsound.core.plugins.asr.DEFAULT_ASR_SHERPA_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.asr.AsrProvider
import com.zugaldia.speedofsound.core.plugins.asr.getModelsForProvider
import org.gnome.adw.ComboRow
import org.gnome.adw.Dialog
import org.gnome.adw.EntryRow
import org.gnome.adw.PreferencesGroup
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Label
import org.gnome.gtk.Orientation
import org.gnome.gtk.StringList
import org.gnome.pango.WrapMode
import org.slf4j.LoggerFactory

@Suppress("TooManyFunctions")
class AddVoiceModelProviderDialog(
    private val existingNames: Set<String>,
    private val existingCredentials: List<CredentialSetting>,
    private val onProviderAdded: (VoiceModelProviderSetting) -> Unit
) : Dialog() {
    private val logger = LoggerFactory.getLogger(AddVoiceModelProviderDialog::class.java)

    private val nameEntry: EntryRow
    private val providerComboRow: ProviderComboRow
    private val modelComboRow: ModelComboRow
    private val credentialComboRow: ComboRow
    private val baseUrlEntry: BaseUrlEntryRow
    private val addButton: Button
    private val messageLabel: Label

    // Default to Sherpa (bundled with the JAR)
    private var selectedProvider: AsrProvider = AsrProvider.SHERPA
    private var selectedModelId: String = DEFAULT_ASR_SHERPA_MODEL_ID
    private var selectedCredentialId: String? = null

    init {
        title = "Add Voice Model Provider"
        contentWidth = DEFAULT_ADD_PROVIDER_DIALOG_WIDTH
        contentHeight = DEFAULT_ADD_PROVIDER_DIALOG_HEIGHT

        nameEntry = EntryRow().apply {
            title = "Configuration Name"
        }

        providerComboRow = ProviderComboRow(
            rowTitle = "Provider",
            rowSubtitle = "Select the ASR provider",
            getCurrentProvider = { selectedProvider },
            onProviderSelected = { provider: AsrProvider ->
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
            title = "Credentials"
            subtitle = "Select a credential (optional)"
            enableSearch = false
        }

        baseUrlEntry = BaseUrlEntryRow(
            onTextChanged = { updateAddButtonState() }
        )

        val preferencesGroup = PreferencesGroup().apply {
            title = "Provider Configuration"
            description = "Configure a voice model provider for speech recognition"
            vexpand = false
            add(nameEntry)
            add(providerComboRow)
            add(credentialComboRow)
            add(modelComboRow)
            add(baseUrlEntry)
        }

        val cancelButton = Button.withLabel("Cancel").apply {
            onClicked { closeDialog() }
        }

        addButton = Button.withLabel("Add").apply {
            addCssClass(STYLE_CLASS_SUGGESTED_ACTION)
            sensitive = false
            onClicked {
                if (validateAndCreateProvider()) {
                    closeDialog()
                }
            }
        }

        val buttonBox = Box(Orientation.HORIZONTAL, DEFAULT_BOX_SPACING).apply {
            halign = Align.END
            valign = Align.END
            append(cancelButton)
            append(addButton)
        }

        messageLabel = Label("").apply {
            vexpand = true
            halign = Align.CENTER
            valign = Align.CENTER
            wrap = true
            wrapMode = WrapMode.WORD_CHAR
            marginStart = DEFAULT_MARGIN
            marginEnd = DEFAULT_MARGIN
        }

        val contentBox = Box(Orientation.VERTICAL, DEFAULT_BOX_SPACING).apply {
            marginTop = DEFAULT_MARGIN
            marginBottom = DEFAULT_MARGIN
            marginStart = DEFAULT_MARGIN
            marginEnd = DEFAULT_MARGIN
            vexpand = true
            append(preferencesGroup)
            append(messageLabel)
            append(buttonBox)
        }

        child = contentBox

        // Initialize state
        loadCredentialList()
        refreshDialog()

        // Set up notifications after all widgets are initialized
        providerComboRow.setupNotifications()
        modelComboRow.setupNotifications()
        baseUrlEntry.setupNotifications()
        setupNotifications()
    }

    private fun setupNotifications() {
        nameEntry.onNotify("text") { updateAddButtonState() }
        credentialComboRow.onNotify("selected") {
            selectedCredentialId = getSelectedCredentialId()
            updateAddButtonState()
        }
    }

    private fun refreshDialog() {
        baseUrlEntry.clear()
        updateMessageLabel("")
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
        val baseUrl = baseUrlEntry.getBaseUrl()
        val modelId = selectedModelId
        addButton.sensitive = validateInput(name, baseUrl, modelId)
    }

    private fun updateMessageLabel(message: String, styleClass: String = STYLE_CLASS_ACCENT) {
        messageLabel.label = message
        messageLabel.removeCssClass(STYLE_CLASS_ACCENT)
        messageLabel.removeCssClass(STYLE_CLASS_SUCCESS)
        messageLabel.removeCssClass(STYLE_CLASS_WARNING)
        messageLabel.removeCssClass(STYLE_CLASS_ERROR)
        messageLabel.addCssClass(styleClass)
    }

    @Suppress("ReturnCount")
    private fun validateInput(name: String, baseUrl: String?, modelId: String?): Boolean {
        if (name.isEmpty()) { return false }
        if (name.length > MAX_PROVIDER_CONFIG_NAME_LENGTH) { return false }
        if (existingNames.contains(name)) { return false }
        if (modelId == null) { return false }
        if (baseUrl != null && !isValidUrl(baseUrl)) { return false }
        return true
    }

    private fun validateAndCreateProvider(): Boolean {
        val name = nameEntry.text.trim()
        val baseUrl = baseUrlEntry.getBaseUrl()
        val modelId = selectedModelId
        if (!validateInput(name, baseUrl, modelId)) { return false }

        val config = VoiceModelProviderSetting(
            id = generateUniqueId(),
            name = name,
            provider = selectedProvider,
            credentialId = selectedCredentialId,
            baseUrl = baseUrl,
            modelId = modelId
        )

        onProviderAdded(config)
        return true
    }

    private fun closeDialog() {
        close()
    }
}
