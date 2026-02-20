package com.zugaldia.speedofsound.core.models.text

import com.zugaldia.speedofsound.core.generateUniqueId
import com.zugaldia.speedofsound.core.plugins.llm.LlmProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class TextModelTest {

    @Test
    fun `creates TextModel with all fields`() {
        val uniqueId = generateUniqueId()
        val model = TextModel(id = uniqueId, name = "Test Model", provider = LlmProvider.ANTHROPIC)
        assertEquals(uniqueId, model.id)
        assertEquals("Test Model", model.name)
        assertEquals(LlmProvider.ANTHROPIC, model.provider)
    }

    @Test
    fun `creates TextModel with Google provider`() {
        val model = TextModel(id = "gemini-pro", name = "Gemini Pro", provider = LlmProvider.GOOGLE)
        assertEquals("gemini-pro", model.id)
        assertEquals("Gemini Pro", model.name)
        assertEquals(LlmProvider.GOOGLE, model.provider)
    }

    @Test
    fun `creates TextModel with OpenAI provider`() {
        val model = TextModel(id = "gpt-4", name = "GPT-4", provider = LlmProvider.OPENAI)
        assertEquals("gpt-4", model.id)
        assertEquals("GPT-4", model.name)
        assertEquals(LlmProvider.OPENAI, model.provider)
    }

    @Test
    fun `data class equality works correctly`() {
        val model1 = TextModel(id = "model-1", name = "Test Model", provider = LlmProvider.ANTHROPIC)
        val model2 = TextModel(id = "model-1", name = "Test Model", provider = LlmProvider.ANTHROPIC)
        assertEquals(model1, model2)
        assertEquals(model1.hashCode(), model2.hashCode())
    }

    @Test
    fun `data class copy works correctly`() {
        val original = TextModel(id = "original-id", name = "Original Name", provider = LlmProvider.ANTHROPIC)
        val modified = original.copy(name = "Modified Name")
        assertEquals("original-id", original.id)
        assertEquals("Original Name", original.name)
        assertEquals("original-id", modified.id)
        assertEquals("Modified Name", modified.name)
        assertEquals(LlmProvider.ANTHROPIC, modified.provider)
    }

    @Test
    fun `implements SelectableModel interface`() {
        val model = TextModel(id = "test-id", name = "Test Name", provider = LlmProvider.ANTHROPIC)
        assertEquals("test-id", model.id)
        assertEquals("Test Name", model.name)
    }
}
