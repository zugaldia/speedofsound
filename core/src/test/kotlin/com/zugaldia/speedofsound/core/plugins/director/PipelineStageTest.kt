package com.zugaldia.speedofsound.core.plugins.director

import kotlin.test.Test
import kotlin.test.assertEquals

class PipelineStageTest {

    @Test
    fun `enum has expected values`() {
        val stages = PipelineStage.entries
        assertEquals(3, stages.size)
        assertEquals(PipelineStage.RECORDING, stages[0])
        assertEquals(PipelineStage.TRANSCRIPTION, stages[1])
        assertEquals(PipelineStage.POLISHING, stages[2])
    }

    @Test
    fun `RECORDING stage exists`() {
        val stage = PipelineStage.RECORDING
        assertEquals("RECORDING", stage.name)
    }

    @Test
    fun `TRANSCRIPTION stage exists`() {
        val stage = PipelineStage.TRANSCRIPTION
        assertEquals("TRANSCRIPTION", stage.name)
    }

    @Test
    fun `POLISHING stage exists`() {
        val stage = PipelineStage.POLISHING
        assertEquals("POLISHING", stage.name)
    }

    @Test
    fun `valueOf works correctly`() {
        assertEquals(PipelineStage.RECORDING, PipelineStage.valueOf("RECORDING"))
        assertEquals(PipelineStage.TRANSCRIPTION, PipelineStage.valueOf("TRANSCRIPTION"))
        assertEquals(PipelineStage.POLISHING, PipelineStage.valueOf("POLISHING"))
    }

    @Test
    fun `enum ordinals are sequential`() {
        assertEquals(0, PipelineStage.RECORDING.ordinal)
        assertEquals(1, PipelineStage.TRANSCRIPTION.ordinal)
        assertEquals(2, PipelineStage.POLISHING.ordinal)
    }
}
