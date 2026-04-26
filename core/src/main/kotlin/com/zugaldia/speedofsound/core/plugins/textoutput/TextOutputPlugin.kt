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
     * Prepares the text for output while the application window still has focus.
     *
     * This is called before the window is hidden, giving plugins a chance to perform
     * focus-dependent operations (e.g., setting the clipboard on Wayland, which requires
     * the calling window to be focused). The default implementation is a no-op.
     */
    open suspend fun prepareText(request: TextOutputRequest): Result<Unit> = Result.success(Unit)

    /**
     * Outputs the given text to the active application.
     *
     * Each implementation decides how to deliver the text (e.g., simulating keystrokes,
     * pasting from clipboard). Called after the window has been hidden and the target
     * application has regained focus.
     */
    abstract suspend fun outputText(request: TextOutputRequest): Result<Unit>
}
