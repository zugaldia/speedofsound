package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.languageFromIso2
import com.zugaldia.speedofsound.core.plugins.asr.WhisperOptions
import com.zugaldia.speedofsound.core.plugins.director.DirectorOptions
import com.zugaldia.speedofsound.core.plugins.llm.GOOGLE_ENVIRONMENT_VARIABLE
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlmOptions
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    fun getWhisperOptions(): WhisperOptions =
        WhisperOptions(modelID = "sherpa-onnx-whisper-turbo")

    fun getGoogleLlmOptions(): GoogleLlmOptions =
        GoogleLlmOptions(apiKey = System.getenv(GOOGLE_ENVIRONMENT_VARIABLE))

    fun getDirectorOptions(): DirectorOptions = DirectorOptions(
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
