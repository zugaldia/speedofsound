package com.zugaldia.speedofsound.app.screens.preferences.shared

import com.zugaldia.speedofsound.core.models.SelectableModel
import org.gnome.adw.ComboRow
import org.gnome.adw.EntryRow
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

/**
 * Generic manager for model selection from a list of predefined models.
 * Works with any model type that implements SelectableModel.
 */
class ModelComboRow<T : SelectableModel>(
    rowTitle: String,
    rowSubtitle: String,
    private val getModels: () -> Map<String, T>,
    private val getCurrentModelId: () -> String,
    private val onModelIdSelected: (String) -> Unit
) {
    private val logger = LoggerFactory.getLogger(ModelComboRow::class.java)

    private var currentModels: Map<String, T> = emptyMap()

    companion object {
        private const val CUSTOM_MODEL_LABEL = "Custom..."
    }

    val comboRow = ComboRow()
    val customEntryRow = EntryRow().apply {
        title = "Custom Model ID"
        visible = false
    }

    init {
        comboRow.apply {
            title = rowTitle
            subtitle = rowSubtitle
            useSubtitle = false // Do not use the _current value_ as the subtitle
            enableSearch = true // Some providers list *many* models, so allow searching
        }
        refreshComboRows()
    }

    /**
     * Refresh the list of available models on initialization and when the provider changes.
     */
    fun refreshComboRows() {
        currentModels = getModels()
        refreshComboRowsInternal()
    }

    /**
     * Refresh the list of available models with a custom list (e.g., fetched from API).
     * Only applies the update if the list is non-empty.
     */
    fun refreshComboRows(models: List<T>) {
        if (models.isEmpty()) { return }
        currentModels = models.associateBy { it.id }
        refreshComboRowsInternal()
    }

    private fun refreshComboRowsInternal() {
        val modelList = currentModels.values.toList()
        val modelNames = modelList.map { it.name }.toMutableList()
        modelNames.add(CUSTOM_MODEL_LABEL) // Add the custom option at the end
        comboRow.model = StringList(modelNames.toTypedArray())

        val selectedModelId = getCurrentModelId()
        val predefinedIndex = modelList.indexOfFirst { it.id == selectedModelId }
        if (predefinedIndex >= 0) {
            comboRow.selected = predefinedIndex
            customEntryRow.visible = false
        } else if (modelList.isNotEmpty()) {
            comboRow.selected = modelList.size // Index of "Custom..." option
            customEntryRow.text = selectedModelId
            customEntryRow.visible = true
        } else {
            comboRow.selected = 0 // Default to the first model
            customEntryRow.visible = false
        }
    }

    /**
     * Set up notification callbacks. Call this after all widgets are initialized.
     */
    fun setupNotifications() {
        comboRow.onNotify("selected") {
            val selectedIndex = comboRow.selected
            val modelList = currentModels.values.toList()
            if (selectedIndex >= 0 && selectedIndex < modelList.size) {
                // Predefined model selected
                val selectedModel = modelList[selectedIndex]
                customEntryRow.visible = false
                onModelIdSelected(selectedModel.id)
            } else if (selectedIndex == modelList.size) {
                // Custom option
                customEntryRow.visible = true
                val customModelId = customEntryRow.text.trim()
                if (customModelId.isNotEmpty()) {
                    onModelIdSelected(customModelId)
                }
            }
        }

        customEntryRow.onNotify("text") {
            val customModelId = customEntryRow.text.trim()
            if (customModelId.isNotEmpty()) {
                onModelIdSelected(customModelId)
            }
        }
    }
}
