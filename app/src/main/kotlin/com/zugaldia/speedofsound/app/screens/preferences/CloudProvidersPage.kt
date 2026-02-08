package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.PasswordEntryRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SwitchRow
import org.slf4j.LoggerFactory

class CloudProvidersPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(CloudProvidersPage::class.java)

    private val enableSwitch: SwitchRow
    private val anthropicGroup: PreferencesGroup
    private val googleGroup: PreferencesGroup
    private val openaiGroup: PreferencesGroup

    init {
        title = "Cloud Providers"
        iconName = "network-server-symbolic"

        enableSwitch = SwitchRow().apply {
            title = "Enable cloud providers"
            subtitle = "Allow the use of cloud-based services for voice and text models"
            active = viewModel.getCloudEnabled()
            onNotify("active") {
                val enabled = active
                logger.info("Cloud providers enabled: $enabled")
                viewModel.setCloudEnabled(enabled)
                updateGroupsSensitivity()
            }
        }

        val cloudGroup = PreferencesGroup().apply {
            title = "Cloud Services"
            description = "$APPLICATION_NAME works locally by default, keeping your data on your machine. " +
                "Cloud providers are useful for machines with limited resources or where " +
                "larger models are needed for accessibility. When you configure API keys, " +
                "data will be shared with those providers."
            add(enableSwitch)
        }

        anthropicGroup = PreferencesGroup().apply {
            title = "Anthropic"
            description = "Enable Anthropic Claude models for text processing"
            val apiKeyRow = PasswordEntryRow().apply {
                title = "API Key"
                text = viewModel.getAnthropicApiKey()
                onNotify("text") {
                    val apiKey = text
                    logger.info("Anthropic API key updated (${apiKey.length} chars)")
                    viewModel.setAnthropicApiKey(apiKey)
                }
            }

            add(apiKeyRow)
        }

        googleGroup = PreferencesGroup().apply {
            title = "Google"
            description = "Enable Google Gemini models for voice and text processing"
            val apiKeyRow = PasswordEntryRow().apply {
                title = "API Key"
                text = viewModel.getGoogleApiKey()
                onNotify("text") {
                    val apiKey = text
                    logger.info("Google API key updated (${apiKey.length} chars)")
                    viewModel.setGoogleApiKey(apiKey)
                }
            }

            add(apiKeyRow)
        }

        openaiGroup = PreferencesGroup().apply {
            title = "OpenAI"
            description = "Enable OpenAI models for voice and text processing"
            val apiKeyRow = PasswordEntryRow().apply {
                title = "API Key"
                text = viewModel.getOpenaiApiKey()
                onNotify("text") {
                    val apiKey = text
                    logger.info("OpenAI API key updated (${apiKey.length} chars)")
                    viewModel.setOpenaiApiKey(apiKey)
                }
            }

            add(apiKeyRow)
        }

        add(cloudGroup)
        add(anthropicGroup)
        add(googleGroup)
        add(openaiGroup)

        updateGroupsSensitivity()
    }

    private fun updateGroupsSensitivity() {
        val enabled = enableSwitch.active
        anthropicGroup.sensitive = enabled
        googleGroup.sensitive = enabled
        openaiGroup.sensitive = enabled
    }
}
