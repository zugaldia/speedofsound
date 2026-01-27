package com.zugaldia.speedofsound.core

/**
 * Converts a Language to a default locale string (e.g., "en-US").
 *
 * @throws UnsupportedOperationException if the language is not yet supported
 */
fun Language.toDefaultLocale(): String = when (this) {
    Language.ENGLISH -> "en-US"
    Language.SPANISH -> "es-ES"
    Language.FRENCH -> "fr-FR"
    Language.GERMAN -> "de-DE"
    Language.ITALIAN -> "it-IT"
    Language.JAPANESE -> "ja-JP"
    Language.KOREAN -> "ko-KR"
    Language.CHINESE -> "zh-CN"
    Language.HEBREW -> "he-IL"
    Language.DUTCH -> "nl-NL"
    Language.TURKISH -> "tr-TR"
    else -> throw UnsupportedOperationException("Language (${this.name}) is not yet supported for locale conversion")
}
