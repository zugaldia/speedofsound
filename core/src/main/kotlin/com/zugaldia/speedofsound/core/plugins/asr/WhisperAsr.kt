package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.models.ModelManager
import com.zugaldia.speedofsound.core.models.SUPPORTED_ASR_MODELS
import com.zugaldia.speedofsound.core.plugins.AppPlugin

class WhisperAsr(
    private val options: WhisperOptions = WhisperOptions(),
) : AppPlugin<AsrState, WhisperOptions>(
    initialOptions = options,
    initialState = AsrState(),
) {
    companion object {
        // Ideally, we use CUDA for faster inference whenever available (Sherpa fallbacks to CPU if CUDA is not
        // available). However, it seems that the official JAR files do not include this support. Assuming we can
        // access the GPU from the sandboxed environment, we need a build of Sherpa with -DSHERPA_ONNX_ENABLE_GPU=ON.
        // The next step is to make it work outside Flatpak/Snap (CLI), then we can tackle the sandboxes.
        const val PROVIDER = "cuda"
    }

    private var recognizer: OfflineRecognizer? = null

    override fun initialize() {
        super.initialize()

        val modelManager = ModelManager()
        val model = SUPPORTED_ASR_MODELS[options.modelID]
            ?: throw IllegalArgumentException("Model not found: ${options.modelID}.")
        if (!modelManager.isModelDownloaded(options.modelID)) {
            throw IllegalStateException("Model not downloaded: ${options.modelID}.")
        }

        val modelPath = modelManager.getModelPath(options.modelID)
        val encoder = modelPath.resolve(model.encoder).toString()
        val decoder = modelPath.resolve(model.decoder).toString()
        val tokens = modelPath.resolve(model.tokens).toString()

        val whisper = OfflineWhisperModelConfig.builder()
            .setEncoder(encoder)
            .setDecoder(decoder)
            .setLanguage(options.language.iso2)
            .setTask("transcribe")
            .build()

        val modelConfig = OfflineModelConfig.builder()
            .setWhisper(whisper)
            .setTokens(tokens)
            .setNumThreads(Runtime.getRuntime().availableProcessors()) // Use all available CPU cores
            .setProvider(PROVIDER)
            .setDebug(true)
            .build()

        val config = OfflineRecognizerConfig.builder()
            .setOfflineModelConfig(modelConfig)
            .setDecodingMethod("greedy_search")
            .build()

        recognizer = OfflineRecognizer(config)
    }

    override fun enable() {
        super.enable()
    }

    fun transcribe(audioData: FloatArray, audioInfo: AudioInfo = AudioInfo.Default): Result<String> = runCatching {
        val currentRecognizer = recognizer ?: throw IllegalStateException("Recognizer not initialized")

        try {
            val stream = currentRecognizer.createStream()
            stream.acceptWaveform(audioData, audioInfo.sampleRate)
            currentRecognizer.decode(stream)
            val text = currentRecognizer.getResult(stream).text
            stream.release()
            text
        } finally {
            log.info("Transcription completed.")
        }
    }

    override fun disable() {
        super.disable()
    }

    override fun shutdown() {
        recognizer?.release()
        recognizer = null
        super.shutdown()
    }
}
