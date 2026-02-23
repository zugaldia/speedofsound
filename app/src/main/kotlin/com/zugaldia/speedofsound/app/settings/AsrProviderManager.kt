package com.zugaldia.speedofsound.app.settings

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.plugins.AppPluginCategory
import com.zugaldia.speedofsound.core.plugins.AppPluginRegistry
import com.zugaldia.speedofsound.core.plugins.asr.SherpaWhisperAsr
import com.zugaldia.speedofsound.core.plugins.asr.SherpaWhisperAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.AsrPluginOptions
import com.zugaldia.speedofsound.core.plugins.asr.OpenAiAsr
import com.zugaldia.speedofsound.core.plugins.asr.OpenAiAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.asr.pluginIdForProvider
import org.slf4j.LoggerFactory

/**
 * Manages ASR provider selection, configuration, and plugin activation.
 */
class AsrProviderManager(
    private val registry: AppPluginRegistry,
    private val settingsClient: SettingsClient
) {
    private val logger = LoggerFactory.getLogger(AsrProviderManager::class.java)

    fun registerAsrPlugins() {
        registry.register(AppPluginCategory.ASR, OpenAiAsr())
        registry.register(AppPluginCategory.ASR, SherpaWhisperAsr())
    }

    /**
     * Activates the currently selected ASR provider from settings. Invoked when the MainViewModel starts
     * or when a new provider is selected in the settings screen (KEY_SELECTED_VOICE_MODEL_PROVIDER_ID changes).
     */
    fun activateSelectedProvider() {
        applySelectedProviderConfig(setActive = true)
    }

    /**
     * Refreshes the configuration for the currently selected provider. Invoked when the list of providers
     * (KEY_VOICE_MODEL_PROVIDERS) or credentials change (KEY_CREDENTIALS).
     */
    fun refreshProviderConfiguration() {
        applySelectedProviderConfig(setActive = false)
    }

    /**
     * Applies configuration for the currently selected provider.
     * Optionally activates the provider if setActive is true.
     */
    private fun applySelectedProviderConfig(setActive: Boolean) {
        val selectedProviderId = settingsClient.getSelectedVoiceModelProviderId()
        val providers = settingsClient.getVoiceModelProviders()
        val selectedProvider = providers.find { it.id == selectedProviderId }
        val pluginId = if (selectedProvider != null) {
            val options = settingsClient.resolveVoiceProviderOptions(selectedProvider)
            val id = pluginIdForProvider(selectedProvider.provider)
            applyAsrOptions(id, options)
            id
        } else {
            // No provider configured or a previously available provider was removed.
            // We fall back to the default local ASR plugin and model.
            settingsClient.setSelectedVoiceModelProviderId(DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID)
            SherpaWhisperAsr.ID
        }

        if (setActive) {
            registry.setActiveById(AppPluginCategory.ASR, pluginId)
        }
    }

    /**
     * Updates the language for the currently active ASR provider.
     */
    fun updateLanguage(language: Language) {
        val activePlugin = registry.getActive(AppPluginCategory.ASR) ?: return
        when (activePlugin) {
            is OpenAiAsr -> activePlugin.updateOptions(activePlugin.getOptions().copy(language = language))
            is SherpaWhisperAsr -> activePlugin.updateOptions(activePlugin.getOptions().copy(language = language))
        }
    }

    /**
     * Gets the name of the currently selected ASR provider.
     */
    fun getCurrentProviderName(): String {
        val selectedProviderId = settingsClient.getSelectedVoiceModelProviderId()
        val providers = settingsClient.getVoiceModelProviders()
        val selectedProvider = providers.find { it.id == selectedProviderId }
        return selectedProvider?.name ?: ""
    }

    private fun applyAsrOptions(pluginId: String, options: AsrPluginOptions) {
        val plugin = registry.getPluginById(AppPluginCategory.ASR, pluginId) ?: return
        when (plugin) {
            is OpenAiAsr -> plugin.updateOptions(options as OpenAiAsrOptions)
            is SherpaWhisperAsr -> plugin.updateOptions(options as SherpaWhisperAsrOptions)
        }
    }
}
