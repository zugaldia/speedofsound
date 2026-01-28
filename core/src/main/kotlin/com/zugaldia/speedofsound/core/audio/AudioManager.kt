package com.zugaldia.speedofsound.core.audio

import com.zugaldia.speedofsound.core.audio.AudioConstants.BITS_PER_BYTE
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Path
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.io.encoding.Base64

/**
 * Utility object for managing audio data operations.
 */
object AudioManager {

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
     * Saves raw PCM audio data to a WAV file.
     *
     * @param data Raw PCM audio data as a byte array
     * @param audioInfo Audio format information (sample rate, channels, sample width)
     * @param filePath Path where the WAV file should be saved
     */
    fun saveToWav(data: ByteArray, audioInfo: AudioInfo, filePath: Path) {
        val audioFormat = AudioFormat(
            audioInfo.sampleRate.toFloat(),
            audioInfo.sampleWidth * BITS_PER_BYTE,
            audioInfo.channels,
            true,  // signed
            false  // little endian
        )

        val frameSize = audioInfo.channels * audioInfo.sampleWidth
        val frameLength = data.size.toLong() / frameSize
        ByteArrayInputStream(data).use { byteArrayInputStream ->
            AudioInputStream(byteArrayInputStream, audioFormat, frameLength).use { audioInputStream ->
                val outputFile = filePath.toFile()
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile)
            }
        }
    }
}
