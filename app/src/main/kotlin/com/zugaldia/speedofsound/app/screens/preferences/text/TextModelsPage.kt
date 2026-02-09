package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SwitchRow
import org.slf4j.LoggerFactory

class TextModelsPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(TextModelsPage::class.java)

    private val enableSwitch: SwitchRow
    private val anthropicGroup: AnthropicTextModelGroup
    private val googleGroup: GoogleTextModelGroup
    private val openAiGroup: OpenAiTextModelGroup

    init {
        title = "Text Models"
        iconName = "accessories-text-editor-symbolic"

        enableSwitch = SwitchRow().apply {
            title = "Enable text processing"
            subtitle = "Process transcriptions with an LLM for improved results"
            active = viewModel.getTextProcessingEnabled()
            onNotify("active") {
                val enabled = active
                logger.info("Text processing enabled: $enabled")
                viewModel.setTextProcessingEnabled(enabled)
                updateGroupsSensitivity()
            }
        }

        val textProcessingGroup = PreferencesGroup().apply {
            title = "Text Processing"
            description = "Process transcriptions using a Large Language Model (LLM) to improve " +
                    "accuracy, grammar, and formatting. Slower processing but produces higher quality results."
            add(enableSwitch)
        }

        anthropicGroup = AnthropicTextModelGroup(viewModel)
        googleGroup = GoogleTextModelGroup(viewModel)
        openAiGroup = OpenAiTextModelGroup(viewModel)

        add(textProcessingGroup)
        add(anthropicGroup)
        add(googleGroup)
        add(openAiGroup)
        updateGroupsSensitivity()
    }

    private fun updateGroupsSensitivity() {
        val enabled = enableSwitch.active
        anthropicGroup.sensitive = enabled
        googleGroup.sensitive = enabled
        openAiGroup.sensitive = enabled
    }
}
