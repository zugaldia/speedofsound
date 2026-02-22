package com.zugaldia.speedofsound.core.plugins.recorder

import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_CHANNELS_STEREO
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_FORMAT_PCM
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_RATE_24KHZ
import com.zugaldia.speedofsound.core.audio.AudioConstants.AUDIO_SAMPLE_WIDTH_32BIT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecorderOptionsTest {

    @Test
    fun `default RecorderOptions has expected values`() {
        val options = RecorderOptions()
        assertEquals(AudioInfo.Default, options.audioInfo)
        assertFalse(options.computeVolumeLevel)
    }

    @Test
    fun `creates RecorderOptions with custom audio info`() {
        val customAudioInfo = AudioInfo(
            format = AUDIO_FORMAT_PCM,
            channels = AUDIO_CHANNELS_STEREO,
            sampleRate = AUDIO_SAMPLE_RATE_24KHZ,
            sampleWidth = AUDIO_SAMPLE_WIDTH_32BIT
        )
        val options = RecorderOptions(audioInfo = customAudioInfo)
        assertEquals(customAudioInfo, options.audioInfo)
        assertFalse(options.computeVolumeLevel)
    }

    @Test
    fun `creates RecorderOptions with volume level enabled`() {
        val options = RecorderOptions(computeVolumeLevel = true)
        assertEquals(AudioInfo.Default, options.audioInfo)
        assertTrue(options.computeVolumeLevel)
    }

    @Test
    fun `creates RecorderOptions with all custom values`() {
        val customAudioInfo = AudioInfo(
            format = AUDIO_FORMAT_PCM,
            channels = AUDIO_CHANNELS_STEREO,
            sampleRate = AUDIO_SAMPLE_RATE_24KHZ,
            sampleWidth = AUDIO_SAMPLE_WIDTH_32BIT
        )
        val options = RecorderOptions(
            audioInfo = customAudioInfo,
            computeVolumeLevel = true
        )
        assertEquals(customAudioInfo, options.audioInfo)
        assertTrue(options.computeVolumeLevel)
    }

    @Test
    fun `data class equality works correctly`() {
        val options1 = RecorderOptions(computeVolumeLevel = true)
        val options2 = RecorderOptions(computeVolumeLevel = true)
        assertEquals(options1, options2)
        assertEquals(options1.hashCode(), options2.hashCode())
    }

    @Test
    fun `data class copy works correctly`() {
        val original = RecorderOptions(computeVolumeLevel = false)
        val modified = original.copy(computeVolumeLevel = true)
        assertFalse(original.computeVolumeLevel)
        assertTrue(modified.computeVolumeLevel)
        assertEquals(original.audioInfo, modified.audioInfo)
    }
}
