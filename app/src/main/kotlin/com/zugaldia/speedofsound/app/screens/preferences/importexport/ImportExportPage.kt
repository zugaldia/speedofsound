package com.zugaldia.speedofsound.app.screens.preferences.importexport

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.getDataDir
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
import org.gnome.gtk.Button
import org.gnome.gtk.Label

class ImportExportPage(viewModel: PreferencesViewModel, private val onImportSuccess: () -> Unit) : PreferencesPage() {
    private val manager = ImportExportManager(viewModel)
    private val pageScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val statusLabel: Label
    private val exportButton: Button
    private val importButton: Button

    init {
        title = "Import / Export"
        iconName = "document-send-symbolic"

        val exportFilePath = getDataDir().resolve(ImportExportManager.EXPORT_FILENAME)

        exportButton = Button.withLabel("Export").apply {
            valign = Align.CENTER
        }

        val exportRow = ActionRow().apply {
            title = "Export Preferences"
            subtitle = "File: $exportFilePath"
            addSuffix(exportButton)
        }

        val exportGroup = PreferencesGroup().apply {
            title = "Export"
            description = "Export your preferences to a file. Use it as a backup or to transfer your " +
                    "configuration to a different machine."
            add(exportRow)
        }

        importButton = Button.withLabel("Import").apply {
            valign = Align.CENTER
        }

        val importRow = ActionRow().apply {
            title = "Import Preferences"
            subtitle = "File: $exportFilePath"
            addSuffix(importButton)
        }

        val importGroup = PreferencesGroup().apply {
            title = "Import"
            description = "Import preferences from a file. Items such as credentials, " +
                    "providers, and vocabulary are added to your existing ones. " +
                    "Some items such as language and custom context will be replaced."
            add(importRow)
        }

        statusLabel = Label("").apply {
            selectable = true
            wrap = true
            halign = Align.CENTER
            valign = Align.CENTER
            hexpand = true
            vexpand = true
            visible = false
        }

        val statusGroup = PreferencesGroup().apply {
            add(statusLabel)
        }

        add(exportGroup)
        add(importGroup)
        add(statusGroup)

        exportButton.onClicked { onExportClicked() }
        importButton.onClicked { onImportClicked() }
    }

    private fun onExportClicked() {
        setButtonsEnabled(false)
        pageScope.launch {
            val result = manager.export()
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                setButtonsEnabled(true)
                result.fold(
                    onSuccess = { filePath -> showStatus("Exported to: $filePath") },
                    onFailure = { error -> showStatus("Export failed: ${error.message}") }
                )
                false
            }
        }
    }

    private fun onImportClicked() {
        setButtonsEnabled(false)
        pageScope.launch {
            val result = manager.importSettings()
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                setButtonsEnabled(true)
                result.fold(
                    onSuccess = { importResult ->
                        showStatus(buildImportSummary(importResult))
                        onImportSuccess()
                    },
                    onFailure = { error -> showStatus("Import failed: ${error.message}") }
                )
                false
            }
        }
    }

    fun shutdown() {
        pageScope.cancel()
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        exportButton.sensitive = enabled
        importButton.sensitive = enabled
    }

    private fun showStatus(message: String) {
        statusLabel.label = message
        statusLabel.visible = true
    }

    private fun buildImportSummary(result: ImportResult): String {
        val parts = mutableListOf<String>()
        if (result.credentialsAdded > 0) parts.add("${result.credentialsAdded} credential(s)")
        if (result.voiceProvidersAdded > 0) parts.add("${result.voiceProvidersAdded} voice provider(s)")
        if (result.textProvidersAdded > 0) parts.add("${result.textProvidersAdded} text provider(s)")
        if (result.vocabularyWordsAdded > 0) parts.add("${result.vocabularyWordsAdded} vocabulary word(s)")
        return if (parts.isEmpty()) {
            "Import complete. No new items to add."
        } else {
            "Imported: ${parts.joinToString(", ")}."
        }
    }
}
