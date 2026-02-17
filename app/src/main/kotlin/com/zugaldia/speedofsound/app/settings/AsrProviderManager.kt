package com.zugaldia.speedofsound.app.settings

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.plugins.AppPluginCategory
import com.zugaldia.speedofsound.core.plugins.AppPluginRegistry
import com.zugaldia.speedofsound.core.plugins.asr.SherpaAsr
import com.zugaldia.speedofsound.core.plugins.asr.SherpaAsrOptions
import com.zugaldia.speedofsound.core.plugins.asr.OnnxAsr
import com.zugaldia.speedofsound.core.plugins.asr.OnnxAsrOptions
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
        registry.register(AppPluginCategory.ASR, OnnxAsr())
        registry.register(AppPluginCategory.ASR, OpenAiAsr())
        registry.register(AppPluginCategory.ASR, SherpaAsr())
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
            is OnnxAsr -> activePlugin.updateOptions(activePlugin.getOptions().copy(language = language))
            is OpenAiAsr -> activePlugin.updateOptions(activePlugin.getOptions().copy(language = language))
            is SherpaAsr -> activePlugin.updateOptions(activePlugin.getOptions().copy(language = language))
        }
    }

    private fun applyAsrOptions(pluginId: String, options: AsrPluginOptions) {
        val plugin = registry.getPluginById(AppPluginCategory.ASR, pluginId) ?: return
        when (plugin) {
            is OnnxAsr -> plugin.updateOptions(options as OnnxAsrOptions)
            is OpenAiAsr -> plugin.updateOptions(options as OpenAiAsrOptions)
            is SherpaAsr -> plugin.updateOptions(options as SherpaAsrOptions)
        }
    }
}
