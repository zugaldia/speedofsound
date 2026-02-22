package com.zugaldia.speedofsound.core.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AudioConstantsTest {

    @Test
    fun `audio sample widths are positive`() {
        assertTrue(AudioConstants.AUDIO_SAMPLE_WIDTH_16BIT > 0)
        assertTrue(AudioConstants.AUDIO_SAMPLE_WIDTH_32BIT > 0)
    }

    @Test
    fun `audio sample rates are positive`() {
        assertTrue(AudioConstants.AUDIO_SAMPLE_RATE_16KHZ > 0)
        assertTrue(AudioConstants.AUDIO_SAMPLE_RATE_24KHZ > 0)
    }

    @Test
    fun `audio channels are valid`() {
        assertEquals(1, AudioConstants.AUDIO_CHANNELS_MONO)
        assertEquals(2, AudioConstants.AUDIO_CHANNELS_STEREO)
    }

    @Test
    fun `defaults reference defined constants`() {
        assertEquals(AudioConstants.AUDIO_FORMAT_PCM, AudioConstants.DEFAULT_AUDIO_FORMAT)
        assertEquals(AudioConstants.AUDIO_CHANNELS_MONO, AudioConstants.DEFAULT_AUDIO_CHANNELS)
        assertEquals(AudioConstants.AUDIO_SAMPLE_WIDTH_16BIT, AudioConstants.DEFAULT_AUDIO_SAMPLE_WIDTH)
        assertEquals(AudioConstants.AUDIO_SAMPLE_RATE_16KHZ, AudioConstants.DEFAULT_AUDIO_SAMPLE_RATE)
    }

    @Test
    fun `frame length is positive`() {
        assertTrue(AudioConstants.AUDIO_FRAME_LENGTH_512 > 0)
        assertTrue(AudioConstants.DEFAULT_FRAME_LENGTH > 0)
    }
}
