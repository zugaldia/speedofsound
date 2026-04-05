package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineMoonshineModelConfig
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.models.voice.ModelManager

class SherpaMoonshineAsr(
    options: SherpaMoonshineAsrOptions = SherpaMoonshineAsrOptions(),
) : AsrPlugin<SherpaMoonshineAsrOptions>(initialOptions = options) {
    override val id: String = ID

    companion object {
        const val ID = "ASR_SHERPA_MOONSHINE"
        const val PROVIDER = "cpu"
    }

    private var recognizer: OfflineRecognizer? = null

    override fun enable() {
        super.enable()
        createRecognizer()
    }

    private fun createRecognizer() {
        val modelManager = ModelManager()

        // Validate model exists in the registry and is downloaded
        val model = SUPPORTED_SHERPA_MOONSHINE_ASR_MODELS[currentOptions.modelId]
        if (model == null || !modelManager.isModelDownloaded(currentOptions.modelId)) {
            val reason = if (model == null) "not found" else "not downloaded"
            throw IllegalStateException("Model ${currentOptions.modelId} $reason.")
        }

        val modelPath = modelManager.getModelPath(currentOptions.modelId)
        val encoder = modelPath.resolve(model.components[0].name).toString()
        val decoder = modelPath.resolve(model.components[1].name).toString()
        val tokens = modelPath.resolve(model.components[2].name).toString()

        val moonshine = OfflineMoonshineModelConfig.builder()
            .setEncoder(encoder)
            .setMergedDecoder(decoder)
            .build()

        val modelConfig = OfflineModelConfig.builder()
            .setMoonshine(moonshine)
            .setTokens(tokens)
            .setNumThreads(Runtime.getRuntime().availableProcessors()) // Use all available CPU cores
            .setProvider(PROVIDER)
            .setDebug(currentOptions.enableDebug)
            .build()

        val config = OfflineRecognizerConfig.builder()
            .setOfflineModelConfig(modelConfig)
            .setDecodingMethod("greedy_search")
            .build()

        recognizer = OfflineRecognizer(config)
        log.info("Recognizer created: ${model.id}")
    }

    private fun ensureRecognizerLanguage() {
        val model = SUPPORTED_SHERPA_MOONSHINE_ASR_MODELS[currentOptions.modelId] ?: return
        if (currentOptions.language !in model.languages) {
            log.warn(
                "Language ${currentOptions.language.iso2} is not supported by model ${currentOptions.modelId}. " +
                    "Supported: ${model.languages.joinToString { it.iso2 }}"
            )
        }
    }

    override fun transcribe(request: AsrRequest): Result<AsrResponse> = runCatching {
        ensureRecognizerLanguage()
        val currentRecognizer = recognizer ?: error("Recognizer not initialized, plugin must be enabled first")
        try {
            val stream = currentRecognizer.createStream()
            val floatArray = AudioManager.convertPcm16ToFloat(request.audioData)
            stream.acceptWaveform(floatArray, request.audioInfo.sampleRate)

            log.info("Transcribing with ${currentOptions.modelId}")
            currentRecognizer.decode(stream)
            val text = currentRecognizer.getResult(stream).text
            stream.release()
            AsrResponse(text)
        } finally {
            log.info("Transcription completed.")
        }
    }

    private fun closeRecognizer() {
        recognizer?.release()
        recognizer = null
    }

    override fun disable() {
        super.disable()
        closeRecognizer()
    }

    override fun shutdown() {
        closeRecognizer()
        super.shutdown()
    }
}
