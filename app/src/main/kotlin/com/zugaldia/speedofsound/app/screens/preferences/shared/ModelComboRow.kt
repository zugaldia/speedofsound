package com.zugaldia.speedofsound.app.screens.preferences.shared

import com.zugaldia.speedofsound.core.models.SelectableModel
import org.gnome.adw.ComboRow
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
) : ComboRow() {
    private val logger = LoggerFactory.getLogger(ModelComboRow::class.java)

    private var currentModels: Map<String, T> = emptyMap()

    init {
        title = rowTitle
        subtitle = rowSubtitle
        useSubtitle = false // Do not use the _current value_ as the subtitle
        enableSearch = true // Some providers list *many* models, so allow searching
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
        val modelNames = modelList.map { it.name }
        model = StringList(modelNames.toTypedArray())

        val selectedModelId = getCurrentModelId()
        val predefinedIndex = modelList.indexOfFirst { it.id == selectedModelId }
        if (predefinedIndex >= 0) {
            selected = predefinedIndex
        } else if (modelList.isNotEmpty()) {
            selected = 0 // Default to the first model
        }
    }

    /**
     * Set up notification callbacks. Call this after all widgets are initialized.
     */
    fun setupNotifications() {
        onNotify("selected") {
            val selectedIndex = selected
            val modelList = currentModels.values.toList()
            if (selectedIndex >= 0 && selectedIndex < modelList.size) {
                val selectedModel = modelList[selectedIndex]
                logger.info("Model changed to ${selectedModel.name} (${selectedModel.id})")
                onModelIdSelected(selectedModel.id)
            }
        }
    }
}
