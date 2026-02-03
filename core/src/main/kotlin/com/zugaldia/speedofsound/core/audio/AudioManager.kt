package com.zugaldia.speedofsound.core.audio

import com.k2fsa.sherpa.onnx.LibraryUtils
import com.k2fsa.sherpa.onnx.WaveReader
import com.k2fsa.sherpa.onnx.WaveWriter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import kotlin.io.encoding.Base64
import kotlin.math.sqrt

/**
 * Utility object for managing audio data operations.
 */
object AudioManager {

    /**
     * Maximum value of a signed 16-bit PCM sample (2^15)
     * Used for normalizing 16-bit PCM audio to float range [-1.0, 1.0]
     */
    private const val PCM_16_MAX_VALUE = 32768.0f

    init {
        // Required by some Sherpa ONNX functions (like WaveWriter) to work
        LibraryUtils.enableDebug()
        LibraryUtils.load()
    }

    /**
     * Encodes audio data to a Base64 string.
     *
     * @param data Raw audio data as a byte array
     * @return Base64-encoded string representation of the audio data
     */
    fun encodeToBase64(data: ByteArray): String {
        return Base64.encode(data)
    }

    /**
     * Decodes Base64-encoded audio data back to a byte array.
     *
     * @param base64 Base64-encoded audio data string
     * @return Decoded audio data as a byte array
     */
    fun decodeFromBase64(base64: String): ByteArray {
        return Base64.decode(base64)
    }

    /**
     * Converts 16-bit signed PCM audio data to normalized float samples (used by Sherpa ONNX)
     *
     * The input ByteArray contains 16-bit signed PCM samples in little-endian format.
     * Each sample is converted to a float value in the range [-1.0, 1.0].
     *
     * @param data Raw 16-bit PCM audio data as a byte array (little-endian)
     * @return Float array with normalized samples in the range [-1.0, 1.0]
     */
    fun convertPcm16ToFloat(data: ByteArray): FloatArray {
        val shortBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val samples = FloatArray(shortBuffer.remaining())
        for (i in samples.indices) {
            samples[i] = shortBuffer.get() / PCM_16_MAX_VALUE
        }

        return samples
    }

    /**
     * Computes the RMS (Root Mean Square) volume level from 16-bit PCM audio data.
     *
     * RMS is the standard measure for perceived loudness of audio signals.
     * The result is normalized to the range [0.0, 1.0].
     *
     * @param data Raw 16-bit PCM audio data as a byte array (little-endian)
     * @return Normalized RMS volume level in range [0.0, 1.0]
     */
    fun computeRmsLevel(data: ByteArray): Float {
        val shortBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val sampleCount = shortBuffer.remaining()
        if (data.isEmpty() || sampleCount == 0) return 0f

        var sumOfSquares = 0.0
        for (i in 0 until sampleCount) {
            val sample = shortBuffer.get().toDouble() / PCM_16_MAX_VALUE
            sumOfSquares += sample * sample
        }

        val rms = sqrt(sumOfSquares / sampleCount)
        return rms.coerceIn(0.0, 1.0).toFloat()
    }

    /**
     * Saves audio data to a WAV file using Sherpa ONNX WaveWriter.
     *
     * @param samples Audio samples as a float array
     * @param sampleRate Sample rate in Hz
     * @param filePath Path where the WAV file should be saved
     * @return true if the file was written successfully, false otherwise
     */
    fun saveToWav(samples: FloatArray, sampleRate: Int, filePath: Path): Boolean {
        return WaveWriter.write(filePath.toString(), samples, sampleRate)
    }

    /**
     * Loads audio data from a WAV file using Sherpa ONNX WaveReader.
     *
     * @param filePath Path to the WAV file to load
     * @return Pair of samples (FloatArray) and sample rate (Int)
     */
    fun loadFromWav(filePath: Path): Pair<FloatArray, Int> {
        val waveReader = WaveReader(filePath.toString())
        return Pair(waveReader.samples, waveReader.sampleRate)
    }
}
