package com.zugaldia.speedofsound.core.plugins.asr

import kotlin.test.Test
import kotlin.test.assertEquals

class AsrResponseTest {

    @Test
    fun `creates response with text`() {
        val response = AsrResponse(text = "Hello world")
        assertEquals("Hello world", response.text)
    }

    @Test
    fun `data class equality works correctly`() {
        val response1 = AsrResponse(text = "Test")
        val response2 = AsrResponse(text = "Test")
        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
    }

    @Test
    fun `data class copy works correctly`() {
        val original = AsrResponse(text = "Original")
        val modified = original.copy(text = "Modified")
        assertEquals("Original", original.text)
        assertEquals("Modified", modified.text)
    }
}
