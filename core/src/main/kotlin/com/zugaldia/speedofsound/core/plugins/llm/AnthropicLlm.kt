package com.zugaldia.speedofsound.core.plugins.llm

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.messages.MessageCreateParams
import com.zugaldia.speedofsound.core.models.text.TextModel

class AnthropicLlm(
    options: AnthropicLlmOptions = AnthropicLlmOptions.Default,
) : LlmPlugin<AnthropicLlmOptions>(options) {
    override val id: String = ID

    private var client: AnthropicClient? = null

    companion object {
        const val ID = "LLM_ANTHROPIC"
    }

    override fun updateOptions(options: AnthropicLlmOptions) {
        super.updateOptions(options)
        if (client != null) { rebuildClient() } // Only rebuild if already enabled
    }

    override fun enable() {
        super.enable()
        rebuildClient()
    }

    private fun rebuildClient() {
        closeClient()
        val builder = AnthropicOkHttpClient.builder()
        currentOptions.baseUrl?.let { builder.baseUrl(it) }

        // For custom local endpoints, the API key is required but ignored
        // Refs: https://docs.ollama.com/api/anthropic-compatibility
        val effectiveApiKey = currentOptions.apiKey ?: if (!currentOptions.baseUrl.isNullOrEmpty()) "local" else null
        effectiveApiKey?.let { builder.apiKey(it) }

        client = builder.build()
    }

    private fun closeClient() {
        client?.let { existingClient ->
            runCatching { existingClient.close() }
                .onFailure { log.warn("Failed to close Anthropic client: ${it.message}") }
        }
        client = null
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")
        val params = MessageCreateParams.builder()
            .maxTokens(currentOptions.maxTokens)
            .addUserMessage(request.text)
            .model(currentOptions.model)
            .build()

        log.info("Sending request to ${currentOptions.model}")
        val message = currentClient.messages().create(params)
        val responseText = message.content()
            .mapNotNull { block -> block.text().orElse(null)?.text() }
            .joinToString("")

        LlmResponse(text = responseText)
    }

    override fun listModels(): Result<List<TextModel>> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")

        log.info("Fetching available models from Anthropic endpoint")
        val models = mutableListOf<TextModel>()
        currentClient.models().list().autoPager().forEach { model ->
            models.add(TextModel(id = model.id(), name = model.displayName()))
        }

        log.info("Retrieved ${models.size} models from Anthropic: ${models.joinToString { it.name }}")
        models
    }

    override fun disable() {
        super.disable()
        closeClient()
    }

    override fun shutdown() {
        closeClient()
        super.shutdown()
    }
}
