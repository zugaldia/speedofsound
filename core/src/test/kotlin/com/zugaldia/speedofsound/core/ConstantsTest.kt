package com.zugaldia.speedofsound.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstantsTest {

    @Test
    fun `application name is not blank`() {
        assertTrue(APPLICATION_NAME.isNotBlank())
    }

    @Test
    fun `application ID follows reverse domain format`() {
        assertTrue(APPLICATION_ID.contains('.'))
        assertTrue(APPLICATION_ID.startsWith("io."))
    }

    @Test
    fun `application short name is not blank`() {
        assertTrue(APPLICATION_SHORT.isNotBlank())
    }

    @Test
    fun `application short name is safe for file and folder names`() {
        assertEquals(APPLICATION_SHORT, APPLICATION_SHORT.lowercase())
        assertTrue(APPLICATION_SHORT.contains(' ').not())
        assertTrue(APPLICATION_SHORT.all { it.isLowerCase() && it.isLetter() })
    }

    @Test
    fun `environment variable names are not blank`() {
        assertTrue(ANTHROPIC_ENVIRONMENT_VARIABLE.isNotBlank())
        assertTrue(GOOGLE_ENVIRONMENT_VARIABLE.isNotBlank())
        assertTrue(OPENAI_ENVIRONMENT_VARIABLE.isNotBlank())
    }

    @Test
    fun `local API key placeholder contains application short name`() {
        assertTrue(LOCAL_API_KEY_PLACEHOLDER.contains(APPLICATION_SHORT))
    }
}
