package com.zugaldia.speedofsound.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.zugaldia.speedofsound.core.ANTHROPIC_ENVIRONMENT_VARIABLE
import com.zugaldia.speedofsound.core.GOOGLE_ENVIRONMENT_VARIABLE
import com.zugaldia.speedofsound.core.OPENAI_ENVIRONMENT_VARIABLE
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlm
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlm
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlmOptions
import com.zugaldia.speedofsound.core.plugins.llm.LlmPlugin
import com.zugaldia.speedofsound.core.plugins.llm.LlmPluginOptions
import com.zugaldia.speedofsound.core.plugins.llm.LlmProvider
import com.zugaldia.speedofsound.core.plugins.llm.LlmRequest
import com.zugaldia.speedofsound.core.plugins.llm.OpenAiLlm
import com.zugaldia.speedofsound.core.plugins.llm.OpenAiLlmOptions
import org.slf4j.LoggerFactory

class LlmCommand : CliktCommand(name = "llm") {
    override val printHelpOnEmptyArgs = true
    private val logger = LoggerFactory.getLogger(LlmCommand::class.java)

    private val provider: LlmProvider by option(
        "--provider", "-p",
        help = "LLM provider to use (anthropic, google, openai)"
    ).choice(
        "anthropic" to LlmProvider.ANTHROPIC,
        "google" to LlmProvider.GOOGLE,
        "openai" to LlmProvider.OPENAI,
        ignoreCase = true
    ).default(LlmProvider.ANTHROPIC)

    private val baseUrl: String? by option(
        "--base-url", "-b",
        help = "Optional base URL for the LLM API"
    )

    private val apiKey: String? by option(
        "--api-key", "-k",
        help = "Optional API key for the LLM provider"
    )

    private val text: List<String> by argument(
        name = "text",
        help = "Text prompt to send to the LLM (multiple words allowed, no quotes needed)"
    ).multiple(required = true)

    override fun run() {
        logger.info("Using provider: $provider")
        val request = LlmRequest(text = text.joinToString(" "))
        when (provider) {
            LlmProvider.ANTHROPIC -> runAnthropic(request)
            LlmProvider.GOOGLE -> runGoogle(request)
            LlmProvider.OPENAI -> runOpenAi(request)
        }
    }

    private fun runAnthropic(request: LlmRequest) {
        logger.info("Initializing Anthropic LLM.")
        val resolvedApiKey = apiKey ?: System.getenv(ANTHROPIC_ENVIRONMENT_VARIABLE)
        val options = AnthropicLlmOptions(baseUrl = baseUrl, apiKey = resolvedApiKey)
        executeWithLlm(AnthropicLlm(options), request)
    }

    private fun runGoogle(request: LlmRequest) {
        logger.info("Initializing Google LLM.")
        val resolvedApiKey = apiKey ?: System.getenv(GOOGLE_ENVIRONMENT_VARIABLE)
        val options = GoogleLlmOptions(baseUrl = baseUrl, apiKey = resolvedApiKey)
        executeWithLlm(GoogleLlm(options), request)
    }

    private fun runOpenAi(request: LlmRequest) {
        logger.info("Initializing OpenAI LLM.")
        val resolvedApiKey = apiKey ?: System.getenv(OPENAI_ENVIRONMENT_VARIABLE)
        val options = OpenAiLlmOptions(baseUrl = baseUrl, apiKey = resolvedApiKey)
        executeWithLlm(OpenAiLlm(options), request)
    }

    private fun <T : LlmPluginOptions> executeWithLlm(llm: LlmPlugin<T>, request: LlmRequest) {
        llm.initialize()
        llm.enable()

        logger.info("Sending request to LLM.")
        llm.generate(request).onSuccess { response ->
            logger.info("Response received successfully.")
            println(response.text)
        }.onFailure { error ->
            logger.error("LLM generation failed: ${error.message}", error)
        }

        llm.disable()
        llm.shutdown()
    }
}
