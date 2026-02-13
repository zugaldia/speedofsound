package com.zugaldia.speedofsound.app.screens.preferences.text

import com.zugaldia.speedofsound.app.STYLE_CLASS_FLAT
import org.gnome.adw.EntryRow
import org.gnome.gio.Menu
import org.gnome.gio.SimpleAction
import org.gnome.gio.SimpleActionGroup
import org.gnome.gtk.MenuButton

/**
 * Represents a preset URL for a local LLM service.
 */
data class LocalLlmServicePreset(
    val displayName: String,
    val actionName: String,
    val url: String
)

class BaseUrlEntryRow(
    private val onTextChanged: () -> Unit
) : EntryRow() {

    private val servicePresets = listOf(
        LocalLlmServicePreset("LM Studio (Anthropic)", "lm-studio-anthropic", "http://localhost:1234"),
        LocalLlmServicePreset("LM Studio (OpenAI)", "lm-studio-openai", "http://localhost:1234/v1"),
        LocalLlmServicePreset("Ollama (Anthropic)", "ollama-anthropic", "http://localhost:11434"),
        LocalLlmServicePreset("Ollama (OpenAI)", "ollama-openai", "http://localhost:11434/v1"),
        LocalLlmServicePreset("llama.cpp (Anthropic)", "llama-cpp-anthropic", "http://localhost:8080"),
        LocalLlmServicePreset("llama.cpp (OpenAI)", "llama-cpp-openai", "http://localhost:8080/v1"),
        LocalLlmServicePreset("vLLM (Anthropic)", "vllm-anthropic", "http://localhost:8000"),
        LocalLlmServicePreset("vLLM (OpenAI)", "vllm-openai", "http://localhost:8000/v1"),
    )

    init {
        title = "Base URL (optional)"
        setupSuffixButton()
    }

    private fun setupSuffixButton() {
        val menu = Menu()
        servicePresets.forEach { preset ->
            menu.append(preset.displayName, "baseurl.${preset.actionName}")
        }

        val menuButton = MenuButton().apply {
            iconName = "view-list-symbolic"
            tooltipText = "Select a common local service"
            cssClasses = arrayOf(STYLE_CLASS_FLAT)
            menuModel = menu
        }

        val actionGroup = SimpleActionGroup()
        servicePresets.forEach { preset ->
            val action = SimpleAction(preset.actionName, null)
            action.onActivate { text = preset.url }
            actionGroup.addAction(action)
        }

        insertActionGroup("baseurl", actionGroup)
        addSuffix(menuButton)
    }

    fun setupNotifications() {
        onNotify("text") { onTextChanged() }
    }

    fun getBaseUrl(): String? = text.trim().ifEmpty { null }

    fun clear() {
        text = ""
    }
}
