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

    fun getCredentials(): List<com.zugaldia.speedofsound.core.Credential> = settingsClient.getCredentials()
    fun setCredentials(value: List<com.zugaldia.speedofsound.core.Credential>): Boolean =
        settingsClient.setCredentials(value)

    /*
     * Text Models page
     */

    fun getTextProcessingEnabled(): Boolean = settingsClient.getTextProcessingEnabled()
    fun setTextProcessingEnabled(value: Boolean): Boolean = settingsClient.setTextProcessingEnabled(value)

    fun getGoogleModelName(): String = settingsClient.getGoogleModelName()
    fun setGoogleModelName(value: String): Boolean = settingsClient.setGoogleModelName(value)

    fun getGoogleBaseUrl(): String = settingsClient.getGoogleBaseUrl()
    fun setGoogleBaseUrl(value: String): Boolean = settingsClient.setGoogleBaseUrl(value)

    fun getGoogleTextUseApiKey(): Boolean = settingsClient.getGoogleTextUseApiKey()
    fun setGoogleTextUseApiKey(value: Boolean): Boolean = settingsClient.setGoogleTextUseApiKey(value)

    fun getAnthropicModelName(): String = settingsClient.getAnthropicModelName()
    fun setAnthropicModelName(value: String): Boolean = settingsClient.setAnthropicModelName(value)

    fun getAnthropicBaseUrl(): String = settingsClient.getAnthropicBaseUrl()
    fun setAnthropicBaseUrl(value: String): Boolean = settingsClient.setAnthropicBaseUrl(value)

    fun getAnthropicTextUseApiKey(): Boolean = settingsClient.getAnthropicTextUseApiKey()
    fun setAnthropicTextUseApiKey(value: Boolean): Boolean = settingsClient.setAnthropicTextUseApiKey(value)

    fun getOpenAiModelName(): String = settingsClient.getOpenAiModelName()
    fun setOpenAiModelName(value: String): Boolean = settingsClient.setOpenAiModelName(value)

    fun getOpenAiBaseUrl(): String = settingsClient.getOpenAiBaseUrl()
    fun setOpenAiBaseUrl(value: String): Boolean = settingsClient.setOpenAiBaseUrl(value)

    fun getOpenAiTextUseApiKey(): Boolean = settingsClient.getOpenAiTextUseApiKey()
    fun setOpenAiTextUseApiKey(value: Boolean): Boolean = settingsClient.setOpenAiTextUseApiKey(value)

    /*
     * Personalization page
     */

    fun getCustomContext(): String = settingsClient.getCustomContext()
    fun setCustomContext(value: String): Boolean = settingsClient.setCustomContext(value)

    fun getCustomVocabulary(): List<String> = settingsClient.getCustomVocabulary()
    fun setCustomVocabulary(value: List<String>): Boolean = settingsClient.setCustomVocabulary(value)
}
