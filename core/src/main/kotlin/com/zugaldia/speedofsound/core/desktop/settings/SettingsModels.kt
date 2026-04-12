package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.plugins.asr.AsrProvider
import com.zugaldia.speedofsound.core.plugins.llm.LlmProvider
import kotlinx.serialization.Serializable

/**
 * The type of credential. Currently, the only supported credential type is an API key. Prepared for future expansion
 * (e.g., Vertex AI credentials).
 */
@Serializable
enum class CredentialType {
    API_KEY
}

/**
 * A credential used to authenticate with a Voice or Text provider.
 */
@Serializable
data class CredentialSetting(
    val id: String,
    val type: CredentialType,
    val name: String,
    val value: String
)

/**
 * Configuration for a voice model provider.
 *
 * Users can configure which voice provider to use for voice transcription, along with
 * authentication credentials and provider-specific settings.
 */
@Serializable
data class VoiceModelProviderSetting(
    override val id: String,
    override val name: String,
    val provider: AsrProvider,
    val modelId: String,
    val credentialId: String? = null,
    val baseUrl: String? = null
) : SelectableProviderSetting

/**
 * Configuration for a text model provider.
 *
 * Users can configure which LLM provider to use for text processing, along with
 * authentication credentials and provider-specific settings.
 */
@Serializable
data class TextModelProviderSetting(
    override val id: String,
    override val name: String,
    val provider: LlmProvider,
    val modelId: String,
    val credentialId: String? = null,
    val baseUrl: String? = null,
    val disableThinking: Boolean = false
) : SelectableProviderSetting

/**
 * A serializable snapshot of all exportable user preferences.
 * Instance-specific settings (portal token, selected provider IDs, text processing toggle) are excluded.
 *
 * IMPORTANT: When adding a new setting here, you must also update [ImportExportManager] in the app
 * module to include it in both the export() and importSettings() functions.
 */
@Serializable
data class SettingsExport(
    val version: Int = 1,
    val defaultLanguage: String = DEFAULT_LANGUAGE.iso2,
    val secondaryLanguage: String = DEFAULT_SECONDARY_LANGUAGE.iso2,
    val backgroundRecording: Boolean = DEFAULT_BACKGROUND_RECORDING,
    val hideInsteadOfMinimize: Boolean = DEFAULT_HIDE_INSTEAD_OF_MINIMIZE,
    val stayHiddenOnActivation: Boolean = DEFAULT_STAY_HIDDEN_ON_ACTIVATION,
    val appendSpace: Boolean = DEFAULT_APPEND_SPACE,
    val credentials: List<CredentialSetting> = emptyList(),
    val voiceModelProviders: List<VoiceModelProviderSetting> = emptyList(),
    val textModelProviders: List<TextModelProviderSetting> = emptyList(),
    val sanitizeSpecialChars: Boolean = DEFAULT_SANITIZE_SPECIAL_CHARS,
    val postHideDelayMs: Int = DEFAULT_POST_HIDE_DELAY_MS,
    val typingDelayMs: Int = DEFAULT_TYPING_DELAY_MS,
    val customContext: String = DEFAULT_CUSTOM_CONTEXT,
    val customVocabulary: List<String> = emptyList()
)
