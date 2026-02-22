package com.zugaldia.speedofsound.core.plugins.recorder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecorderEventTest {

    @Test
    fun `creates RecordingLevel event with zero level`() {
        val event = RecorderEvent.RecordingLevel(level = 0.0f)
        assertEquals(0.0f, event.level)
    }

    @Test
    fun `creates RecordingLevel event with max level`() {
        val event = RecorderEvent.RecordingLevel(level = 1.0f)
        assertEquals(1.0f, event.level)
    }

    @Test
    fun `creates RecordingLevel event with mid-range level`() {
        val event = RecorderEvent.RecordingLevel(level = 0.5f)
        assertEquals(0.5f, event.level)
    }

    @Test
    fun `RecordingLevel is a RecorderEvent`() {
        val event: RecorderEvent = RecorderEvent.RecordingLevel(level = 0.75f)
        assertTrue(event is RecorderEvent.RecordingLevel)
        if (event is RecorderEvent.RecordingLevel) {
            assertEquals(0.75f, event.level)
        }
    }

    @Test
    fun `data class equality works correctly`() {
        val event1 = RecorderEvent.RecordingLevel(level = 0.5f)
        val event2 = RecorderEvent.RecordingLevel(level = 0.5f)
        assertEquals(event1, event2)
        assertEquals(event1.hashCode(), event2.hashCode())
    }

    @Test
    fun `data class copy works correctly`() {
        val original = RecorderEvent.RecordingLevel(level = 0.3f)
        val modified = original.copy(level = 0.8f)
        assertEquals(0.3f, original.level)
        assertEquals(0.8f, modified.level)
    }
}
