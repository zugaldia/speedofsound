package com.zugaldia.speedofsound.core.plugins.textoutput

import com.zugaldia.speedofsound.core.plugins.AppPlugin

/**
 * Base class for text output plugins.
 *
 * Provides a common interface for different text output mechanisms (e.g., keyboard simulation
 * via XDG Remote Desktop Portal, clipboard paste).
 */
abstract class TextOutputPlugin<Options : TextOutputPluginOptions>(initialOptions: Options) :
    AppPlugin<Options>(initialOptions) {

    /**
     * Outputs the given text to the active application.
     *
     * Each implementation decides how to deliver the text (e.g., simulating keystrokes,
     * pasting from clipboard).
     */
    abstract suspend fun outputText(request: TextOutputRequest): Result<Unit>
}
