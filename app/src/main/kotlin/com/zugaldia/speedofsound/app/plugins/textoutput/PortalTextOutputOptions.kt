package com.zugaldia.speedofsound.app.plugins.textoutput

import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_SANITIZE_SPECIAL_CHARS
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_TYPING_DELAY_MS
import com.zugaldia.speedofsound.core.plugins.textoutput.TextOutputPluginOptions

/**
 * Options for the portal-based text output plugin.
 *
 * @param typingDelayMs Delay in milliseconds between consecutive keystrokes.
 * @param sanitizeSpecialChars Whether to replace accented characters with ASCII equivalents.
 */
data class PortalTextOutputOptions(
    val typingDelayMs: Long = DEFAULT_TYPING_DELAY_MS.toLong(),
    val sanitizeSpecialChars: Boolean = DEFAULT_SANITIZE_SPECIAL_CHARS,
) : TextOutputPluginOptions
