package com.zugaldia.speedofsound.core.plugins.common

/*
 * Based on the reference example on:
 * https://github.com/microsoft/onnxruntime-inference-examples/tree/main/mobile/examples/whisper/local/android
 */

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_RATE_16KHZ
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

object OnnxUtils {
    // Whisper limit
    private const val MAX_AUDIO_LENGTH_IN_SECONDS = 30

    fun createIntTensor(env: OrtEnvironment, data: IntArray, shape: LongArray): OnnxTensor =
        OnnxTensor.createTensor(env, IntBuffer.wrap(data), shape)

    fun createFloatTensor(env: OrtEnvironment, data: FloatArray, shape: LongArray): OnnxTensor =
        OnnxTensor.createTensor(env, FloatBuffer.wrap(data), shape)

    fun tensorShape(vararg dims: Long) = longArrayOf(*dims)

    fun fromRawPcmBytes(env: OrtEnvironment, rawBytes: ByteArray): OnnxTensor {
        // Only ByteOrder.LITTLE_ENDIAN is supported
        val rawByteBuffer = ByteBuffer.wrap(rawBytes)
        rawByteBuffer.order(ByteOrder.nativeOrder())
        val floatBuffer = rawByteBuffer.asFloatBuffer()
        val numSamples = minOf(floatBuffer.capacity(), MAX_AUDIO_LENGTH_IN_SECONDS * AUDIO_SAMPLE_RATE_16KHZ)
        return OnnxTensor.createTensor(
            env, floatBuffer, tensorShape(1, numSamples.toLong())
        )
    }

    fun fromRawPcmBytes(env: OrtEnvironment, rawBytes: FloatArray): OnnxTensor {
        val floatBuffer = FloatBuffer.wrap(rawBytes)
        val numSamples = minOf(floatBuffer.capacity(), MAX_AUDIO_LENGTH_IN_SECONDS * AUDIO_SAMPLE_RATE_16KHZ)
        return OnnxTensor.createTensor(
            env, floatBuffer, tensorShape(1, numSamples.toLong())
        )
    }
}
