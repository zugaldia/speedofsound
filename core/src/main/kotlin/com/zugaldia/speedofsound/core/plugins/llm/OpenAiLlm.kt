package com.zugaldia.speedofsound.core.plugins.llm

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.responses.ResponseCreateParams

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
        currentOptions.apiKey?.let { builder.apiKey(it) }
        currentOptions.baseUrl?.let { builder.baseUrl(it) }
        client = builder.build()
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")
        val params = ResponseCreateParams.builder()
            .input(request.text)
            .model(currentOptions.model)
            .build()

        log.info("Sending request to ${currentOptions.model}")
        val response = currentClient.responses().create(params)
        val responseText = response.output()
            .flatMap { item -> item.message().map { it.content() }.orElse(emptyList()) }
            .mapNotNull { content -> content.outputText().orElse(null)?.text() }
            .joinToString("")

        LlmResponse(text = responseText)
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
