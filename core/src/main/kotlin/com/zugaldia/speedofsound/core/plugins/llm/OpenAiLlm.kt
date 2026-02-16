package com.zugaldia.speedofsound.core.plugins.llm

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.responses.ResponseCreateParams
import com.zugaldia.speedofsound.core.LOCAL_API_KEY_PLACEHOLDER
import com.zugaldia.speedofsound.core.models.text.TextModel

class OpenAiLlm(
    options: OpenAiLlmOptions = OpenAiLlmOptions.Default,
) : LlmPlugin<OpenAiLlmOptions>(options) {
    override val id: String = ID

    private var client: OpenAIClient? = null

    companion object {
        const val ID = "LLM_OPENAI"
    }

    override fun updateOptions(options: OpenAiLlmOptions) {
        super.updateOptions(options)
        if (client != null) { rebuildClient() } // Only rebuild if already enabled
    }

    override fun enable() {
        super.enable()
        rebuildClient()
    }

    private fun closeClient() {
        client?.let { existingClient ->
            runCatching { existingClient.close() }
                .onFailure { log.warn("Failed to close OpenAI client: ${it.message}") }
        }
        client = null
    }

    private fun rebuildClient() {
        closeClient()
        val builder = OpenAIOkHttpClient.builder()
        currentOptions.baseUrl?.let { builder.baseUrl(it) }

        // For custom local endpoints, the API key is required but ignored
        // Refs: https://docs.ollama.com/api/openai-compatibility
        val effectiveApiKey = currentOptions.apiKey
            ?: if (!currentOptions.baseUrl.isNullOrEmpty()) LOCAL_API_KEY_PLACEHOLDER else null
        effectiveApiKey?.let { builder.apiKey(it) }

        client = builder.build()
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")
        val params = ResponseCreateParams.builder()
            .input(request.text)
            .model(currentOptions.modelId)
            .build()

        log.info("Sending request to ${currentOptions.modelId}")
        val response = currentClient.responses().create(params)
        val responseText = response.output()
            .flatMap { item -> item.message().map { it.content() }.orElse(emptyList()) }
            .mapNotNull { content -> content.outputText().orElse(null)?.text() }
            .joinToString("")

        LlmResponse(text = responseText)
    }

    override fun listModels(): Result<List<TextModel>> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")

        log.info("Fetching available models from OpenAI endpoint")
        val models = mutableListOf<TextModel>()
        currentClient.models().list().autoPager().forEach { model ->
            // OpenAI doesn't provide separate display names
            models.add(TextModel(id = model.id(), name = model.id()))
        }

        log.info("Retrieved ${models.size} models from OpenAI: ${models.joinToString { it.name }}")
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
