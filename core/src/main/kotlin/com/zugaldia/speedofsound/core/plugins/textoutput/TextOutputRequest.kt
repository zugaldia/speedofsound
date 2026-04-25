package com.zugaldia.speedofsound.core.plugins.textoutput

/**
 * Request to output text to the active application.
 *
 * @param text The plain text to output.
 */
data class TextOutputRequest(
    val text: String,
)
