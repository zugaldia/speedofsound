package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.core.models.text.TextModel
import org.gnome.adw.ComboRow
import org.gnome.adw.EntryRow
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

/**
 * Manager for model selection that handles both predefined and custom models.
 */
class ModelComboRow(
    rowTitle: String,
    rowSubtitle: String,
    private val getModels: () -> Map<String, TextModel>,
    private val getCurrentModelId: () -> String,
    private val onModelIdSelected: (String) -> Unit
) {
    private val logger = LoggerFactory.getLogger(ModelComboRow::class.java)

    private var currentModels: Map<String, TextModel> = emptyMap()
    private var isCustomMode = false

    val comboRow: ComboRow = ComboRow().apply {
        title = rowTitle
        subtitle = rowSubtitle
        useSubtitle = false // Do not use the _current value_ as the subtitle
        enableSearch = false
    }

    val customEntryRow: EntryRow = EntryRow().apply {
        title = "Custom Model ID"
        visible = false
    }

    companion object {
        private const val CUSTOM_OPTION = "Custom..."
    }

    init {
        refreshComboRows()
    }

    /**
     * Refresh the list of available models on initialization and when the provider changes.
     */
    fun refreshComboRows() {
        currentModels = getModels()
        val modelList = currentModels.values.toList()
        val modelNames = modelList.map { it.name }.toMutableList()
        modelNames.add(CUSTOM_OPTION)

        val stringList = StringList(modelNames.toTypedArray())
        comboRow.model = stringList

        val selectedModelId = getCurrentModelId()
        val predefinedIndex = modelList.indexOfFirst { it.id == selectedModelId }
        if (predefinedIndex >= 0) {
            comboRow.selected = predefinedIndex // Select predefined model
            isCustomMode = false
            customEntryRow.visible = false
        } else {
            comboRow.selected = modelNames.size - 1 // Select "Custom..."
            customEntryRow.text = selectedModelId
            isCustomMode = true
            customEntryRow.visible = true
        }
    }

    /**
     * Set up notification callbacks. Call this after all widgets are initialized.
     */
    fun setupNotifications() {
        comboRow.onNotify("selected") {
            val selectedIndex = comboRow.selected
            val modelList = currentModels.values.toList()
            val isCustomSelected = selectedIndex == modelList.size
            if (isCustomSelected) {
                isCustomMode = true
                customEntryRow.visible = true
                customEntryRow.grabFocus()
                val customId = customEntryRow.text.trim()
                if (customId.isNotEmpty()) {
                    logger.info("Custom model selected: $customId")
                    onModelIdSelected(customId)
                }
            } else if (selectedIndex >= 0 && selectedIndex < modelList.size) {
                val selectedModel = modelList[selectedIndex]
                isCustomMode = false
                customEntryRow.visible = false
                logger.info("Model changed to ${selectedModel.name} (${selectedModel.id})")
                onModelIdSelected(selectedModel.id)
            }
        }

        customEntryRow.onNotify("text") {
            if (isCustomMode) {
                val customModelId = customEntryRow.text.trim()
                if (customModelId.isNotEmpty()) {
                    onModelIdSelected(customModelId)
                }
            }
        }
    }
}
