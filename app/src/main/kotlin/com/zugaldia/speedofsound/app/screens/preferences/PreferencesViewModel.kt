package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.APPLICATION_URL_KEYBOARD_SHORTCUT
import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.speedofsound.core.desktop.settings.CredentialSetting
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.desktop.settings.TextModelProviderSetting
import com.zugaldia.speedofsound.core.desktop.settings.VoiceModelProviderSetting
import com.zugaldia.stargate.sdk.globalshortcuts.BoundShortcut
import com.zugaldia.stargate.sdk.session.CreateSessionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.slf4j.LoggerFactory

@Suppress("TooManyFunctions") // ViewModel delegates to SettingsClient for all preference properties
class PreferencesViewModel(
    private val settingsClient: SettingsClient,
    private val portalsClient: PortalsClient,
) {
    private val logger = LoggerFactory.getLogger(PreferencesViewModel::class.java)
    val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        logger.info("Initializing.")
    }

    fun shutdown() {
        logger.info("Shutting down.")
        viewModelScope.cancel()
    }

    /*
     * General page
     */

    fun getShortcutConfigured(): Boolean = settingsClient.getShortcutConfigured()
    fun setShortcutConfigured(value: Boolean): Boolean = settingsClient.setShortcutConfigured(value)

    suspend fun createGlobalShortcutsSession(): Result<CreateSessionResponse> =
        portalsClient.createGlobalShortcutsSession()
    suspend fun listGlobalShortcuts(): Result<List<BoundShortcut>> = portalsClient.listGlobalShortcuts()
    suspend fun bindGlobalShortcuts(): Result<List<BoundShortcut>> = portalsClient.bindGlobalShortcuts()
    val globalShortcutsVersion: Int get() = portalsClient.globalShortcutsVersion
    fun configureGlobalShortcuts(): Result<Unit> = portalsClient.configureGlobalShortcuts()
    suspend fun openDocumentationUri() = portalsClient.openUri(APPLICATION_URL_KEYBOARD_SHORTCUT)

    fun getBackgroundRecording(): Boolean = settingsClient.getBackgroundRecording()
    fun setBackgroundRecording(value: Boolean): Boolean = settingsClient.setBackgroundRecording(value)

    fun getHideInsteadOfMinimize(): Boolean = settingsClient.getHideInsteadOfMinimize()
    fun setHideInsteadOfMinimize(value: Boolean): Boolean = settingsClient.setHideInsteadOfMinimize(value)

    fun getDefaultLanguage(): String = settingsClient.getDefaultLanguage()
    fun setDefaultLanguage(value: String): Boolean = settingsClient.setDefaultLanguage(value)

    fun getSecondaryLanguage(): String = settingsClient.getSecondaryLanguage()
    fun setSecondaryLanguage(value: String): Boolean = settingsClient.setSecondaryLanguage(value)

    fun getAppendSpace(): Boolean = settingsClient.getAppendSpace()
    fun setAppendSpace(value: Boolean): Boolean = settingsClient.setAppendSpace(value)

    /*
     * Cloud Credentials page
     */

    fun getCredentials(): List<CredentialSetting> = settingsClient.getCredentials()
    fun setCredentials(value: List<CredentialSetting>): Boolean =
        settingsClient.setCredentials(value)

    /*
     * Voice Models page
     */

    fun getVoiceModelProviders(): List<VoiceModelProviderSetting> =
        settingsClient.getVoiceModelProviders()
    fun setVoiceModelProviders(value: List<VoiceModelProviderSetting>): Boolean =
        settingsClient.setVoiceModelProviders(value)

    fun getSelectedVoiceModelProviderId(): String =
        settingsClient.getSelectedVoiceModelProviderId()
    fun setSelectedVoiceModelProviderId(value: String): Boolean =
        settingsClient.setSelectedVoiceModelProviderId(value)

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

    /*
     * Advanced page
     */

    fun getSanitizeSpecialChars(): Boolean = settingsClient.getSanitizeSpecialChars()
    fun setSanitizeSpecialChars(value: Boolean): Boolean = settingsClient.setSanitizeSpecialChars(value)

    fun getPostHideDelayMs(): Int = settingsClient.getPostHideDelayMs()
    fun setPostHideDelayMs(value: Int): Boolean = settingsClient.setPostHideDelayMs(value)

    fun getTypingDelayMs(): Int = settingsClient.getTypingDelayMs()
    fun setTypingDelayMs(value: Int): Boolean = settingsClient.setTypingDelayMs(value)
}
