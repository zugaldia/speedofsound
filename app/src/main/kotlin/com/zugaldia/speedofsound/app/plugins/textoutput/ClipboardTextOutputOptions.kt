package com.zugaldia.speedofsound.app.plugins.textoutput

import com.zugaldia.speedofsound.core.plugins.textoutput.TextOutputPluginOptions

/**
 * Options for the clipboard-based text output plugin.
 *
 * @param pasteDelayMs Delay in milliseconds between setting the clipboard and simulating Ctrl+V.
 *  Allows time for the clipboard content to propagate before the paste shortcut is sent.
 */
data class ClipboardTextOutputOptions(
    val pasteDelayMs: Long = DEFAULT_PASTE_DELAY_MS,
) : TextOutputPluginOptions

private const val DEFAULT_PASTE_DELAY_MS = 50L
