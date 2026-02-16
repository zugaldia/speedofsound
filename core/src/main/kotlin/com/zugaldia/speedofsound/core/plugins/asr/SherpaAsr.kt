package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.models.voice.ModelManager

class SherpaAsr(
    options: SherpaAsrOptions = SherpaAsrOptions(),
) : AsrPlugin<SherpaAsrOptions>(initialOptions = options) {
    override val id: String = ID

    companion object {
        const val ID = "ASR_SHERPA"

        // Ideally, we use "cuda" for faster inference whenever available (Sherpa fallbacks to CPU if CUDA is not
        // available). However, it seems that the official JAR files do not include this support. Assuming we can
        // access the GPU from the sandboxed environment, we need a build of Sherpa with -DSHERPA_ONNX_ENABLE_GPU=ON.
        // The next step is to make it work outside Flatpak/Snap (CLI), then we can tackle the sandboxes.
        const val PROVIDER = "cpu"
    }

    private var recognizer: OfflineRecognizer? = null
    private var recognizerLanguage: Language? = null

    override fun enable() {
        super.enable()
        createRecognizer()
    }

    private fun createRecognizer() {
        val modelManager = ModelManager()
        val model = SUPPORTED_SHERPA_ASR_MODELS[currentOptions.modelId]
            ?: throw IllegalArgumentException("Model not found: ${currentOptions.modelId}.")
        if (!modelManager.isModelDownloaded(currentOptions.modelId)) {
            throw IllegalStateException("Model not downloaded: ${currentOptions.modelId}.")
        }

        val modelPath = modelManager.getModelPath(currentOptions.modelId)
        val encoder = modelPath.resolve(model.components[0].name).toString()
        val decoder = modelPath.resolve(model.components[1].name).toString()
        val tokens = modelPath.resolve(model.components[2].name).toString()

        val whisper = OfflineWhisperModelConfig.builder()
            .setEncoder(encoder)
            .setDecoder(decoder)
            .setLanguage(currentOptions.language.iso2)
            .setTask("transcribe")
            .build()

        val modelConfig = OfflineModelConfig.builder()
            .setWhisper(whisper)
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
        recognizerLanguage = currentOptions.language
        log.info("Recognizer created: ${model.id}/${recognizerLanguage?.iso2}")
    }

    private fun ensureRecognizerLanguage() {
        if (currentOptions.language != recognizerLanguage) {
            log.info("Language changed, reinitializing.")
            recognizer?.release()
            createRecognizer()
        }
    }

    override fun transcribe(request: AsrRequest): Result<AsrResponse> = runCatching {
        ensureRecognizerLanguage()
        val currentRecognizer = recognizer ?: error("Recognizer not initialized, plugin must be enabled first")
        try {
            val stream = currentRecognizer.createStream()
            val floatArray = AudioManager.convertPcm16ToFloat(request.audioData)
            stream.acceptWaveform(floatArray, request.audioInfo.sampleRate)
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
        recognizerLanguage = null
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
