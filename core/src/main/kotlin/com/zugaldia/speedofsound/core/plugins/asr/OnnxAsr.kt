package com.zugaldia.speedofsound.core.plugins.asr

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import com.zugaldia.speedofsound.core.plugins.common.OnnxUtils.createFloatTensor
import com.zugaldia.speedofsound.core.plugins.common.OnnxUtils.createIntTensor
import com.zugaldia.speedofsound.core.plugins.common.OnnxUtils.fromRawPcmBytes
import com.zugaldia.speedofsound.core.plugins.common.OnnxUtils.tensorShape

/*
 * This is an ONNX-pure implementation of Whisper that uses one merged model for inference. In theory, it should be
 * easier to enable CUDA inference optimization (with sessionOptions.addCUDA()) because GPU-ready libraries are
 * provided for ONNX, unlike Sherpa.
 *
 * https://github.com/microsoft/onnxruntime-inference-examples/tree/main/mobile/examples/whisper/local/android
 */
class OnnxAsr(options: OnnxAsrOptions = OnnxAsrOptions()) :
    AsrPlugin<OnnxAsrOptions>(initialOptions = options) {
    override val id: String = ID

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()

    private var session: OrtSession? = null
    private var baseInputs: Map<String, OnnxTensor> = emptyMap()

    override fun enable() {
        super.enable()
        val sessionOptions = OrtSession.SessionOptions()
        sessionOptions.registerCustomOpLibrary(OrtxPackage.getLibraryPath())

        val modelData: ByteArray = javaClass.getResourceAsStream(RESOURCE_PATH)?.use { inputStream ->
            inputStream.readBytes()
        } ?: error("Failed to load model from app resources: $RESOURCE_PATH")

        session = env.createSession(modelData, sessionOptions)
        baseInputs = mapOf(
            "min_length" to createIntTensor(env, intArrayOf(1), tensorShape(1)),
            "max_length" to createIntTensor(env, intArrayOf(DEFAULT_MAX_LENGTH), tensorShape(1)),
            "num_beams" to createIntTensor(env, intArrayOf(1), tensorShape(1)),
            "num_return_sequences" to createIntTensor(env, intArrayOf(1), tensorShape(1)),
            "length_penalty" to createFloatTensor(env, floatArrayOf(1.0f), tensorShape(1)),
            "repetition_penalty" to createFloatTensor(env, floatArrayOf(1.0f), tensorShape(1)),
        )

        log.info("ONNX enabled ($RESOURCE_PATH).")
    }

    override fun transcribe(request: AsrRequest): Result<AsrResponse> = runCatching {
        val currentSession = session ?: error("Session not initialized, plugin must be enabled first")
        val audioTensor: OnnxTensor = fromRawPcmBytes(env, request.audioData)
        audioTensor.use {
            val inputs = mutableMapOf<String, OnnxTensor>()
            baseInputs.toMap(inputs)
            inputs["audio_pcm"] = audioTensor
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
    }

    override fun disable() {
        super.disable()
        closeSession()
    }

    override fun shutdown() {
        closeSession()
        super.shutdown()
    }

    companion object {
        const val ID = "ASR_ONNX"
        private const val RESOURCE_PATH = "/models/asr/whisper_cpu_int8_model.onnx"
        private const val DEFAULT_MAX_LENGTH = 200
    }
}
