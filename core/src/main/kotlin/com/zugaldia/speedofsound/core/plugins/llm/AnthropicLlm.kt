package com.zugaldia.speedofsound.core.plugins.llm

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.messages.MessageCreateParams

class AnthropicLlm(
    options: AnthropicLlmOptions,
) : LlmPlugin<AnthropicLlmOptions>(options) {

    private lateinit var client: AnthropicClient

    override fun initialize() {
        super.initialize()
        val builder = AnthropicOkHttpClient.builder()
        currentOptions.apiKey?.let { builder.apiKey(it) }
        currentOptions.baseUrl?.let { builder.baseUrl(it) }
        client = builder.build()
    }

    override fun enable() {
        super.enable()
    }

    override fun generate(request: LlmRequest): Result<LlmResponse> = runCatching {
        val params = MessageCreateParams.builder()
            .maxTokens(currentOptions.maxTokens)
            .addUserMessage(request.text)
            .model(currentOptions.model ?: DEFAULT_ANTHROPIC_MODEL_ID)
            .build()

        val message = client.messages().create(params)
        val responseText = message.content()
            .mapNotNull { block -> block.text().orElse(null)?.text() }
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
