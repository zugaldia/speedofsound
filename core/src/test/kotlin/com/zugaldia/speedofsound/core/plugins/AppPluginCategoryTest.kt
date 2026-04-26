package com.zugaldia.speedofsound.core.plugins

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppPluginCategoryTest {

    @Test
    fun `enum has expected values`() {
        val categories = AppPluginCategory.entries
        assertEquals(5, categories.size)
        assertTrue(categories.contains(AppPluginCategory.RECORDER))
        assertTrue(categories.contains(AppPluginCategory.ASR))
        assertTrue(categories.contains(AppPluginCategory.LLM))
        assertTrue(categories.contains(AppPluginCategory.DIRECTOR))
        assertTrue(categories.contains(AppPluginCategory.TEXT_OUTPUT))
    }

    @Test
    fun `enum values can be accessed directly`() {
        val recorder = AppPluginCategory.RECORDER
        val asr = AppPluginCategory.ASR
        val llm = AppPluginCategory.LLM
        val director = AppPluginCategory.DIRECTOR
        assertEquals(AppPluginCategory.RECORDER, recorder)
        assertEquals(AppPluginCategory.ASR, asr)
        assertEquals(AppPluginCategory.LLM, llm)
        assertEquals(AppPluginCategory.DIRECTOR, director)
    }
}
