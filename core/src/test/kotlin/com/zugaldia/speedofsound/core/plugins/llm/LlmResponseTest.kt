package com.zugaldia.speedofsound.core.plugins.llm

import kotlin.test.Test
import kotlin.test.assertEquals

class LlmResponseTest {

    @Test
    fun `creates response with text`() {
        val response = LlmResponse(text = "Polished text")
        assertEquals("Polished text", response.text)
    }

    @Test
    fun `data class equality works correctly`() {
        val response1 = LlmResponse(text = "Test")
        val response2 = LlmResponse(text = "Test")
        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
    }

    @Test
    fun `data class copy works correctly`() {
        val original = LlmResponse(text = "Original")
        val modified = original.copy(text = "Modified")
        assertEquals("Original", original.text)
        assertEquals("Modified", modified.text)
    }
}
