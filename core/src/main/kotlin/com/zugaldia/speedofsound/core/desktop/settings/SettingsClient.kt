package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.languageFromIso2
import com.zugaldia.speedofsound.core.models.voice.ModelManager
import com.zugaldia.speedofsound.core.plugins.asr.AsrPluginOptions
import com.zugaldia.speedofsound.core.plugins.asr.AsrProvider
import com.zugaldia.speedofsound.core.plugins.asr.OpenAiAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.SherpaCanaryAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.SherpaParakeetAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.SherpaWhisperAsrOptions
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

    /**
     * Resolves a VoiceModelProviderSetting into the appropriate AsrPluginOptions
     */
    fun resolveVoiceProviderOptions(providerSetting: VoiceModelProviderSetting): AsrPluginOptions {
        val apiKey = providerSetting.credentialId?.let { credId -> getCredentials().find { it.id == credId }?.value }
        val language = languageFromIso2(getDefaultLanguage()) ?: DEFAULT_LANGUAGE
        return when (providerSetting.provider) {
            AsrProvider.OPENAI -> OpenAiAsrOptions(
                modelId = providerSetting.modelId,
                language = language,
                baseUrl = providerSetting.baseUrl,
                apiKey = apiKey,
            )

            AsrProvider.SHERPA_CANARY -> SherpaCanaryAsrOptions(
                modelId = providerSetting.modelId,
                language = language,
            )

            AsrProvider.SHERPA_PARAKEET -> SherpaParakeetAsrOptions(
                modelId = providerSetting.modelId,
                language = language,
            )

            AsrProvider.SHERPA_WHISPER -> SherpaWhisperAsrOptions(
                modelId = providerSetting.modelId,
                language = language,
            )
        }
    }

    /**
     * Resolves a TextModelProviderSetting into the appropriate LlmPluginOptions
     */
    fun resolveTextProviderOptions(providerSetting: TextModelProviderSetting): LlmPluginOptions {
        val apiKey = providerSetting.credentialId?.let { credId -> getCredentials().find { it.id == credId }?.value }
        return when (providerSetting.provider) {
            LlmProvider.ANTHROPIC -> AnthropicLlmOptions(
                apiKey = apiKey,
                modelId = providerSetting.modelId,
                baseUrl = providerSetting.baseUrl,
                disableThinking = providerSetting.disableThinking,
            )

            LlmProvider.GOOGLE -> GoogleLlmOptions(
                apiKey = apiKey,
                modelId = providerSetting.modelId,
                baseUrl = providerSetting.baseUrl,
                disableThinking = providerSetting.disableThinking,
            )

            LlmProvider.OPENAI -> OpenAiLlmOptions(
                apiKey = apiKey,
                modelId = providerSetting.modelId,
                baseUrl = providerSetting.baseUrl,
                disableThinking = providerSetting.disableThinking,
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

    fun getWelcomeScreenShown(): Boolean =
        settingsStore.getBoolean(KEY_WELCOME_SCREEN_SHOWN, DEFAULT_WELCOME_SCREEN_SHOWN)

    fun setWelcomeScreenShown(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_WELCOME_SCREEN_SHOWN, value)

    fun getPortalsRestoreToken(): String =
        settingsStore.getString(KEY_PORTALS_RESTORE_TOKEN, DEFAULT_PORTALS_RESTORE_TOKEN)

    fun setPortalsRestoreToken(value: String): Boolean =
        settingsStore.setString(KEY_PORTALS_RESTORE_TOKEN, value)

    fun getShortcutConfigured(): Boolean =
        settingsStore.getBoolean(KEY_SHORTCUT_CONFIGURED, DEFAULT_SHORTCUT_CONFIGURED)

    fun setShortcutConfigured(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_SHORTCUT_CONFIGURED, value)

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

    fun getBackgroundRecording(): Boolean =
        settingsStore.getBoolean(KEY_BACKGROUND_RECORDING, DEFAULT_BACKGROUND_RECORDING)

    fun setBackgroundRecording(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_BACKGROUND_RECORDING, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_BACKGROUND_RECORDING)
        }

    fun getAppendSpace(): Boolean =
        settingsStore.getBoolean(KEY_APPEND_SPACE, DEFAULT_APPEND_SPACE)

    fun setAppendSpace(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_APPEND_SPACE, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_APPEND_SPACE)
        }

    fun getHideInsteadOfMinimize(): Boolean =
        settingsStore.getBoolean(KEY_HIDE_INSTEAD_OF_MINIMIZE, DEFAULT_HIDE_INSTEAD_OF_MINIMIZE)

    fun setHideInsteadOfMinimize(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_HIDE_INSTEAD_OF_MINIMIZE, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_HIDE_INSTEAD_OF_MINIMIZE)
        }

    fun getStayHiddenOnActivation(): Boolean =
        settingsStore.getBoolean(KEY_STAY_HIDDEN_ON_ACTIVATION, DEFAULT_STAY_HIDDEN_ON_ACTIVATION)

    fun setStayHiddenOnActivation(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_STAY_HIDDEN_ON_ACTIVATION, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_STAY_HIDDEN_ON_ACTIVATION)
        }

    fun getTextOutputMethod(): String =
        settingsStore.getString(KEY_TEXT_OUTPUT_METHOD, DEFAULT_TEXT_OUTPUT_METHOD)

    fun setTextOutputMethod(value: String): Boolean =
        settingsStore.setString(KEY_TEXT_OUTPUT_METHOD, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_TEXT_OUTPUT_METHOD)
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
     * Voice Models page
     */

    private fun getLocalVoiceModelProviders(): List<VoiceModelProviderSetting> {
        val modelManager = ModelManager()
        return SUPPORTED_LOCAL_ASR_MODELS.values
            .filter { modelManager.isModelDownloaded(it.id) }
            .sortedBy { it.dataSizeMegabytes }
            .map { voiceModel ->
                VoiceModelProviderSetting(
                    id = voiceModel.id,
                    name = voiceModel.name,
                    provider = voiceModel.provider,
                    modelId = voiceModel.id,
                )
            }
    }

    fun getVoiceModelProviders(): List<VoiceModelProviderSetting> {
        val json = settingsStore.getString(KEY_VOICE_MODEL_PROVIDERS, DEFAULT_VOICE_MODEL_PROVIDERS)
        val customProviders = if (json.isEmpty() || json == DEFAULT_VOICE_MODEL_PROVIDERS) {
            emptyList()
        } else {
            runCatching {
                Json.decodeFromString<List<VoiceModelProviderSetting>>(json)
            }.getOrElse { error ->
                logger.error("Failed to decode voice model providers from JSON", error)
                emptyList()
            }
        }

        // Include the local provider first
        return getLocalVoiceModelProviders() + customProviders
    }

    fun setVoiceModelProviders(value: List<VoiceModelProviderSetting>): Boolean {
        // Filter out the local providers before saving
        val customProviders = value.filter { it.id !in SUPPORTED_LOCAL_ASR_MODELS.keys }
        val json = Json.encodeToString(customProviders)
        return settingsStore.setString(KEY_VOICE_MODEL_PROVIDERS, json).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_VOICE_MODEL_PROVIDERS)
        }
    }

    fun getSelectedVoiceModelProviderId(): String =
        settingsStore.getString(KEY_SELECTED_VOICE_MODEL_PROVIDER_ID, DEFAULT_SELECTED_VOICE_MODEL_PROVIDER_ID)

    fun setSelectedVoiceModelProviderId(value: String): Boolean =
        settingsStore.setString(KEY_SELECTED_VOICE_MODEL_PROVIDER_ID, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_SELECTED_VOICE_MODEL_PROVIDER_ID)
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

    /*
     * Advanced page
     */

    fun getSanitizeSpecialChars(): Boolean =
        settingsStore.getBoolean(KEY_SANITIZE_SPECIAL_CHARS, DEFAULT_SANITIZE_SPECIAL_CHARS)

    fun setSanitizeSpecialChars(value: Boolean): Boolean =
        settingsStore.setBoolean(KEY_SANITIZE_SPECIAL_CHARS, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_SANITIZE_SPECIAL_CHARS)
        }

    fun getPostHideDelayMs(): Int =
        settingsStore.getInt(KEY_POST_HIDE_DELAY_MS, DEFAULT_POST_HIDE_DELAY_MS)

    fun setPostHideDelayMs(value: Int): Boolean =
        settingsStore.setInt(KEY_POST_HIDE_DELAY_MS, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_POST_HIDE_DELAY_MS)
        }

    fun getTypingDelayMs(): Int =
        settingsStore.getInt(KEY_TYPING_DELAY_MS, DEFAULT_TYPING_DELAY_MS)

    fun setTypingDelayMs(value: Int): Boolean =
        settingsStore.setInt(KEY_TYPING_DELAY_MS, value).also { success ->
            if (success) _settingsChanged.tryEmit(KEY_TYPING_DELAY_MS)
        }
}
