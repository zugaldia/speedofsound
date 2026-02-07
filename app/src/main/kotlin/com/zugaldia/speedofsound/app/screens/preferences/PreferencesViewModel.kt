package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
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

    fun getDefaultLanguage(): String = settingsClient.getDefaultLanguage()
    fun setDefaultLanguage(value: String): Boolean = settingsClient.setDefaultLanguage(value)

    fun getSecondaryLanguage(): String = settingsClient.getSecondaryLanguage()
    fun setSecondaryLanguage(value: String): Boolean = settingsClient.setSecondaryLanguage(value)

    fun getCustomContext(): String = settingsClient.getCustomContext()
    fun setCustomContext(value: String): Boolean = settingsClient.setCustomContext(value)

    fun getCustomVocabulary(): List<String> = settingsClient.getCustomVocabulary()
    fun setCustomVocabulary(value: List<String>): Boolean = settingsClient.setCustomVocabulary(value)

    fun getCloudEnabled(): Boolean = settingsClient.getCloudEnabled()
    fun setCloudEnabled(value: Boolean): Boolean = settingsClient.setCloudEnabled(value)

    fun getAnthropicApiKey(): String = settingsClient.getAnthropicApiKey()
    fun setAnthropicApiKey(value: String): Boolean = settingsClient.setAnthropicApiKey(value)

    fun getGoogleApiKey(): String = settingsClient.getGoogleApiKey()
    fun setGoogleApiKey(value: String): Boolean = settingsClient.setGoogleApiKey(value)

    fun getOpenaiApiKey(): String = settingsClient.getOpenaiApiKey()
    fun setOpenaiApiKey(value: String): Boolean = settingsClient.setOpenaiApiKey(value)
}
