package com.zugaldia.speedofsound.app.screens.preferences.library

import com.zugaldia.speedofsound.app.STYLE_CLASS_BOXED_LIST
import com.zugaldia.speedofsound.app.STYLE_CLASS_DIM_LABEL
import com.zugaldia.speedofsound.app.STYLE_CLASS_FLAT
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.desktop.settings.SUPPORTED_LOCAL_ASR_MODELS
import com.zugaldia.speedofsound.core.models.voice.ModelManager
import com.zugaldia.speedofsound.core.models.voice.ModelManagerEvent
import com.zugaldia.speedofsound.core.plugins.asr.DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.gnome.adw.ActionRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.glib.GLib
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.ListBox
import org.gnome.gtk.Orientation
import org.gnome.gtk.SelectionMode
import org.gnome.gtk.Spinner
import org.slf4j.LoggerFactory

class ModelLibraryPage(
    private val viewModel: PreferencesViewModel,
    private val onOperationsStateChanged: (Boolean) -> Unit
) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(ModelLibraryPage::class.java)
    private val modelManager = ModelManager()
    private val modelsListBox: ListBox
    private val pageScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val deletingModels = mutableSetOf<String>()
    private val downloadingModels = mutableSetOf<String>()
    private val modelRows = mutableMapOf<String, ActionRow>()

    init {
        title = "Model Library"
        iconName = "folder-download-symbolic"

        modelsListBox = ListBox().apply {
            addCssClass(STYLE_CLASS_BOXED_LIST)
            selectionMode = SelectionMode.NONE
        }

        val modelsGroup = PreferencesGroup().apply {
            title = "Available Voice Models"
            description = "Download models for local speech transcription."
            add(modelsListBox)
        }

        add(modelsGroup)
        refreshModels()
        observeModelEvents()
    }

    private fun refreshModels() {
        // Clear existing rows. We should investigate a better approach, I believe this is the root cause for some
        // (java:2230969): Gtk-CRITICAL **: 07:20:05.435: gtk_widget_get_can_focus: assertion 'GTK_IS_WIDGET (widget)'
        // that we're seeing when downloading/deleting a model (although everything seems to work).
        modelRows.clear()
        while (modelsListBox.firstChild != null) {
            modelsListBox.remove(modelsListBox.firstChild)
        }

        SUPPORTED_LOCAL_ASR_MODELS.values
            .sortedBy { it.dataSizeMegabytes }
            .forEach { model ->
                val isDownloaded = modelManager.isModelDownloaded(model.id)
                val isDeleting = deletingModels.contains(model.id)
                val isDownloading = downloadingModels.contains(model.id)
                val isOperationInProgress = isDeleting || isDownloading
                val row = ActionRow().apply {
                    title = model.name
                    subtitle = when {
                        isDeleting -> "Deleting..."
                        isDownloading -> "Downloading..."
                        else -> "${model.dataSizeMegabytes} MB"
                    }

                    // Gray out models that haven't been downloaded
                    if (!isDownloaded && !isOperationInProgress) {
                        addCssClass(STYLE_CLASS_DIM_LABEL)
                    }

                    // Add spinner as the first suffix - create a new one for each row
                    // Replace with Adw.Spinner when we target Adw >= 1.6
                    if (isOperationInProgress) {
                        val spinner = Spinner().apply {
                            spinning = true
                            valign = Align.CENTER
                            tooltipText = "Operation in progress..."
                        }
                        addSuffix(spinner)
                    }

                    // Add action buttons as the second suffix
                    val buttonBox = createActionButtons(model.id, isDownloaded)
                    addSuffix(buttonBox)
                }

                modelRows[model.id] = row
                modelsListBox.append(row)
            }
    }

    private fun handleDownloadModel(modelId: String) {
        if (downloadingModels.contains(modelId)) {
            logger.info("Model download already in progress: $modelId")
            return
        }

        logger.info("Download button clicked for model: $modelId")
        downloadingModels.add(modelId)
        notifyOperationsStateChanged()
        refreshModels()
        pageScope.launch { modelManager.downloadModel(modelId) }
    }

    private fun handleRemoveModel(modelId: String) {
        if (deletingModels.contains(modelId)) {
            logger.info("Model deletion already in progress: $modelId")
            return
        }

        logger.info("Remove button clicked for model: $modelId")
        deletingModels.add(modelId)
        notifyOperationsStateChanged()
        refreshModels()
        pageScope.launch { modelManager.deleteModel(modelId) }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createActionButtons(modelId: String, isDownloaded: Boolean): Box {
        val isDownloading = downloadingModels.contains(modelId)
        val isDeleting = deletingModels.contains(modelId)
        val isOperationInProgress = isDownloading || isDeleting
        val downloadButton = Button.fromIconName("folder-download-symbolic").apply {
            tooltipText = when {
                isDownloading -> "Downloading..."
                isOperationInProgress -> "Operation in progress"
                isDownloaded -> "Already downloaded"
                else -> "Download model"
            }
            addCssClass(STYLE_CLASS_FLAT)
            valign = Align.CENTER
            sensitive = !isDownloaded && !isOperationInProgress
            onClicked { handleDownloadModel(modelId) }
        }

        val isDefaultModel = modelId == DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
        val removeButton = Button.fromIconName("user-trash-symbolic").apply {
            tooltipText = when {
                isDefaultModel -> "Cannot delete default model"
                isDeleting -> "Deleting..."
                isOperationInProgress -> "Operation in progress"
                else -> "Remove model"
            }
            addCssClass(STYLE_CLASS_FLAT)
            valign = Align.CENTER
            sensitive = isDownloaded && !isOperationInProgress && !isDefaultModel
            onClicked { handleRemoveModel(modelId) }
        }

        return Box(Orientation.HORIZONTAL, 0).apply {
            append(downloadButton)
            append(removeButton)
        }
    }

    @Suppress("LongMethod")
    private fun observeModelEvents() {
        pageScope.launch {
            modelManager.events.collect { event ->
                when (event) {
                    is ModelManagerEvent.Progress -> {
                        when (event.operation) {
                            ModelManagerEvent.Progress.Operation.DOWNLOADING -> {
                                val percentage = event.percentage
                                val subtitle = if (percentage != null) {
                                    "Downloading (${percentage.toInt()}%)..."
                                } else {
                                    event.message
                                }
                                updateRowSubtitle(event.modelId, subtitle)
                            }

                            ModelManagerEvent.Progress.Operation.VERIFYING_CHECKSUM -> {
                                updateRowSubtitle(event.modelId, "Verifying integrity...")
                            }

                            ModelManagerEvent.Progress.Operation.EXTRACTING -> {
                                updateRowSubtitle(event.modelId, "Extracting archive...")
                            }

                            ModelManagerEvent.Progress.Operation.COPYING_FILES -> {
                                updateRowSubtitle(event.modelId, "Installing files...")
                            }

                            ModelManagerEvent.Progress.Operation.DELETING -> {
                                updateRowSubtitle(event.modelId, event.message)
                            }
                        }
                    }

                    is ModelManagerEvent.Completed -> {
                        when (event.operation) {
                            ModelManagerEvent.Completed.Operation.DOWNLOAD -> {
                                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                                    downloadingModels.remove(event.modelId)
                                    logger.info("Successfully downloaded model: ${event.modelId}")
                                    notifyOperationsStateChanged()
                                    refreshModels()
                                    false
                                }
                            }

                            ModelManagerEvent.Completed.Operation.DELETE -> {
                                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                                    deletingModels.remove(event.modelId)
                                    logger.info("Successfully deleted model: ${event.modelId}")
                                    notifyOperationsStateChanged()
                                    refreshModels()
                                    false
                                }
                            }
                        }
                    }

                    is ModelManagerEvent.Error -> {
                        when (event.operation) {
                            ModelManagerEvent.Error.Operation.DOWNLOAD -> {
                                updateRowSubtitle(event.modelId, "Error: ${event.message}")
                                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                                    downloadingModels.remove(event.modelId)
                                    logger.error("Failed to download model: ${event.modelId}", event.exception)
                                    notifyOperationsStateChanged()
                                    refreshModels()
                                    false
                                }
                            }

                            ModelManagerEvent.Error.Operation.DELETE -> {
                                updateRowSubtitle(event.modelId, "Error: ${event.message}")
                                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                                    deletingModels.remove(event.modelId)
                                    logger.error("Failed to delete model: ${event.modelId}", event.exception)
                                    notifyOperationsStateChanged()
                                    refreshModels()
                                    false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateRowSubtitle(modelId: String, subtitle: String) {
        GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
            modelRows[modelId]?.subtitle = subtitle
            false
        }
    }

    fun hasOperationsInProgress(): Boolean {
        return downloadingModels.isNotEmpty() || deletingModels.isNotEmpty()
    }

    private fun notifyOperationsStateChanged() {
        val hasOperations = hasOperationsInProgress()
        onOperationsStateChanged(hasOperations)
    }

    fun shutdown() {
        logger.info("Shutting down ModelLibraryPage")
        pageScope.cancel()
    }
}
