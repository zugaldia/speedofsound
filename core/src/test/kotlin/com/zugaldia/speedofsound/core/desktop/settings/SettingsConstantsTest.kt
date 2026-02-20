package com.zugaldia.speedofsound.core.desktop.settings

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SettingsConstantsTest {

    @Test
    fun `properties filename is not blank`() {
        assertTrue(DEFAULT_PROPERTIES_FILENAME.isNotBlank())
    }

    @Test
    fun `setting keys are not blank`() {
        assertTrue(KEY_PORTALS_RESTORE_TOKEN.isNotBlank())
        assertTrue(KEY_DEFAULT_LANGUAGE.isNotBlank())
        assertTrue(KEY_SECONDARY_LANGUAGE.isNotBlank())
        assertTrue(KEY_CREDENTIALS.isNotBlank())
        assertTrue(KEY_VOICE_MODEL_PROVIDERS.isNotBlank())
        assertTrue(KEY_SELECTED_VOICE_MODEL_PROVIDER_ID.isNotBlank())
        assertTrue(KEY_TEXT_PROCESSING_ENABLED.isNotBlank())
        assertTrue(KEY_TEXT_MODEL_PROVIDERS.isNotBlank())
        assertTrue(KEY_SELECTED_TEXT_MODEL_PROVIDER_ID.isNotBlank())
        assertTrue(KEY_CUSTOM_CONTEXT.isNotBlank())
        assertTrue(KEY_CUSTOM_VOCABULARY.isNotBlank())
    }

    @Test
    fun `default languages are defined`() {
        assertNotNull(DEFAULT_LANGUAGE)
        assertNotNull(DEFAULT_SECONDARY_LANGUAGE)
    }

    @Test
    fun `supported ASR models is not empty`() {
        assertTrue(SUPPORTED_LOCAL_ASR_MODELS.isNotEmpty())
    }

    @Test
    fun `default JSON strings can be parsed as lists`() {
        val credentials = Json.decodeFromString<List<CredentialSetting>>(DEFAULT_CREDENTIALS)
        assertTrue(credentials.isEmpty())

        val voiceProviders = Json.decodeFromString<List<VoiceModelProviderSetting>>(DEFAULT_VOICE_MODEL_PROVIDERS)
        assertTrue(voiceProviders.isEmpty())

        val textProviders = Json.decodeFromString<List<TextModelProviderSetting>>(DEFAULT_TEXT_MODEL_PROVIDERS)
        assertTrue(textProviders.isEmpty())
    }
}
