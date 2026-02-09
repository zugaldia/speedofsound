package com.zugaldia.speedofsound.core.plugins.director

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE

const val PROMPT_KEY_INPUT = "{INPUT}"
const val PROMPT_KEY_LANGUAGE = "{LANGUAGE}"
const val PROMPT_KEY_CONTEXT = "{CONTEXT}"
const val PROMPT_KEY_VOCABULARY = "{VOCABULARY}"

val DEFAULT_VOCABULARY = listOf(APPLICATION_NAME, "Linux", "GNOME")
const val DEFAULT_CONTEXT =
    "This is a voice-to-text transcription captured from a dictation application on a Linux desktop."

val PROMPT_TEMPLATE = """
# Goal
You are an expert copy editor.
Your goal is to review the following INPUT transcription to improve its quality and readability.

## Instructions
- Fix any grammar and spelling errors.
- Add proper capitalization.
- Add any necessary punctuation such as periods and commas.
- Keep punctuation simple: avoid dashes and semicolons.

## Context
$PROMPT_KEY_CONTEXT

## Vocabulary
Make sure that the following terms are spelled correctly: $PROMPT_KEY_VOCABULARY.

## OUTPUT format
- Return only the corrected transcription.
- Do not add commentary, answers, or timestamps.
- Preserve the same language as the INPUT.

# Transcription
LANGUAGE: $PROMPT_KEY_LANGUAGE
INPUT: $PROMPT_KEY_INPUT
OUTPUT:
""".trimIndent()

/**
 * Options for the director plugin.
 */
data class DirectorOptions(
    val language: Language = DEFAULT_LANGUAGE,
    val customContext: String = DEFAULT_CONTEXT,
    val customVocabulary: List<String> = DEFAULT_VOCABULARY,
) : DirectorPluginOptions
