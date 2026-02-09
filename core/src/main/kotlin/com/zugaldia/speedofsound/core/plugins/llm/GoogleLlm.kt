package com.zugaldia.speedofsound.core.plugins.llm

import com.google.genai.Client

class GoogleLlm(
    options: GoogleLlmOptions,
) : LlmPlugin<GoogleLlmOptions>(options) {

    private lateinit var client: Client

    override fun initialize() {
        super.initialize()
        val builder = Client.builder()
        currentOptions.apiKey?.let { builder.apiKey(it) }
        client = builder.build()
    }

    override fun enable() {
        super.enable()
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        log.info("Sending request to ${currentOptions.model}")
        val response = client.models.generateContent(
            currentOptions.model ?: DEFAULT_GOOGLE_MODEL_ID,
            request.text,
            null
        )
        LlmResponse(text = response.text() ?: "")
    }

    override fun disable() {
        super.disable()
    }

    override fun shutdown() {
        client.close()
        super.shutdown()
    }
}
