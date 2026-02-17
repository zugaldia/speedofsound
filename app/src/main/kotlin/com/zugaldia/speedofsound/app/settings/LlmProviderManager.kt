package com.zugaldia.speedofsound.app.settings

import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.plugins.AppPluginCategory
import com.zugaldia.speedofsound.core.plugins.AppPluginRegistry
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlm
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlm
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.LlmPluginOptions
import com.zugaldia.speedofsound.core.plugins.llm.OpenAiLlm
import com.zugaldia.speedofsound.core.plugins.llm.OpenAiLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.pluginIdForProvider
import org.slf4j.LoggerFactory

/**
 * Manages LLM provider selection, configuration, and plugin activation.
 */
class LlmProviderManager(
    private val registry: AppPluginRegistry,
    private val settingsClient: SettingsClient
) {
    private val logger = LoggerFactory.getLogger(LlmProviderManager::class.java)

    fun registerLlmPlugins() {
        registry.register(AppPluginCategory.LLM, AnthropicLlm())
        registry.register(AppPluginCategory.LLM, GoogleLlm())
        registry.register(AppPluginCategory.LLM, OpenAiLlm())
    }

    /**
     * Activates the currently selected LLM provider from settings.
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
        val selectedProviderId = settingsClient.getSelectedTextModelProviderId()
        val providers = settingsClient.getTextModelProviders()
        val selectedProvider = providers.find { it.id == selectedProviderId } ?: return

        val pluginId = pluginIdForProvider(selectedProvider.provider)
        val options = settingsClient.resolveTextProviderOptions(selectedProvider)
        applyLlmOptions(pluginId, options)

        if (setActive) {
            registry.setActiveById(AppPluginCategory.LLM, pluginId)
        }
    }

    private fun applyLlmOptions(pluginId: String, options: LlmPluginOptions) {
        val plugin = registry.getPluginById(AppPluginCategory.LLM, pluginId) ?: return
        when (plugin) {
            is AnthropicLlm -> plugin.updateOptions(options as AnthropicLlmOptions)
            is GoogleLlm -> plugin.updateOptions(options as GoogleLlmOptions)
            is OpenAiLlm -> plugin.updateOptions(options as OpenAiLlmOptions)
        }
    }
}
