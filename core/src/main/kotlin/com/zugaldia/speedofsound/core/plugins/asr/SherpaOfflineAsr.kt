package com.zugaldia.speedofsound.core.plugins.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.models.voice.ModelManager
import com.zugaldia.speedofsound.core.models.voice.VoiceModel
import java.nio.file.Path

/**
 * Abstract base class for Sherpa ONNX offline ASR plugins.
 *
 * Handles the common recognizer lifecycle (create, language switch, transcribe, close)
 * while delegating the model-specific OfflineModelConfig setup to subclasses.
 */
abstract class SherpaOfflineAsr<Options : AsrPluginOptions>(
    initialOptions: Options,
) : AsrPlugin<Options>(initialOptions = initialOptions) {

    companion object {
        // Ideally, we use "cuda" for faster inference whenever available (Sherpa fallbacks to CPU if CUDA is not
        // available). However, it seems that the official JAR files do not include this support. Assuming we can
        // access the GPU from the sandboxed environment, we need a build of Sherpa with -DSHERPA_ONNX_ENABLE_GPU=ON.
        // The next step is to make it work outside Flatpak/Snap (CLI), then we can tackle the sandboxes.
        const val PROVIDER = "cpu"
    }

    private var recognizer: OfflineRecognizer? = null
    private var recognizerLanguage: Language? = null

    /**
     * Returns the supported model registry for this plugin.
     */
    protected abstract fun supportedModels(): Map<String, VoiceModel>

    /**
     * Applies the model-specific configuration (e.g. Whisper, Canary, Transducer) to the
     * [OfflineModelConfig.Builder].
     *
     * Subclasses should extract the component paths they need from [model] and [modelPath],
     * call the appropriate setter (e.g. `setWhisper`, `setCanary`, `setTransducer`) on the builder,
     * and return the resulting builder.
     */
    protected abstract fun applyModelSpecificConfig(
        builder: OfflineModelConfig.Builder,
        model: VoiceModel,
        modelPath: Path,
        language: Language,
    ): OfflineModelConfig.Builder

    /**
     * Called before the recognizer is created. Override for any pre-creation setup
     * such as extracting bundled models.
     */
    protected open fun onBeforeCreateRecognizer(modelManager: ModelManager) {
        // Default: no-op
    }

    override fun enable() {
        super.enable()
        createRecognizer()
    }

    private fun createRecognizer() {
        val modelManager = ModelManager()
        onBeforeCreateRecognizer(modelManager)

        val model = supportedModels()[currentOptions.modelId]
        if (model == null || !modelManager.isModelDownloaded(currentOptions.modelId)) {
            val reason = if (model == null) "not found" else "not downloaded"
            throw IllegalStateException("Model ${currentOptions.modelId} $reason.")
        }

        val modelPath = modelManager.getModelPath(currentOptions.modelId)
        val tokens = modelPath.resolve(model.components.last().name).toString()

        val modelConfigBuilder = applyModelSpecificConfig(
            OfflineModelConfig.builder(),
            model,
            modelPath,
            currentOptions.language,
        )

        val modelConfig = modelConfigBuilder
            .setTokens(tokens)
            .setNumThreads(Runtime.getRuntime().availableProcessors())
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
        val stream = currentRecognizer.createStream()
        try {
            log.info("Transcribing with ${currentOptions.modelId}/$recognizerLanguage")
            val floatArray = AudioManager.convertPcm16ToFloat(request.audioData)
            stream.acceptWaveform(floatArray, request.audioInfo.sampleRate)
            currentRecognizer.decode(stream)
            val text = currentRecognizer.getResult(stream).text
            AsrResponse(text)
        } finally {
            stream.release()
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
