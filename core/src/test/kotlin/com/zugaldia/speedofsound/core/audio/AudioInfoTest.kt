package com.zugaldia.speedofsound.core.audio

import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_CHANNELS_MONO
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_CHANNELS_STEREO
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_FORMAT_PCM
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_RATE_16KHZ
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_RATE_24KHZ
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_WIDTH_16BIT
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_WIDTH_32BIT
import javax.sound.sampled.AudioFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AudioInfoTest {

    @Test
    fun `default AudioInfo has expected values`() {
        val audioInfo = AudioInfo.Default
        assertEquals(AUDIO_FORMAT_PCM, audioInfo.format)
        assertEquals(AUDIO_CHANNELS_MONO, audioInfo.channels)
        assertEquals(AUDIO_SAMPLE_RATE_16KHZ, audioInfo.sampleRate)
        assertEquals(AUDIO_SAMPLE_WIDTH_16BIT, audioInfo.sampleWidth)
    }

    @Test
    fun `toAudioFormat converts bytes to bits correctly for 16-bit audio`() {
        val audioInfo = AudioInfo(
            format = AUDIO_FORMAT_PCM,
            channels = AUDIO_CHANNELS_MONO,
            sampleRate = AUDIO_SAMPLE_RATE_16KHZ,
            sampleWidth = AUDIO_SAMPLE_WIDTH_16BIT // 2 bytes
        )

        val audioFormat = audioInfo.toAudioFormat()
        assertEquals(16.0f, audioFormat.sampleSizeInBits.toFloat())
        assertEquals(AUDIO_SAMPLE_RATE_16KHZ.toFloat(), audioFormat.sampleRate)
        assertEquals(AUDIO_CHANNELS_MONO, audioFormat.channels)
        assertTrue(audioFormat.isBigEndian.not()) // little-endian
        assertEquals(AudioFormat.Encoding.PCM_SIGNED, audioFormat.encoding)
    }

    @Test
    fun `toAudioFormat converts bytes to bits correctly for 32-bit audio`() {
        val audioInfo = AudioInfo(
            format = AUDIO_FORMAT_PCM,
            channels = AUDIO_CHANNELS_STEREO,
            sampleRate = AUDIO_SAMPLE_RATE_24KHZ,
            sampleWidth = AUDIO_SAMPLE_WIDTH_32BIT // 4 bytes
        )

        val audioFormat = audioInfo.toAudioFormat()
        assertEquals(32.0f, audioFormat.sampleSizeInBits.toFloat())
        assertEquals(AUDIO_SAMPLE_RATE_24KHZ.toFloat(), audioFormat.sampleRate)
        assertEquals(AUDIO_CHANNELS_STEREO, audioFormat.channels)
    }

    @Test
    fun `from converts bits to bytes correctly for 16-bit audio`() {
        val audioFormat = AudioFormat(
            AUDIO_SAMPLE_RATE_16KHZ.toFloat(),
            16, // bits
            AUDIO_CHANNELS_MONO,
            true, // signed
            false // little-endian
        )

        val audioInfo = AudioInfo.from(audioFormat)
        assertEquals(AUDIO_FORMAT_PCM, audioInfo.format)
        assertEquals(AUDIO_CHANNELS_MONO, audioInfo.channels)
        assertEquals(AUDIO_SAMPLE_RATE_16KHZ, audioInfo.sampleRate)
        assertEquals(AUDIO_SAMPLE_WIDTH_16BIT, audioInfo.sampleWidth) // 2 bytes
    }

    @Test
    fun `from converts bits to bytes correctly for 32-bit audio`() {
        val audioFormat = AudioFormat(
            AUDIO_SAMPLE_RATE_24KHZ.toFloat(),
            32, // bits
            AUDIO_CHANNELS_STEREO,
            true, // signed
            false // little-endian
        )

        val audioInfo = AudioInfo.from(audioFormat)
        assertEquals(AUDIO_FORMAT_PCM, audioInfo.format)
        assertEquals(AUDIO_CHANNELS_STEREO, audioInfo.channels)
        assertEquals(AUDIO_SAMPLE_RATE_24KHZ, audioInfo.sampleRate)
        assertEquals(AUDIO_SAMPLE_WIDTH_32BIT, audioInfo.sampleWidth) // 4 bytes
    }

    @Test
    fun `round-trip conversion preserves all values`() {
        val original = AudioInfo(
            format = AUDIO_FORMAT_PCM,
            channels = AUDIO_CHANNELS_STEREO,
            sampleRate = AUDIO_SAMPLE_RATE_24KHZ,
            sampleWidth = AUDIO_SAMPLE_WIDTH_32BIT
        )

        val audioFormat = original.toAudioFormat()
        val restored = AudioInfo.from(audioFormat)
        assertEquals(original.format, restored.format)
        assertEquals(original.channels, restored.channels)
        assertEquals(original.sampleRate, restored.sampleRate)
        assertEquals(original.sampleWidth, restored.sampleWidth)
    }

    @Test
    fun `toAudioFormat produces signed PCM encoding`() {
        val audioInfo = AudioInfo.Default
        val audioFormat = audioInfo.toAudioFormat()
        assertEquals(AudioFormat.Encoding.PCM_SIGNED, audioFormat.encoding)
    }

    @Test
    fun `toAudioFormat produces little-endian format`() {
        val audioInfo = AudioInfo.Default
        val audioFormat = audioInfo.toAudioFormat()
        assertFalse(audioFormat.isBigEndian)
    }

    @Test
    fun `data class equality works correctly`() {
        val audioInfo1 = AudioInfo(
            format = AUDIO_FORMAT_PCM,
            channels = AUDIO_CHANNELS_MONO,
            sampleRate = AUDIO_SAMPLE_RATE_16KHZ,
            sampleWidth = AUDIO_SAMPLE_WIDTH_16BIT
        )

        val audioInfo2 = AudioInfo(
            format = AUDIO_FORMAT_PCM,
            channels = AUDIO_CHANNELS_MONO,
            sampleRate = AUDIO_SAMPLE_RATE_16KHZ,
            sampleWidth = AUDIO_SAMPLE_WIDTH_16BIT
        )

        assertEquals(audioInfo1, audioInfo2)
        assertEquals(audioInfo1.hashCode(), audioInfo2.hashCode())
    }

    @Test
    fun `data class copy works correctly`() {
        val original = AudioInfo.Default
        val modified = original.copy(sampleRate = AUDIO_SAMPLE_RATE_24KHZ)
        assertEquals(AUDIO_SAMPLE_RATE_16KHZ, original.sampleRate)
        assertEquals(AUDIO_SAMPLE_RATE_24KHZ, modified.sampleRate)
        assertEquals(original.format, modified.format)
        assertEquals(original.channels, modified.channels)
        assertEquals(original.sampleWidth, modified.sampleWidth)
    }
}
