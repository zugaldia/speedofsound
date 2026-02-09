package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SwitchRow
import org.slf4j.LoggerFactory

class TextModelsPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(TextModelsPage::class.java)

    private val enableSwitch: SwitchRow

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
            }
        }

        val textProcessingGroup = PreferencesGroup().apply {
            title = "Text Processing"
            description = "(Optional) Process transcriptions using a Large Language Model (LLM) to improve " +
                    "accuracy, grammar, and formatting. Slower processing but produces higher quality results."
            add(enableSwitch)
        }

        add(textProcessingGroup)
    }
}
