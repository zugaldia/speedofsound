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

    private lateinit var session: OrtSession
    private lateinit var baseInputs: Map<String, OnnxTensor>

    override fun initialize() {
        super.initialize()

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

        log.info("ONNX initialized ($RESOURCE_PATH).")
    }

    override fun transcribe(request: AsrRequest): Result<AsrResponse> = runCatching {
        val audioTensor: OnnxTensor = fromRawPcmBytes(env, request.audioData)
        audioTensor.use {
            val inputs = mutableMapOf<String, OnnxTensor>()
            baseInputs.toMap(inputs)
            inputs["audio_pcm"] = audioTensor
            val outputs = session.run(inputs)
            val recognizedText = outputs.use {
                @Suppress("UNCHECKED_CAST")
                (outputs[0].value as Array<Array<String>>)[0][0]
            }

            AsrResponse(recognizedText.trim())
        }
    }

    override fun shutdown() {
        baseInputs.values.forEach { it.close() }
        session.close()
        super.shutdown()
    }

    companion object {
        const val ID = "ASR_ONNX"
        private const val RESOURCE_PATH = "/models/asr/whisper_cpu_int8_model.onnx"
        private const val DEFAULT_MAX_LENGTH = 200
    }
}
