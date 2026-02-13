package com.zugaldia.speedofsound.core.plugins.llm

import com.google.genai.Client

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
        log.info("Sending request to ${currentOptions.model}")
        val response = currentClient.models.generateContent(
            currentOptions.model,
            request.text,
            null
        )
        LlmResponse(text = response.text() ?: "")
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
