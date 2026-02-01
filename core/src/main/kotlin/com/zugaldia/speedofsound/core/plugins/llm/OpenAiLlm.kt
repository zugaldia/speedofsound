package com.zugaldia.speedofsound.core.plugins.llm

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.responses.ResponseCreateParams

class OpenAiLlm(
    private val options: OpenAiLlmOptions,
) : LlmPlugin<OpenAiLlmOptions>(options) {

    private lateinit var client: OpenAIClient

    override fun initialize() {
        super.initialize()
        val builder = OpenAIOkHttpClient.builder()
        options.apiKey?.let { builder.apiKey(it) }
        options.baseUrl?.let { builder.baseUrl(it) }
        client = builder.build()
    }

    override fun enable() {
        super.enable()
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        val params = ResponseCreateParams.builder()
            .input(request.text)
            .model(options.model ?: DEFAULT_OPENAI_MODEL_ID)
            .build()

        val response = client.responses().create(params)
        val responseText = response.output()
            .flatMap { item -> item.message().map { it.content() }.orElse(emptyList()) }
            .mapNotNull { content -> content.outputText().orElse(null)?.text() }
            .joinToString("")

        LlmResponse(text = responseText)
    }

    override fun disable() {
        super.disable()
    }

    override fun shutdown() {
        client.close()
        super.shutdown()
    }
}
