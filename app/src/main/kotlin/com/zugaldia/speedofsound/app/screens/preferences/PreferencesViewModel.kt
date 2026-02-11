package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.desktop.settings.CredentialSetting
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.desktop.settings.TextModelProviderSetting
import org.slf4j.LoggerFactory

@Suppress("TooManyFunctions") // ViewModel delegates to SettingsClient for all preference properties
class PreferencesViewModel(private val settingsClient: SettingsClient) {
    private val logger = LoggerFactory.getLogger(PreferencesViewModel::class.java)

    var state: PreferencesState = PreferencesState()
        private set

    init {
        logger.info("Initializing.")
    }

    fun shutdown() {
        logger.info("Shutting down.")
    }

    /*
     * General page
     */

    fun getDefaultLanguage(): String = settingsClient.getDefaultLanguage()
    fun setDefaultLanguage(value: String): Boolean = settingsClient.setDefaultLanguage(value)

    fun getSecondaryLanguage(): String = settingsClient.getSecondaryLanguage()
    fun setSecondaryLanguage(value: String): Boolean = settingsClient.setSecondaryLanguage(value)

    /*
     * Cloud Credentials page
     */

    fun getCredentials(): List<CredentialSetting> = settingsClient.getCredentials()
    fun setCredentials(value: List<CredentialSetting>): Boolean =
        settingsClient.setCredentials(value)

    /*
     * Text Models page
     */

    fun getTextProcessingEnabled(): Boolean = settingsClient.getTextProcessingEnabled()
    fun setTextProcessingEnabled(value: Boolean): Boolean = settingsClient.setTextProcessingEnabled(value)

    fun getTextModelProviders(): List<TextModelProviderSetting> =
        settingsClient.getTextModelProviders()
    fun setTextModelProviders(value: List<TextModelProviderSetting>): Boolean =
        settingsClient.setTextModelProviders(value)

    fun getSelectedTextModelProviderId(): String =
        settingsClient.getSelectedTextModelProviderId()
    fun setSelectedTextModelProviderId(value: String): Boolean =
        settingsClient.setSelectedTextModelProviderId(value)

    /*
     * Personalization page
     */

    fun getCustomContext(): String = settingsClient.getCustomContext()
    fun setCustomContext(value: String): Boolean = settingsClient.setCustomContext(value)

    fun getCustomVocabulary(): List<String> = settingsClient.getCustomVocabulary()
    fun setCustomVocabulary(value: List<String>): Boolean = settingsClient.setCustomVocabulary(value)
}
