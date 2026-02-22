package com.zugaldia.speedofsound.core.audio

import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_FORMAT_PCM
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AudioManagerTest {

    // Test fixture: jfk.wav from resources
    private val jfkWavPath by lazy {
        val resource = javaClass.getResource("/jfk.wav") ?: error("Test resource /jfk.wav not found")
        Paths.get(resource.toURI())
    }

    // Helper: Generate synthetic PCM data with known amplitude
    private fun generateSyntheticPcm16(sampleCount: Int, amplitude: Short = 16384): ByteArray {
        val buffer = ByteBuffer.allocate(sampleCount * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until sampleCount) {
            // Alternate between positive and negative to simulate a square wave
            val sample = if (i % 2 == 0) amplitude else (-amplitude).toShort()
            buffer.putShort(sample)
        }
        return buffer.array()
    }

    // Helper: Generate silence (all zeros)
    private fun generateSilence(sampleCount: Int): ByteArray {
        return ByteArray(sampleCount * 2) // 16-bit samples = 2 bytes each
    }

    @Test
    fun `convertPcm16ToFloat produces correct array length`() {
        val pcmData = generateSyntheticPcm16(sampleCount = 100)
        val floatSamples = AudioManager.convertPcm16ToFloat(pcmData)
        assertEquals(100, floatSamples.size)
    }

    @Test
    fun `convertPcm16ToFloat normalizes values to range -1 to 1`() {
        val pcmData = generateSyntheticPcm16(sampleCount = 100, amplitude = 32767) // Max 16-bit value
        val floatSamples = AudioManager.convertPcm16ToFloat(pcmData)
        floatSamples.forEach { sample ->
            assertTrue(sample >= -1.0f && sample <= 1.0f, "Sample $sample out of range [-1.0, 1.0]")
        }
    }

    @Test
    fun `convertPcm16ToFloat handles silence correctly`() {
        val silence = generateSilence(sampleCount = 50)
        val floatSamples = AudioManager.convertPcm16ToFloat(silence)
        floatSamples.forEach { sample ->
            assertEquals(0.0f, sample, absoluteTolerance = 0.001f)
        }
    }

    @Test
    fun `convertPcm16ToFloat converts max positive value correctly`() {
        // Create a buffer with a single max positive value (32,767)
        val buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putShort(Short.MAX_VALUE)
        val pcmData = buffer.array()

        val floatSamples = AudioManager.convertPcm16ToFloat(pcmData)
        assertEquals(1, floatSamples.size) // 1 sample = 1 Short (16 bits) = 2 bytes
        assertTrue(floatSamples[0] > 0.999f && floatSamples[0] < 1.0f) // 32767 / 32768.0 ≈ 0.99997
    }

    @Test
    fun `convertPcm16ToFloat converts max negative value correctly`() {
        // Create a buffer with a single min negative value (-32768)
        val buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putShort(Short.MIN_VALUE)
        val pcmData = buffer.array()

        val floatSamples = AudioManager.convertPcm16ToFloat(pcmData)
        assertEquals(1, floatSamples.size) // 1 sample = 1 Short (16 bits) = 2 bytes
        assertEquals(-1.0f, floatSamples[0], absoluteTolerance = 0.001f) // -32768 / 32768.0 = -1.0
    }

    @Test
    fun `computeRmsLevel returns zero for silence`() {
        val silence = generateSilence(sampleCount = 100)
        val rmsLevel = AudioManager.computeRmsLevel(silence)
        assertEquals(0.0f, rmsLevel, absoluteTolerance = 0.001f)
    }

    @Test
    fun `computeRmsLevel returns zero for empty array`() {
        val emptyData = ByteArray(0)
        val rmsLevel = AudioManager.computeRmsLevel(emptyData)
        assertEquals(0.0f, rmsLevel)
    }

    @Test
    fun `computeRmsLevel returns value in range 0 to 1`() {
        val pcmData = generateSyntheticPcm16(sampleCount = 200, amplitude = 16384)
        val rmsLevel = AudioManager.computeRmsLevel(pcmData)
        assertTrue(rmsLevel in 0.0f..1.0f, "RMS $rmsLevel out of range [0.0, 1.0]")
    }

    @Test
    fun `computeRmsLevel increases with amplitude`() {
        val lowAmplitude = generateSyntheticPcm16(sampleCount = 100, amplitude = 8192)
        val highAmplitude = generateSyntheticPcm16(sampleCount = 100, amplitude = 24576)
        val lowRms = AudioManager.computeRmsLevel(lowAmplitude)
        val highRms = AudioManager.computeRmsLevel(highAmplitude)
        assertTrue(highRms > lowRms, "High amplitude RMS ($highRms) should be greater than low ($lowRms)")
    }

    @Test
    fun `saveToWav creates file successfully`() {
        val tempFile = Files.createTempFile("test-audio", ".wav")
        try {
            val pcmData = generateSyntheticPcm16(sampleCount = 100)
            val audioInfo = AudioInfo.Default
            val success = AudioManager.saveToWav(pcmData, audioInfo, tempFile)
            assertTrue(success)
            assertTrue(tempFile.exists())
            assertTrue(Files.size(tempFile) > 0, "WAV file should not be empty")
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

    @Test
    fun `saveToInMemoryWav produces non-empty byte array`() {
        val pcmData = generateSyntheticPcm16(sampleCount = 100)
        val audioInfo = AudioInfo.Default
        val wavBytes = AudioManager.saveToInMemoryWav(pcmData, audioInfo)
        assertTrue(wavBytes.isNotEmpty())

        // WAV files start with the "RIFF" header
        assertEquals('R'.code.toByte(), wavBytes[0])
        assertEquals('I'.code.toByte(), wavBytes[1])
        assertEquals('F'.code.toByte(), wavBytes[2])
        assertEquals('F'.code.toByte(), wavBytes[3])
    }

    @Test
    fun `saveToInMemoryWav includes WAVE format identifier`() {
        val pcmData = generateSyntheticPcm16(sampleCount = 50)
        val audioInfo = AudioInfo.Default
        val wavBytes = AudioManager.saveToInMemoryWav(pcmData, audioInfo)

        // "WAVE" identifier should be at bytes 8-11
        assertTrue(wavBytes.size > 11)
        assertEquals('W'.code.toByte(), wavBytes[8])
        assertEquals('A'.code.toByte(), wavBytes[9])
        assertEquals('V'.code.toByte(), wavBytes[10])
        assertEquals('E'.code.toByte(), wavBytes[11])
    }

    @Test
    fun `loadFromWav successfully loads jfk wav file`() {
        val (samples, audioInfo) = AudioManager.loadFromWav(jfkWavPath)
        assertTrue(samples.isNotEmpty(), "Loaded samples should not be empty")

        // JFK.wav properties: 16-bit mono PCM at 16kHz, 11 seconds duration
        assertEquals(AUDIO_FORMAT_PCM, audioInfo.format)
        assertEquals(1, audioInfo.channels, "Expected mono audio")
        assertEquals(16000, audioInfo.sampleRate, "Expected 16kHz sample rate")
        assertEquals(2, audioInfo.sampleWidth, "Expected 16-bit (2 bytes) samples")

        // Verify sample count matches the expected duration (~11 seconds at 16kHz)
        // 16,000 samples/sec * 11 sec * 2 bytes/sample = 352 kilobytes
        val expectedSampleCount = 16000 * 11 * 2
        assertTrue(samples.size in (expectedSampleCount - 1000)..(expectedSampleCount + 1000),
            "Expected ~$expectedSampleCount bytes, got ${samples.size}")
    }

    @Test
    fun `loadFromWav produces valid AudioInfo from jfk wav`() {
        val (_, audioInfo) = AudioManager.loadFromWav(jfkWavPath)

        // Verify AudioInfo can be converted to AudioFormat without error
        val audioFormat = audioInfo.toAudioFormat()
        assertEquals(audioInfo.sampleRate.toFloat(), audioFormat.sampleRate)
        assertEquals(audioInfo.channels, audioFormat.channels)
    }

    @Test
    fun `roundtrip saveToWav and loadFromWav preserves data`() {
        val tempFile = Files.createTempFile("test-roundtrip", ".wav")
        try {
            // Save
            val originalPcm = generateSyntheticPcm16(sampleCount = 200)
            val originalInfo = AudioInfo.Default
            val saveSuccess = AudioManager.saveToWav(originalPcm, originalInfo, tempFile)
            assertTrue(saveSuccess)

            // Load
            val (loadedPcm, loadedInfo) = AudioManager.loadFromWav(tempFile)

            // Verify
            assertEquals(originalPcm.size, loadedPcm.size)
            assertEquals(originalInfo.format, loadedInfo.format)
            assertEquals(originalInfo.channels, loadedInfo.channels)
            assertEquals(originalInfo.sampleRate, loadedInfo.sampleRate)
            assertEquals(originalInfo.sampleWidth, loadedInfo.sampleWidth)
            assertTrue(originalPcm.contentEquals(loadedPcm))
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

    @Test
    fun `roundtrip with jfk wav preserves data integrity`() {
        val tempFile = Files.createTempFile("test-jfk-roundtrip", ".wav")
        try {
            // Load original
            val (originalPcm, originalInfo) = AudioManager.loadFromWav(jfkWavPath)

            // Save to temp
            val saveSuccess = AudioManager.saveToWav(originalPcm, originalInfo, tempFile)
            assertTrue(saveSuccess)

            // Verify
            val (reloadedPcm, reloadedInfo) = AudioManager.loadFromWav(tempFile)
            assertEquals(originalPcm.size, reloadedPcm.size)
            assertEquals(originalInfo, reloadedInfo)
            assertTrue(originalPcm.contentEquals(reloadedPcm))
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

    @Test
    fun `computeRmsLevel on jfk wav returns reasonable value`() {
        val (samples, _) = AudioManager.loadFromWav(jfkWavPath)
        val rmsLevel = AudioManager.computeRmsLevel(samples)

        // JFK speech should have non-zero RMS
        assertTrue(rmsLevel > 0.0f, "Speech audio should have non-zero RMS")
        assertTrue(rmsLevel <= 1.0f, "RMS should not exceed 1.0")
    }

    @Test
    fun `convertPcm16ToFloat on jfk wav produces valid float samples`() {
        val (samples, _) = AudioManager.loadFromWav(jfkWavPath)
        val floatSamples = AudioManager.convertPcm16ToFloat(samples)
        assertTrue(floatSamples.isNotEmpty())
        floatSamples.forEach { sample ->
            assertTrue(sample >= -1.0f && sample <= 1.0f, "Sample out of range: $sample")
        }

        // Verify at least some samples are non-zero (speech has amplitude)
        val nonZeroCount = floatSamples.count { abs(it) > 0.01f }
        assertTrue(nonZeroCount > 0, "Speech should have non-zero samples")
    }

    @Test
    fun `saveToWav handles invalid path gracefully`() {
        val invalidPath = Path("/nonexistent/directory/that/does/not/exist/test.wav")
        val pcmData = generateSyntheticPcm16(sampleCount = 10)
        val audioInfo = AudioInfo.Default
        val success = AudioManager.saveToWav(pcmData, audioInfo, invalidPath)
        assertFalse(success, "Should return false for invalid path")
    }

    @Test
    fun `saveToInMemoryWav handles empty pcm data`() {
        val emptyPcm = ByteArray(0)
        val audioInfo = AudioInfo.Default

        // Should not throw, just produce a minimal WAV
        val wavBytes = AudioManager.saveToInMemoryWav(emptyPcm, audioInfo)
        assertTrue(wavBytes.isNotEmpty(), "Should produce at least WAV header")
    }
}
