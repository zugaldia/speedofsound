package com.zugaldia.speedofsound.core.plugins.llm

import com.google.genai.Client

class GoogleLlm(
    private val options: GoogleLlmOptions,
) : LlmPlugin<GoogleLlmOptions>(options) {

    private lateinit var client: Client

    override fun initialize() {
        super.initialize()
        val builder = Client.builder()
        options.apiKey?.let { builder.apiKey(it) }
        client = builder.build()
    }

    override fun enable() {
        super.enable()
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        val response = client.models.generateContent(
            options.model ?: DEFAULT_GOOGLE_MODEL_ID,
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
