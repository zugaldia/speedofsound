package com.zugaldia.speedofsound.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UtilsTest {

    @Test
    fun `generateUniqueId returns non-empty string`() {
        val id = generateUniqueId()
        assertTrue(id.isNotBlank())
    }

    @Test
    fun `generateUniqueId returns different IDs on multiple calls`() {
        val id1 = generateUniqueId()
        val id2 = generateUniqueId()
        assertNotEquals(id1, id2)
    }

    @Test
    fun `generateTimestamp returns positive value`() {
        val timestamp = generateTimestamp()
        assertTrue(timestamp > 0)
    }

    @Test
    fun `generateTimestamp increases over time`() {
        val timestamp1 = generateTimestamp()
        Thread.sleep(1) // Sleep for 1ms to ensure time passes
        val timestamp2 = generateTimestamp()
        assertTrue(timestamp2 > timestamp1)
    }

    @Test
    fun `languageFromIso2 finds known language`() {
        val english = languageFromIso2("en")
        assertNotNull(english)
        assertEquals(Language.ENGLISH, english)
    }

    @Test
    fun `languageFromIso2 returns null for unknown code`() {
        val unknown = languageFromIso2("zz")
        assertNull(unknown)
    }

    @Test
    fun `getDataDir returns non-null path`() {
        val dataDir = getDataDir()
        assertNotNull(dataDir)
    }

    @Test
    fun `getDataDir creates directory that exists`() {
        val dataDir = getDataDir()
        assertTrue(dataDir.toFile().exists())
        assertTrue(dataDir.toFile().isDirectory)
    }

    @Test
    fun `getTmpDataDir returns non-null path`() {
        val tmpDir = getTmpDataDir()
        assertNotNull(tmpDir)
    }

    @Test
    fun `getTmpDataDir creates directory that exists`() {
        val tmpDir = getTmpDataDir()
        assertTrue(tmpDir.toFile().exists())
        assertTrue(tmpDir.toFile().isDirectory)
    }

    @Test
    fun `generateTmpWavFilePath returns path with wav extension`() {
        val wavPath = generateTmpWavFilePath()
        assertTrue(wavPath.toString().endsWith(".wav"))
    }

    @Test
    fun `generateTmpWavFilePath contains application short name`() {
        val wavPath = generateTmpWavFilePath()
        assertTrue(wavPath.toString().contains(APPLICATION_SHORT))
    }

    @Test
    fun `generateTmpWavFilePath returns different paths on multiple calls`() {
        val path1 = generateTmpWavFilePath()
        Thread.sleep(1) // Ensure different timestamps
        val path2 = generateTmpWavFilePath()
        assertNotEquals(path1, path2)
    }

    @Test
    fun `isValidUrl returns true for valid HTTP URL`() {
        assertTrue(isValidUrl("http://example.com"))
    }

    @Test
    fun `isValidUrl returns true for valid HTTPS URL`() {
        assertTrue(isValidUrl("https://example.com"))
    }

    @Test
    fun `isValidUrl returns true for URL with path`() {
        assertTrue(isValidUrl("https://example.com/path/to/resource"))
    }

    @Test
    fun `isValidUrl returns false for invalid URL`() {
        assertFalse(isValidUrl("not a url"))
    }

    @Test
    fun `isValidUrl returns false for empty string`() {
        assertFalse(isValidUrl(""))
    }

    @Test
    fun `isValidUrl returns false for malformed URL`() {
        assertFalse(isValidUrl("ht!tp://invalid"))
    }

    @Test
    fun `isValidUrl returns true for localhost URL with port`() {
        assertTrue(isValidUrl("http://localhost:8080"))
    }

    @Test
    fun `isValidUrl returns true for localhost URL with port and path`() {
        assertTrue(isValidUrl("http://localhost:8080/v1"))
    }

    @Test
    fun `isValidUrl returns true for HTTPS URL with port`() {
        assertTrue(isValidUrl("https://example.com:8443"))
    }

    @Test
    fun `isValidUrl returns true for URL with port and path`() {
        assertTrue(isValidUrl("https://api.example.com:443/v1"))
    }

    @Test
    fun `isValidUrl returns true for LM Studio URL`() {
        assertTrue(isValidUrl("http://localhost:1234/v1"))
    }

    @Test
    fun `isValidUrl returns true for Ollama URL`() {
        assertTrue(isValidUrl("http://localhost:11434/v1"))
    }

    @Test
    fun `isValidUrl returns true for vLLM URL in the local network`() {
        assertTrue(isValidUrl("http://myvllm:8000/v1"))
    }
}
