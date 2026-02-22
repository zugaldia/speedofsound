package com.zugaldia.speedofsound.core.plugins.llm

import kotlin.test.Test
import kotlin.test.assertEquals

class LlmRequestTest {

    @Test
    fun `creates request with text`() {
        val request = LlmRequest(text = "Please polish this text")
        assertEquals("Please polish this text", request.text)
    }

    @Test
    fun `data class equality works correctly`() {
        val request1 = LlmRequest(text = "Test")
        val request2 = LlmRequest(text = "Test")
        assertEquals(request1, request2)
        assertEquals(request1.hashCode(), request2.hashCode())
    }

    @Test
    fun `data class copy works correctly`() {
        val original = LlmRequest(text = "Original")
        val modified = original.copy(text = "Modified")
        assertEquals("Original", original.text)
        assertEquals("Modified", modified.text)
    }
}
