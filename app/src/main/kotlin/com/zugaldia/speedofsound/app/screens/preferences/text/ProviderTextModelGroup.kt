package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.models.text.TextModel
import org.gnome.adw.ComboRow
import org.gnome.adw.EntryRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.SwitchRow
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

const val CUSTOM_MODEL_OPTION_TEXT = "Custom..."

/**
 * Base class for text model provider preference groups.
 * Handles common UI logic for model selection, API key toggle, and base URL configuration.
 */
open class ProviderTextModelGroup(
    protected val viewModel: PreferencesViewModel,
    private val providerName: String,
    private val providerDescription: String,
    private val presetModels: List<TextModel>,
    private val getModelName: () -> String,
    private val setModelName: (String) -> Boolean,
    private val getUseApiKey: () -> Boolean,
    private val setUseApiKey: (Boolean) -> Boolean,
    private val getBaseUrl: () -> String,
    private val setBaseUrl: (String) -> Boolean
) : PreferencesGroup() {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val modelNames = presetModels.map { it.name }
    private val customOptionIndex = modelNames.size

    init {
        title = providerName
        description = providerDescription

        val customModelEntryRow = EntryRow().apply {
            title = "Custom Model Name"
            visible = false
            onNotify("text") {
                val modelName = text
                logger.info("$providerName custom model name updated: $modelName")
                setModelName(modelName)
            }
        }

        val modelComboRow = ComboRow().apply {
            title = "Model"
            subtitle = "Select a preset or use a custom model"
            model = StringList((modelNames + CUSTOM_MODEL_OPTION_TEXT).toTypedArray())

            val savedModelId = getModelName()
            val presetIndex = presetModels.indexOfFirst { it.id == savedModelId }
            if (presetIndex >= 0) {
                selected = presetIndex
            } else if (savedModelId.isNotEmpty()) {
                selected = customOptionIndex
                customModelEntryRow.text = savedModelId
                customModelEntryRow.visible = true
            }

            onNotify("selected") {
                val selectedIndex = selected
                if (selectedIndex == customOptionIndex) {
                    customModelEntryRow.visible = true
                    val customValue = customModelEntryRow.text
                    if (customValue.isNotEmpty()) {
                        logger.info("$providerName model switched to custom: $customValue")
                        setModelName(customValue)
                    }
                } else if (selectedIndex in presetModels.indices) {
                    customModelEntryRow.visible = false
                    val modelId = presetModels[selectedIndex].id
                    logger.info("$providerName model selected: ${presetModels[selectedIndex].name} (id: $modelId)")
                    setModelName(modelId)
                }
            }
        }

        val useApiKeySwitch = SwitchRow().apply {
            title = "Use API Key"
            subtitle = "Use the API key defined in the Cloud Providers page (not needed for local models)"
            active = getUseApiKey()
            onNotify("active") {
                val enabled = active
                logger.info("$providerName text use API key: $enabled")
                setUseApiKey(enabled)
            }
        }

        val baseUrlRow = EntryRow().apply {
            title = "Base URL"
            text = getBaseUrl()
            onNotify("text") {
                val baseUrl = text
                logger.info("$providerName base URL updated: $baseUrl")
                setBaseUrl(baseUrl)
            }
        }

        add(modelComboRow)
        add(customModelEntryRow)
        add(useApiKeySwitch)
        add(baseUrlRow)
    }
}
