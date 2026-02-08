package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.plugins.llm.SUPPORTED_ANTHROPIC_TEXT_MODELS

class AnthropicTextModelGroup(
    viewModel: PreferencesViewModel
) : ProviderTextModelGroup(
    viewModel = viewModel,
    providerName = "Anthropic",
    providerDescription = "Configure Anthropic Claude models (or compatible endpoints) for text processing",
    presetModels = SUPPORTED_ANTHROPIC_TEXT_MODELS.values.toList(),
    getModelName = { viewModel.getAnthropicModelName() },
    setModelName = { viewModel.setAnthropicModelName(it) },
    getUseApiKey = { viewModel.getAnthropicTextUseApiKey() },
    setUseApiKey = { viewModel.setAnthropicTextUseApiKey(it) },
    getBaseUrl = { viewModel.getAnthropicBaseUrl() },
    setBaseUrl = { viewModel.setAnthropicBaseUrl(it) }
)
