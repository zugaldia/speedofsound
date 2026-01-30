package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.DEFAULT_ASR_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.AppPluginOptions

/**
 * Options for configuring the Whisper ASR plugin.
 */
data class WhisperOptions(
    val modelID: String = DEFAULT_ASR_MODEL_ID,
    val language: Language = Language.ENGLISH,
) : AppPluginOptions
