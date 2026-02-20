package com.zugaldia.speedofsound.core.plugins.director

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DirectorEventTest {

    @Test
    fun `RecordingStarted is a DirectorEvent`() {
        val event: DirectorEvent = DirectorEvent.RecordingStarted
        assertTrue(event is DirectorEvent.RecordingStarted)
    }

    @Test
    fun `TranscriptionStarted is a DirectorEvent`() {
        val event: DirectorEvent = DirectorEvent.TranscriptionStarted
        assertTrue(event is DirectorEvent.TranscriptionStarted)
    }

    @Test
    fun `PolishingStarted is a DirectorEvent`() {
        val event: DirectorEvent = DirectorEvent.PolishingStarted
        assertTrue(event is DirectorEvent.PolishingStarted)
    }

    @Test
    fun `PipelineCancelled is a DirectorEvent`() {
        val event: DirectorEvent = DirectorEvent.PipelineCancelled
        assertTrue(event is DirectorEvent.PipelineCancelled)
    }

    @Test
    fun `creates PipelineCompleted with all fields`() {
        val event = DirectorEvent.PipelineCompleted(
            rawTranscription = "raw text",
            polishedText = "polished text",
            finalResult = "final result"
        )
        assertEquals("raw text", event.rawTranscription)
        assertEquals("polished text", event.polishedText)
        assertEquals("final result", event.finalResult)
    }

    @Test
    fun `creates PipelineCompleted with null polished text`() {
        val event = DirectorEvent.PipelineCompleted(
            rawTranscription = "raw text",
            polishedText = null,
            finalResult = "raw text"
        )
        assertEquals("raw text", event.rawTranscription)
        assertEquals(null, event.polishedText)
        assertEquals("raw text", event.finalResult)
    }

    @Test
    fun `PipelineCompleted is a DirectorEvent`() {
        val event: DirectorEvent = DirectorEvent.PipelineCompleted(
            rawTranscription = "test",
            polishedText = "polished",
            finalResult = "final"
        )
        assertTrue(event is DirectorEvent.PipelineCompleted)
    }

    @Test
    fun `PipelineCompleted data class equality works correctly`() {
        val event1 = DirectorEvent.PipelineCompleted(
            rawTranscription = "raw",
            polishedText = "polished",
            finalResult = "final"
        )
        val event2 = DirectorEvent.PipelineCompleted(
            rawTranscription = "raw",
            polishedText = "polished",
            finalResult = "final"
        )
        assertEquals(event1, event2)
        assertEquals(event1.hashCode(), event2.hashCode())
    }

    @Test
    fun `PipelineCompleted data class copy works correctly`() {
        val original = DirectorEvent.PipelineCompleted(
            rawTranscription = "original",
            polishedText = "polished",
            finalResult = "final"
        )
        val modified = original.copy(rawTranscription = "modified")
        assertEquals("original", original.rawTranscription)
        assertEquals("modified", modified.rawTranscription)
        assertEquals("polished", modified.polishedText)
        assertEquals("final", modified.finalResult)
    }

    @Test
    fun `creates PipelineError with all fields`() {
        val exception = RuntimeException("Test error")
        val event = DirectorEvent.PipelineError(
            stage = PipelineStage.RECORDING,
            error = exception
        )
        assertEquals(PipelineStage.RECORDING, event.stage)
        assertEquals(exception, event.error)
    }

    @Test
    fun `creates PipelineError with TRANSCRIPTION stage`() {
        val exception = IllegalStateException("Transcription failed")
        val event = DirectorEvent.PipelineError(
            stage = PipelineStage.TRANSCRIPTION,
            error = exception
        )
        assertEquals(PipelineStage.TRANSCRIPTION, event.stage)
        assertEquals(exception, event.error)
    }

    @Test
    fun `creates PipelineError with POLISHING stage`() {
        val exception = IllegalArgumentException("Polish failed")
        val event = DirectorEvent.PipelineError(
            stage = PipelineStage.POLISHING,
            error = exception
        )
        assertEquals(PipelineStage.POLISHING, event.stage)
        assertEquals(exception, event.error)
    }

    @Test
    fun `PipelineError is a DirectorEvent`() {
        val exception = RuntimeException("Error")
        val event: DirectorEvent = DirectorEvent.PipelineError(
            stage = PipelineStage.RECORDING,
            error = exception
        )
        assertTrue(event is DirectorEvent.PipelineError)
    }

    @Test
    fun `PipelineError preserves error message`() {
        val exception = RuntimeException("Specific error message")
        val event = DirectorEvent.PipelineError(
            stage = PipelineStage.RECORDING,
            error = exception
        )
        assertEquals("Specific error message", event.error.message)
    }

    @Test
    fun `PipelineError data class equality works correctly`() {
        val exception = RuntimeException("Test")
        val event1 = DirectorEvent.PipelineError(
            stage = PipelineStage.RECORDING,
            error = exception
        )
        val event2 = DirectorEvent.PipelineError(
            stage = PipelineStage.RECORDING,
            error = exception
        )
        assertEquals(event1, event2)
        assertEquals(event1.hashCode(), event2.hashCode())
    }

    @Test
    fun `PipelineError data class copy works correctly`() {
        val exception1 = RuntimeException("Original error")
        val exception2 = RuntimeException("New error")
        val original = DirectorEvent.PipelineError(
            stage = PipelineStage.RECORDING,
            error = exception1
        )
        val modified = original.copy(
            stage = PipelineStage.TRANSCRIPTION,
            error = exception2
        )
        assertEquals(PipelineStage.RECORDING, original.stage)
        assertEquals(exception1, original.error)
        assertEquals(PipelineStage.TRANSCRIPTION, modified.stage)
        assertEquals(exception2, modified.error)
    }

    @Test
    fun `data objects are singletons`() {
        val event1 = DirectorEvent.RecordingStarted
        val event2 = DirectorEvent.RecordingStarted
        assertTrue(event1 === event2) // Reference equality
    }

    @Test
    fun `all event types can be created`() {
        val events: List<DirectorEvent> = listOf(
            DirectorEvent.RecordingStarted,
            DirectorEvent.TranscriptionStarted,
            DirectorEvent.PolishingStarted,
            DirectorEvent.PipelineCompleted("raw", null, "final"),
            DirectorEvent.PipelineError(PipelineStage.RECORDING, RuntimeException()),
            DirectorEvent.PipelineCancelled
        )
        assertEquals(6, events.size)
        events.forEach { event ->
            assertNotNull(event)
        }
    }
}
