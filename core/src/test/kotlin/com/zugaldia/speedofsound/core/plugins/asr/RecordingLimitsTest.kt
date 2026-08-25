package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.plugins.director.WHISPER_MAX_RECORDING_DURATION_MS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecordingLimitsTest {

    @Test
    fun `only sherpa whisper is limited to thirty seconds`() {
        assertTrue(isLimitedToThirtySeconds(AsrProvider.SHERPA_WHISPER))
        assertFalse(isLimitedToThirtySeconds(AsrProvider.SHERPA_PARAKEET))
        assertFalse(isLimitedToThirtySeconds(AsrProvider.SHERPA_CANARY))
        assertFalse(isLimitedToThirtySeconds(AsrProvider.OPENAI))
        assertFalse(isLimitedToThirtySeconds(null))
    }

    @Test
    fun `whisper caps longer timeouts`() {
        assertEquals(
            WHISPER_MAX_RECORDING_DURATION_MS,
            clampRecordingDurationMs(300_000L, AsrProvider.SHERPA_WHISPER)
        )
    }

    @Test
    fun `whisper keeps shorter timeouts`() {
        assertEquals(10_000L, clampRecordingDurationMs(10_000L, AsrProvider.SHERPA_WHISPER))
    }

    @Test
    fun `other backends keep the configured timeout`() {
        assertEquals(300_000L, clampRecordingDurationMs(300_000L, AsrProvider.SHERPA_PARAKEET))
        assertEquals(300_000L, clampRecordingDurationMs(300_000L, AsrProvider.OPENAI))
    }
}
