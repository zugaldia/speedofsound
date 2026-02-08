package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.plugins.llm.SUPPORTED_OPENAI_TEXT_MODELS

class OpenAiTextModelGroup(
    viewModel: PreferencesViewModel
) : ProviderTextModelGroup(
    viewModel = viewModel,
    providerName = "OpenAI",
    providerDescription = "Configure OpenAI models (or compatible endpoints) for text processing",
    presetModels = SUPPORTED_OPENAI_TEXT_MODELS.values.toList(),
    getModelName = { viewModel.getOpenAiModelName() },
    setModelName = { viewModel.setOpenAiModelName(it) },
    getUseApiKey = { viewModel.getOpenAiTextUseApiKey() },
    setUseApiKey = { viewModel.setOpenAiTextUseApiKey(it) },
    getBaseUrl = { viewModel.getOpenAiBaseUrl() },
    setBaseUrl = { viewModel.setOpenAiBaseUrl(it) }
)
