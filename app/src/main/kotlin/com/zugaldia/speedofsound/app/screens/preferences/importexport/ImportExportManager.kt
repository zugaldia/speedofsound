package com.zugaldia.speedofsound.app.screens.preferences.importexport

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.desktop.settings.SUPPORTED_LOCAL_ASR_MODELS
import com.zugaldia.speedofsound.core.desktop.settings.SettingsExport
import com.zugaldia.speedofsound.core.getDataDir
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

data class ImportResult(
    val filePath: String,
    val credentialsAdded: Int = 0,
    val voiceProvidersAdded: Int = 0,
    val textProvidersAdded: Int = 0,
    val vocabularyWordsAdded: Int = 0
)

class ImportExportManager(private val viewModel: PreferencesViewModel) {
    private val logger = LoggerFactory.getLogger(ImportExportManager::class.java)

    private val prettyJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun export(): Result<String> = runCatching {
        // We do not export anything that is instance-specific (e.g., portal token). That
        // also includes built-in voice models, which we filter out below.
        val exportData = SettingsExport(
            defaultLanguage = viewModel.getDefaultLanguage(),
            secondaryLanguage = viewModel.getSecondaryLanguage(),
            credentials = viewModel.getCredentials(),
            voiceModelProviders = viewModel.getVoiceModelProviders()
                .filter { it.id !in SUPPORTED_LOCAL_ASR_MODELS.keys },
            textModelProviders = viewModel.getTextModelProviders(),
            customContext = viewModel.getCustomContext(),
            customVocabulary = viewModel.getCustomVocabulary()
        )

        val outputFile = getDataDir().resolve(EXPORT_FILENAME).toFile()
        outputFile.writeText(prettyJson.encodeToString(exportData))
        logger.info("Exported settings to: ${outputFile.absolutePath}")
        outputFile.absolutePath
    }

    fun importSettings(): Result<ImportResult> = runCatching {
        val inputFile = getDataDir().resolve(EXPORT_FILENAME).toFile()
        check(inputFile.exists()) { "Export file not found: ${inputFile.absolutePath}" }

        val exportData = prettyJson.decodeFromString<SettingsExport>(inputFile.readText())
        if (exportData.version != 1) {
            throw IllegalStateException("Unsupported export version: ${exportData.version}")
        }

        viewModel.setDefaultLanguage(exportData.defaultLanguage)
        viewModel.setSecondaryLanguage(exportData.secondaryLanguage)
        viewModel.setCustomContext(exportData.customContext)

        val existingCredentials = viewModel.getCredentials()
        val existingCredentialIds = existingCredentials.map { it.id }.toSet()
        val newCredentials = exportData.credentials.filter { it.id !in existingCredentialIds }
        if (newCredentials.isNotEmpty()) {
            viewModel.setCredentials(existingCredentials + newCredentials)
        }

        val existingVoiceProviders = viewModel.getVoiceModelProviders()
        val existingVoiceIds = existingVoiceProviders.map { it.id }.toSet()
        val newVoiceProviders = exportData.voiceModelProviders.filter { it.id !in existingVoiceIds }
        if (newVoiceProviders.isNotEmpty()) {
            viewModel.setVoiceModelProviders(existingVoiceProviders + newVoiceProviders)
        }

        val existingTextProviders = viewModel.getTextModelProviders()
        val existingTextIds = existingTextProviders.map { it.id }.toSet()
        val newTextProviders = exportData.textModelProviders.filter { it.id !in existingTextIds }
        if (newTextProviders.isNotEmpty()) {
            viewModel.setTextModelProviders(existingTextProviders + newTextProviders)
        }

        val existingVocabulary = viewModel.getCustomVocabulary()
        val existingVocabSet = existingVocabulary.toSet()
        val newVocabWords = exportData.customVocabulary.filter { it !in existingVocabSet }
        if (newVocabWords.isNotEmpty()) {
            viewModel.setCustomVocabulary(existingVocabulary + newVocabWords)
        }

        logger.info("Imported settings from: ${inputFile.absolutePath}")
        ImportResult(
            filePath = inputFile.absolutePath,
            credentialsAdded = newCredentials.size,
            voiceProvidersAdded = newVoiceProviders.size,
            textProvidersAdded = newTextProviders.size,
            vocabularyWordsAdded = newVocabWords.size
        )
    }

    companion object {
        const val EXPORT_FILENAME = "$APPLICATION_SHORT-preferences.json"
    }
}
