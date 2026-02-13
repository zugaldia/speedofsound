package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.languageFromIso2
import com.zugaldia.speedofsound.core.plugins.asr.SherpaOptions
import com.zugaldia.speedofsound.core.plugins.director.DirectorOptions
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.LlmPluginOptions
import com.zugaldia.speedofsound.core.plugins.llm.LlmProvider
import com.zugaldia.speedofsound.core.plugins.llm.OpenAiLlmOptions
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

@Suppress("TooManyFunctions")
class SettingsClient(val settingsStore: SettingsStore) {
    private val logger = LoggerFactory.getLogger(SettingsClient::class.java)

    private val _settingsChanged = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val settingsChanged: SharedFlow<String> = _settingsChanged.asSharedFlow()

    /*
     * Utility functions to build options objects
     */

    fun getRecorderOptions(): RecorderOptions =
        RecorderOptions(computeVolumeLevel = true)

    fun getSherpaOptions(): SherpaOptions =
        SherpaOptions(modelID = "sherpa-onnx-whisper-turbo")

    /**
     * Resolves a TextModelProviderSetting into the appropriate LlmPluginOptions
     */
    fun resolveProviderOptions(provider: TextModelProviderSetting): LlmPluginOptions {
        val apiKey = provider.credentialId?.let { credId -> getCredentials().find { it.id == credId }?.value }
        return when (provider.provider) {
            LlmProvider.ANTHROPIC -> AnthropicLlmOptions(
                apiKey = apiKey,
                model = provider.model,
                baseUrl = provider.baseUrl,
            )
            LlmProvider.GOOGLE -> GoogleLlmOptions(
                apiKey = apiKey,
                model = provider.model,
                baseUrl = provider.baseUrl,
            )
            LlmProvider.OPENAI -> OpenAiLlmOptions(
                apiKey = apiKey,
                model = provider.model,
                baseUrl = provider.baseUrl,
            )
        }
    }

    fun getDirectorOptions(): DirectorOptions = DirectorOptions(
        enableTextProcessing = getTextProcessingEnabled(),
        language = languageFromIso2(getDefaultLanguage()) ?: DEFAULT_LANGUAGE,
        customVocabulary = getCustomVocabulary(),
        customContext = getCustomContext()
    )

    /*
     * Not exposed to the UI
     */

    fun getPortalsRestoreToken(): String =
        settingsStore.getString(KEY_PORTALS_RESTORE_TOKEN, DEFAULT_PORTALS_RESTORE_TOKEN)

    fun setPortalsRestoreToken(value: String): Boolean =
        settingsStore.setString(KEY_PORTALS_RESTORE_TOKEN, value)

    /*
     * General page
     */

    fun getDefaultLanguage(): String =
        settingsStore.getString(KEY_DEFAULT_LANGUAGE, DEFAULT_LANGUAGE.iso2)

    fun setDefaultLanguage(value: String): Boolean =
        settingsStore.setString(KEY_DEFAULT_LANGUAGE, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_DEFAULT_LANGUAGE)
        }

    fun getSecondaryLanguage(): String =
        settingsStore.getString(KEY_SECONDARY_LANGUAGE, DEFAULT_SECONDARY_LANGUAGE.iso2)

    fun setSecondaryLanguage(value: String): Boolean =
        settingsStore.setString(KEY_SECONDARY_LANGUAGE, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_SECONDARY_LANGUAGE)
        }

    /*
     * Cloud Credentials page
     */

    fun getCredentials(): List<CredentialSetting> {
        val json = settingsStore.getString(KEY_CREDENTIALS, DEFAULT_CREDENTIALS)
        return if (json.isEmpty() || json == DEFAULT_CREDENTIALS) {
            emptyList()
        } else {
            runCatching {
                Json.decodeFromString<List<CredentialSetting>>(json)
            }.getOrElse { error ->
                logger.error("Failed to decode credentials from JSON", error)
                emptyList()
            }
        }
    }

    fun setCredentials(value: List<CredentialSetting>): Boolean {
        val json = Json.encodeToString(value)
        return settingsStore.setString(KEY_CREDENTIALS, json).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_CREDENTIALS)
        }
    }

    /*
     * Text Models page
     */

    fun getTextProcessingEnabled(): Boolean =
        settingsStore.getBoolean(KEY_TEXT_PROCESSING_ENABLED, DEFAULT_TEXT_PROCESSING_ENABLED)

    fun setTextProcessingEnabled(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_TEXT_PROCESSING_ENABLED, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_TEXT_PROCESSING_ENABLED)
        }

    fun getTextModelProviders(): List<TextModelProviderSetting> {
        val json = settingsStore.getString(KEY_TEXT_MODEL_PROVIDERS, DEFAULT_TEXT_MODEL_PROVIDERS)
        return if (json.isEmpty() || json == DEFAULT_TEXT_MODEL_PROVIDERS) {
            emptyList()
        } else {
            runCatching {
                Json.decodeFromString<List<TextModelProviderSetting>>(json)
            }.getOrElse { error ->
                logger.error("Failed to decode text model providers from JSON", error)
                emptyList()
            }
        }
    }

    fun setTextModelProviders(value: List<TextModelProviderSetting>): Boolean {
        val json = Json.encodeToString(value)
        return settingsStore.setString(KEY_TEXT_MODEL_PROVIDERS, json).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_TEXT_MODEL_PROVIDERS)
        }
    }

    fun getSelectedTextModelProviderId(): String =
        settingsStore.getString(KEY_SELECTED_TEXT_MODEL_PROVIDER_ID, DEFAULT_SELECTED_TEXT_MODEL_PROVIDER_ID)

    fun setSelectedTextModelProviderId(value: String): Boolean =
        settingsStore.setString(KEY_SELECTED_TEXT_MODEL_PROVIDER_ID, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_SELECTED_TEXT_MODEL_PROVIDER_ID)
        }

    /*
     * Personalization page
     */

    fun getCustomContext(): String =
        settingsStore.getString(KEY_CUSTOM_CONTEXT, DEFAULT_CUSTOM_CONTEXT)

    fun setCustomContext(value: String): Boolean =
        settingsStore.setString(KEY_CUSTOM_CONTEXT, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_CUSTOM_CONTEXT)
        }

    fun getCustomVocabulary(): List<String> =
        settingsStore.getStringArray(KEY_CUSTOM_VOCABULARY, DEFAULT_CUSTOM_VOCABULARY)

    fun setCustomVocabulary(value: List<String>): Boolean =
        settingsStore.setStringArray(KEY_CUSTOM_VOCABULARY, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_CUSTOM_VOCABULARY)
        }
}
