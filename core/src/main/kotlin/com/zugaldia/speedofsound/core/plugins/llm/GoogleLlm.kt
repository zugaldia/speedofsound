package com.zugaldia.speedofsound.core.plugins.llm

import com.google.genai.Client
import com.google.genai.types.ListModelsConfig
import com.zugaldia.speedofsound.core.models.text.TextModel

class GoogleLlm(
    options: GoogleLlmOptions = GoogleLlmOptions.Default,
) : LlmPlugin<GoogleLlmOptions>(options) {
    override val id: String = ID

    private var client: Client? = null

    companion object {
        const val ID = "LLM_GOOGLE"
    }

    override fun updateOptions(options: GoogleLlmOptions) {
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
                .onFailure { log.warn("Failed to close Google client: ${it.message}") }
        }
        client = null
    }

    private fun rebuildClient() {
        closeClient()
        val builder = Client.builder()
        currentOptions.apiKey?.let { builder.apiKey(it) }
        client = builder.build()
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")
        log.info("Sending request to ${currentOptions.modelId}")
        val response = currentClient.models.generateContent(
            currentOptions.modelId,
            request.text,
            null
        )
        LlmResponse(text = response.text() ?: "")
    }

    override fun listModels(): Result<List<TextModel>> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")

        log.info("Fetching available models from Google endpoint")
        val models = mutableListOf<TextModel>()
        currentClient.models.list(ListModelsConfig.builder().build()).forEach { model ->
            val name = model.name().orElse(null)
            val displayName = model.displayName().orElse(null) ?: name
            if (name != null && displayName != null) {
                models.add(TextModel(id = name, name = displayName, provider = LlmProvider.GOOGLE))
            }
        }

        log.info("Retrieved ${models.size} models from Google: ${models.joinToString { it.name }}")
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
