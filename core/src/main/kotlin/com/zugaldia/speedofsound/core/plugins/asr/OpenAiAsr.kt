package com.zugaldia.speedofsound.core.plugins.asr

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.core.MultipartField
import com.openai.models.audio.AudioModel
import com.openai.models.audio.transcriptions.TranscriptionCreateParams
import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.LOCAL_API_KEY_PLACEHOLDER
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import java.io.ByteArrayInputStream
import java.io.InputStream

class OpenAiAsr(options: OpenAiAsrOptions = OpenAiAsrOptions()) :
    AsrPlugin<OpenAiAsrOptions>(initialOptions = options) {
    override val id: String = ID

    private var client: OpenAIClient? = null

    companion object {
        const val ID = "ASR_OPENAI"
    }

    override fun updateOptions(options: OpenAiAsrOptions) {
        super.updateOptions(options)
        if (client != null) {
            rebuildClient()
        } // Only rebuild if already enabled
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
        // Refs: https://docs.vllm.ai/en/latest/serving/openai_compatible_server/#transcriptions-api
        val effectiveApiKey = currentOptions.apiKey
            ?: if (!currentOptions.baseUrl.isNullOrEmpty()) LOCAL_API_KEY_PLACEHOLDER else null
        effectiveApiKey?.let { builder.apiKey(it) }

        client = builder.build()
    }

    override fun transcribe(request: AsrRequest): Result<AsrResponse> = runCatching {
        val currentClient = client ?: error("Client not initialized, plugin must be enabled first")
        val wavFile = AudioManager.saveToInMemoryWav(request.audioData, request.audioInfo)
        val audioStream = ByteArrayInputStream(wavFile)
        val fileField = MultipartField.builder<InputStream>()
            .value(audioStream)
            .filename("$APPLICATION_SHORT.wav")
            .build()

        val params: TranscriptionCreateParams = TranscriptionCreateParams.builder()
            .model(currentOptions.modelId)
            .language(currentOptions.language.iso2)
            .file(fileField)
            .build()

        log.info("Transcribing with ${currentOptions.modelId}")
        val result = currentClient.audio().transcriptions().create(params)
        AsrResponse(text = result.asTranscription().text())
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
