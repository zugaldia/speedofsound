package com.zugaldia.speedofsound.core.plugins.asr

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.models.voice.DEFAULT_ASR_MODEL_ID

/**
 * Options for configuring the Whisper ASR plugin.
 */
data class WhisperOptions(
    val modelID: String = DEFAULT_ASR_MODEL_ID,
    val language: Language = DEFAULT_LANGUAGE,
    val enableDebug: Boolean = false,
) : AsrPluginOptions
