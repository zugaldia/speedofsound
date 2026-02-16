package com.zugaldia.speedofsound.app.screens.preferences.text

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
import com.zugaldia.speedofsound.core.desktop.settings.TextModelProviderSetting
import com.zugaldia.speedofsound.core.generateUniqueId
import com.zugaldia.speedofsound.core.isValidUrl
import com.zugaldia.speedofsound.core.models.text.TextModel
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlm
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.DEFAULT_LLM_ANTHROPIC_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlm
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.LlmPlugin
import com.zugaldia.speedofsound.core.plugins.llm.LlmProvider
import com.zugaldia.speedofsound.core.plugins.llm.OpenAiLlm
import com.zugaldia.speedofsound.core.plugins.llm.OpenAiLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.getModelsForProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.gnome.adw.ComboRow
import org.gnome.adw.Dialog
import org.gnome.adw.EntryRow
import org.gnome.adw.PreferencesGroup
import org.gnome.glib.GLib
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Label
import org.gnome.gtk.Orientation
import org.gnome.gtk.StringList
import org.gnome.pango.WrapMode
import org.slf4j.LoggerFactory

@Suppress("TooManyFunctions")
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
    private val fetchButton: Button
    private val baseUrlEntry: BaseUrlEntryRow
    private val addButton: Button
    private val messageLabel: Label

    // Default to Anthropic (first in alphabetical order)
    private var selectedProvider: LlmProvider = LlmProvider.ANTHROPIC
    private var selectedModelId: String = DEFAULT_LLM_ANTHROPIC_MODEL_ID
    private var selectedCredentialId: String? = null
    private var fetchedModels: List<TextModel>? = null

    private val dialogScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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

        fetchButton = Button.fromIconName("view-refresh-symbolic").apply {
            tooltipText = "Fetch available models from the API"
            onClicked { fetchModels() }
        }

        modelComboRow = ModelComboRow(
            rowTitle = "Model",
            rowSubtitle = "Select the model to use",
            getModels = { getModelsForProvider(selectedProvider) },
            getCurrentModelId = { selectedModelId },
            onModelIdSelected = { modelId: String ->
                selectedModelId = modelId
                updateAddButtonState()
            }
        ).apply {
            addSuffix(fetchButton)
        }

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
            description = "Configure a text model provider for text processing"
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
        fetchedModels = null
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

    private fun fetchModels() {
        val apiKey = getSelectedCredentialApiKey()
        val baseUrl = baseUrlEntry.getBaseUrl()

        updateMessageLabel("Fetching models...", STYLE_CLASS_ACCENT)
        fetchButton.sensitive = false

        dialogScope.launch(Dispatchers.IO) {
            val plugin = createTemporaryPlugin(selectedProvider, apiKey, baseUrl)
            val result = runCatching {
                plugin.enable()
                plugin.listModels().getOrThrow()
            }

            runCatching { plugin.shutdown() }
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                result.fold(
                    onSuccess = { models -> onModelsFetched(models) },
                    onFailure = { error -> onFetchError(error) }
                )
                fetchButton.sensitive = true
                false
            }
        }
    }

    private fun createTemporaryPlugin(provider: LlmProvider, apiKey: String?, baseUrl: String?): LlmPlugin<*> =
        when (provider) {
            LlmProvider.ANTHROPIC -> AnthropicLlm(AnthropicLlmOptions(apiKey = apiKey, baseUrl = baseUrl))
            LlmProvider.GOOGLE -> GoogleLlm(GoogleLlmOptions(apiKey = apiKey, baseUrl = baseUrl))
            LlmProvider.OPENAI -> OpenAiLlm(OpenAiLlmOptions(apiKey = apiKey, baseUrl = baseUrl))
    }

    private fun getSelectedCredentialApiKey(): String? = selectedCredentialId?.let { credId ->
        existingCredentials.find { it.id == credId }?.value
    }

    private fun onModelsFetched(models: List<TextModel>) {
        fetchedModels = models
        if (models.isEmpty()) {
            updateMessageLabel("No models found", STYLE_CLASS_ERROR)
        } else {
            updateMessageLabel("Found ${models.size} models", STYLE_CLASS_SUCCESS)
            modelComboRow.refreshComboRows(models)
        }
    }

    private fun onFetchError(error: Throwable) {
        val errorMsg = error.message ?: "Unknown error"
        updateMessageLabel(errorMsg, STYLE_CLASS_ERROR)
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

        val config = TextModelProviderSetting(
            id = generateUniqueId(),
            name = name,
            provider = selectedProvider,
            credentialId = selectedCredentialId,
            baseUrl = baseUrl,
            model = modelId
        )

        onProviderAdded(config)
        return true
    }

    private fun closeDialog() {
        dialogScope.cancel()
        close()
    }
}
