package com.zugaldia.speedofsound.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LanguageTest {

    @Test
    fun `data class creates language with all properties`() {
        val language = Language("English", "en", "eng")
        assertEquals("English", language.name)
        assertEquals("en", language.iso2)
        assertEquals("eng", language.iso3)
    }

    @Test
    fun `companion object contains expected language constants`() {
        // Use the six official UN languages as sample
        assertEquals(Language("Arabic", "ar", "ara"), Language.ARABIC)
        assertEquals(Language("Chinese", "zh", "chi"), Language.CHINESE)
        assertEquals(Language("English", "en", "eng"), Language.ENGLISH)
        assertEquals(Language("French", "fr", "fre"), Language.FRENCH)
        assertEquals(Language("Russian", "ru", "rus"), Language.RUSSIAN)
        assertEquals(Language("Spanish", "es", "spa"), Language.SPANISH)
    }

    @Test
    fun `all list contains expected number of languages`() {
        // Based on the ISO-639 standard, we have 183 languages defined
        // https://www.loc.gov/standards/iso639-2/ISO-639-2_utf-8.txt
        assertEquals(183, Language.all.size)
    }

    @Test
    fun `all list contains sample languages`() {
        // Verify the six official UN languages are in the list
        assertTrue(Language.all.contains(Language.ARABIC))
        assertTrue(Language.all.contains(Language.CHINESE))
        assertTrue(Language.all.contains(Language.ENGLISH))
        assertTrue(Language.all.contains(Language.FRENCH))
        assertTrue(Language.all.contains(Language.RUSSIAN))
        assertTrue(Language.all.contains(Language.SPANISH))
    }

    @Test
    fun `iso2 codes are two characters`() {
        Language.all.forEach { language ->
            assertEquals(2, language.iso2.length, "Language ${language.name} has invalid ISO2 code")
        }
    }

    @Test
    fun `iso3 codes are three characters`() {
        Language.all.forEach { language ->
            assertEquals(3, language.iso3.length, "Language ${language.name} has invalid ISO3 code")
        }
    }

    @Test
    fun `language names are not blank`() {
        Language.all.forEach { language ->
            assertTrue(language.name.isNotBlank(), "Language has blank name")
        }
    }

    @Test
    fun `all list starts with AFAR and ends with ZULU`() {
        assertEquals(Language.AFAR, Language.all.first())
        assertEquals(Language.ZULU, Language.all.last())
    }
}
