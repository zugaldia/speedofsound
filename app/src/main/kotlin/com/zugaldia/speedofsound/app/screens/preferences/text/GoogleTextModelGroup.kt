package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.plugins.llm.SUPPORTED_GOOGLE_TEXT_MODELS

class GoogleTextModelGroup(
    viewModel: PreferencesViewModel
) : ProviderTextModelGroup(
    viewModel = viewModel,
    providerName = "Google",
    providerDescription = "Configure Google Gemini models (or compatible endpoints) for text processing",
    presetModels = SUPPORTED_GOOGLE_TEXT_MODELS.values.toList(),
    getModelName = { viewModel.getGoogleModelName() },
    setModelName = { viewModel.setGoogleModelName(it) },
    getUseApiKey = { viewModel.getGoogleTextUseApiKey() },
    setUseApiKey = { viewModel.setGoogleTextUseApiKey(it) },
    getBaseUrl = { viewModel.getGoogleBaseUrl() },
    setBaseUrl = { viewModel.setGoogleBaseUrl(it) }
)
