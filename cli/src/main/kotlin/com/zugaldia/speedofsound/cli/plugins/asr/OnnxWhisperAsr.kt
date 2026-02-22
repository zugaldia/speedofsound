package com.zugaldia.speedofsound.cli.plugins.asr

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.models.voice.ModelManager
import com.zugaldia.speedofsound.core.plugins.asr.AsrPlugin
import com.zugaldia.speedofsound.core.plugins.asr.AsrRequest
import com.zugaldia.speedofsound.core.plugins.asr.AsrResponse
import com.zugaldia.speedofsound.cli.plugins.asr.OnnxUtils.createFloatTensor
import com.zugaldia.speedofsound.cli.plugins.asr.OnnxUtils.createIntTensor
import com.zugaldia.speedofsound.cli.plugins.asr.OnnxUtils.fromRawPcmBytes
import com.zugaldia.speedofsound.cli.plugins.asr.OnnxUtils.tensorShape

/*
 * This is an ONNX-pure implementation of Whisper that uses one merged model for inference. In theory, it should be
 * easier to enable CUDA inference optimization (with sessionOptions.addCUDA()) because GPU-ready libraries are
 * provided for ONNX, unlike Sherpa.
 *
 * NOTE: This provider is incubating and CLI only for now.
 *
 * https://github.com/microsoft/onnxruntime-inference-examples/tree/main/mobile/examples/whisper/local/android
 * https://github.com/microsoft/olive-recipes/tree/main/openai-whisper-large-v3/olive
 */
class OnnxWhisperAsr(options: OnnxWhisperAsrOptions = OnnxWhisperAsrOptions()) :
    AsrPlugin<OnnxWhisperAsrOptions>(initialOptions = options) {
    override val id: String = ID

    private val modelManager = ModelManager()
    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()

    private var session: OrtSession? = null
    private var baseInputs: Map<String, OnnxTensor> = emptyMap()

    override fun enable() {
        super.enable()
        val sessionOptions = OrtSession.SessionOptions()
        sessionOptions.registerCustomOpLibrary(OrtxPackage.getLibraryPath())

        val modelPath = modelManager.getModelPath(DEFAULT_ASR_ONNX_WHISPER_MODEL_ID)
        val modelFile = modelPath.resolve("whisper_cpu_int8_model.onnx").toFile().absolutePath
        session = env.createSession(modelFile, sessionOptions)
        baseInputs = mapOf(
            "min_length" to createIntTensor(env, intArrayOf(1), tensorShape(1)),
            "max_length" to createIntTensor(env, intArrayOf(DEFAULT_MAX_LENGTH), tensorShape(1)),
            "num_beams" to createIntTensor(env, intArrayOf(1), tensorShape(1)),
            "num_return_sequences" to createIntTensor(env, intArrayOf(1), tensorShape(1)),
            "length_penalty" to createFloatTensor(env, floatArrayOf(1.0f), tensorShape(1)),
            "repetition_penalty" to createFloatTensor(env, floatArrayOf(1.0f), tensorShape(1)),
        )

        log.info("ONNX enabled ($modelFile).")
    }

    override fun transcribe(request: AsrRequest): Result<AsrResponse> = runCatching {
        val currentSession = session ?: error("Session not initialized, plugin must be enabled first")
        val floatArray = AudioManager.convertPcm16ToFloat(request.audioData)
        val audioTensor: OnnxTensor = fromRawPcmBytes(env, floatArray)
        audioTensor.use {
            val inputs = mutableMapOf<String, OnnxTensor>()
            baseInputs.toMap(inputs)
            inputs["audio_pcm"] = audioTensor

            log.info("Transcribing with ${currentOptions.modelId}")
            val outputs = currentSession.run(inputs)
            val recognizedText = outputs.use {
                @Suppress("UNCHECKED_CAST")
                (outputs[0].value as Array<Array<String>>)[0][0]
            }

            AsrResponse(recognizedText.trim())
        }
    }

    private fun closeSession() {
        baseInputs.values.forEach { it.close() }
        session?.close()
        session = null
    }

    override fun disable() {
        super.disable()
        closeSession()
    }

    companion object {
        const val ID = "ASR_ONNX_WHISPER"
        private const val DEFAULT_MAX_LENGTH = 200
    }
}
