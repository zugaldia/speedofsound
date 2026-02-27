package com.zugaldia.speedofsound.core.plugins.director

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE

// Sherpa ONNX offline Whisper recognizer has a hard 30-second limit, longer audio is truncated
const val DEFAULT_MAX_RECORDING_DURATION_MS = 30_000L

// Default timeout for LLM polishing operations to ensure a quick response
const val DEFAULT_LLM_TIMEOUT_MS = 5_000L

const val PROMPT_KEY_INPUT = "{INPUT}"
const val PROMPT_KEY_LANGUAGE = "{LANGUAGE}"
const val PROMPT_KEY_CONTEXT = "{CONTEXT}"
const val PROMPT_KEY_VOCABULARY = "{VOCABULARY}"

val DEFAULT_VOCABULARY = listOf(APPLICATION_NAME, "Linux", "GNOME")
const val DEFAULT_CONTEXT =
    "This is the raw transcription text captured from a voice dictation application on a Linux desktop."

const val PROMPT_TEMPLATE = """# Goal
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

## Examples

INPUT: what time is it
OUTPUT: What time is it?

INPUT: well update to the the L T S version. of the linux distribution right away.
OUTPUT: We'll update to the LTS version of the Linux distribution right away.

INPUT: Open the mail app send an email to John at example dot com and close it
OUTPUT: Open the mail app, send an email to john@example.com, and close it.

# Transcription
LANGUAGE: $PROMPT_KEY_LANGUAGE
INPUT: $PROMPT_KEY_INPUT
OUTPUT: """

/**
 * Options for the director plugin.
 */
data class DirectorOptions(
    val enableTextProcessing: Boolean = false,
    val language: Language = DEFAULT_LANGUAGE,
    val customContext: String = DEFAULT_CONTEXT,
    val customVocabulary: List<String> = DEFAULT_VOCABULARY,
    val maxRecordingDurationMs: Long = DEFAULT_MAX_RECORDING_DURATION_MS,
    val llmTimeoutMs: Long = DEFAULT_LLM_TIMEOUT_MS,
) : DirectorPluginOptions
