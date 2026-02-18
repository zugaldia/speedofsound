package com.zugaldia.speedofsound.app.screens.preferences.shared

/**
 * Represents a preset URL for a custom ASR or LLM service, typically running locally,
 * but not necessarily (e.g., OpenRouter)
 */
data class CustomServicePreset(
    val displayName: String,
    val actionName: String,
    val url: String
) {
    companion object {
        val VOICE_SERVICE_PRESETS = listOf(
            CustomServicePreset(
                "Groq (OpenAI)", "groq", "https://api.groq.com/openai/v1"
            ),
            CustomServicePreset(
                "Mistral (OpenAI)", "mistral", "https://api.mistral.ai/v1"
            ),
            CustomServicePreset(
                "vLLM (OpenAI)", "vllm-openai", "http://localhost:8000/v1"
            ),
        )

        val TEXT_SERVICE_PRESETS = listOf(
            CustomServicePreset(
                "LM Studio (Anthropic)", "lm-studio-anthropic", "http://localhost:1234"
            ),
            CustomServicePreset(
                "LM Studio (OpenAI)", "lm-studio-openai", "http://localhost:1234/v1"
            ),
            CustomServicePreset(
                "Ollama (Anthropic)", "ollama-anthropic", "http://localhost:11434"
            ),
            CustomServicePreset(
                "Ollama (OpenAI)", "ollama-openai", "http://localhost:11434/v1"
            ),
            CustomServicePreset(
                "OpenRouter (Anthropic)", "openrouter-anthropic", "https://openrouter.ai/api"
            ),
            CustomServicePreset(
                "OpenRouter (OpenAI)", "openrouter-openai", "https://openrouter.ai/api/v1"
            ),
            CustomServicePreset(
                "llama.cpp (Anthropic)", "llama-cpp-anthropic", "http://localhost:8080"
            ),
            CustomServicePreset(
                "llama.cpp (OpenAI)", "llama-cpp-openai", "http://localhost:8080/v1"
            ),
            CustomServicePreset(
                "vLLM (Anthropic)", "vllm-anthropic", "http://localhost:8000"
            ),
            CustomServicePreset(
                "vLLM (OpenAI)", "vllm-openai", "http://localhost:8000/v1"
            ),
        )
    }
}
