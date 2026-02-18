package com.zugaldia.speedofsound.app.settings

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.plugins.AppPluginCategory
import com.zugaldia.speedofsound.core.plugins.AppPluginRegistry
import com.zugaldia.speedofsound.core.plugins.asr.SherpaWhisperAsr
import com.zugaldia.speedofsound.core.plugins.asr.SherpaWhisperAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.OnnxWhisperAsr
import com.zugaldia.speedofsound.core.plugins.asr.OnnxWhisperAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.AsrPluginOptions
import com.zugaldia.speedofsound.core.plugins.asr.OpenAiAsr
import com.zugaldia.speedofsound.core.plugins.asr.OpenAiAsrOptions
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
        registry.register(AppPluginCategory.ASR, OnnxWhisperAsr())
        registry.register(AppPluginCategory.ASR, OpenAiAsr())
        registry.register(AppPluginCategory.ASR, SherpaWhisperAsr())
    }

    /**
     * Activates the currently selected ASR provider from settings.
     */
    fun activateSelectedProvider() {
        applySelectedProviderConfig(setActive = true)
    }

    /**
     * Refreshes the configuration for the currently selected provider.
     * Called when provider settings or credentials change.
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
        val selectedProvider = providers.find { it.id == selectedProviderId } ?: return

        val pluginId = pluginIdForProvider(selectedProvider.provider)
        val options = settingsClient.resolveVoiceProviderOptions(selectedProvider)
        applyAsrOptions(pluginId, options)

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
            is OnnxWhisperAsr -> activePlugin.updateOptions(activePlugin.getOptions().copy(language = language))
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
            is OnnxWhisperAsr -> plugin.updateOptions(options as OnnxWhisperAsrOptions)
            is OpenAiAsr -> plugin.updateOptions(options as OpenAiAsrOptions)
            is SherpaWhisperAsr -> plugin.updateOptions(options as SherpaWhisperAsrOptions)
        }
    }
}
