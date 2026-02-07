package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.PasswordEntryRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SwitchRow
import org.slf4j.LoggerFactory

class CloudProvidersPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(CloudProvidersPage::class.java)

    private val anthropicApiKeyRow: PasswordEntryRow
    private val googleApiKeyRow: PasswordEntryRow
    private val openaiApiKeyRow: PasswordEntryRow

    init {
        title = "Cloud Providers"
        iconName = "network-server-symbolic"

        val initialCloudEnabled = viewModel.getCloudEnabled()

        anthropicApiKeyRow = PasswordEntryRow().apply {
            title = "API Key"
            text = viewModel.getAnthropicApiKey()
            sensitive = initialCloudEnabled
            onNotify("text") {
                val apiKey = text
                logger.info("Anthropic API key updated (${apiKey.length} chars)")
                viewModel.setAnthropicApiKey(apiKey)
            }
        }

        googleApiKeyRow = PasswordEntryRow().apply {
            title = "API Key"
            text = viewModel.getGoogleApiKey()
            sensitive = initialCloudEnabled
            onNotify("text") {
                val apiKey = text
                logger.info("Google API key updated (${apiKey.length} chars)")
                viewModel.setGoogleApiKey(apiKey)
            }
        }

        openaiApiKeyRow = PasswordEntryRow().apply {
            title = "API Key"
            text = viewModel.getOpenaiApiKey()
            sensitive = initialCloudEnabled
            onNotify("text") {
                val apiKey = text
                logger.info("OpenAI API key updated (${apiKey.length} chars)")
                viewModel.setOpenaiApiKey(apiKey)
            }
        }

        val cloudEnabledRow = SwitchRow().apply {
            title = "Enable Cloud Services"
            subtitle = "Optional: Use cloud providers for speech recognition and text processing"
            active = initialCloudEnabled
            onNotify("active") {
                val isActive = active
                logger.info("Cloud services enabled: $isActive")
                viewModel.setCloudEnabled(isActive)
                updateApiKeyRowsSensitivity(isActive)
            }
        }

        val privacyGroup = PreferencesGroup().apply {
            title = "Privacy & Local Processing"
            description = "By default, $APPLICATION_NAME runs locally using on-device models. " +
                "Your voice data never leaves your computer. Cloud providers are optional and can be " +
                "enabled for improved accuracy and accessibility or on devices with limited resources. " +
                "Enabling cloud services will send audio and/or text data to third-party services."
            add(cloudEnabledRow)
        }

        val anthropicGroup = PreferencesGroup().apply {
            title = "Anthropic"
            description = "Claude models for text processing."
            add(anthropicApiKeyRow)
        }

        val googleGroup = PreferencesGroup().apply {
            title = "Google"
            description = "Gemini models for audio or text processing."
            add(googleApiKeyRow)
        }

        val openaiGroup = PreferencesGroup().apply {
            title = "OpenAI"
            description = "OpenAI models for audio or text processing."
            add(openaiApiKeyRow)
        }

        add(privacyGroup)
        add(anthropicGroup)
        add(googleGroup)
        add(openaiGroup)
    }

    private fun updateApiKeyRowsSensitivity(enabled: Boolean) {
        anthropicApiKeyRow.sensitive = enabled
        googleApiKeyRow.sensitive = enabled
        openaiApiKeyRow.sensitive = enabled
    }
}
