package com.zugaldia.speedofsound.app.plugins.textoutput

import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.speedofsound.core.plugins.textoutput.TextOutputPlugin
import com.zugaldia.speedofsound.core.plugins.textoutput.TextOutputRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import org.gnome.gdk.Display
import org.gnome.glib.GLib
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Text output plugin that copies text to the system clipboard and simulates Ctrl+V to paste it.
 *
 * This is an alternative to the portal-based keyboard simulation that works better with
 * applications that have trouble receiving individual key symbol events (e.g., certain Electron apps)
 * or when typing large blocks of text where character-by-character simulation is too slow.
 */
class ClipboardTextOutput(
    private val portalsClient: PortalsClient,
    options: ClipboardTextOutputOptions = ClipboardTextOutputOptions,
) : TextOutputPlugin<ClipboardTextOutputOptions>(options) {
    override val id: String = ID

    companion object {
        const val ID = "TEXT_OUTPUT_CLIPBOARD"
        private const val KEY_SYMBOL_CONTROL_L = 0xFFE3
        private const val KEY_SYMBOL_V = 0x0076
    }

    override suspend fun prepareText(request: TextOutputRequest): Result<Unit> = runCatching {
        setClipboardText(request.text)
    }

    override suspend fun outputText(request: TextOutputRequest): Result<Unit> = runCatching {
        portalsClient.typeKeyCombination(KEY_SYMBOL_CONTROL_L, KEY_SYMBOL_V).getOrThrow()
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun setClipboardText(text: String) = suspendCancellableCoroutine { cont ->
        GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
            try {
                val display = Display.getDefault() ?: throw IllegalStateException("No GDK display available")
                display.getClipboard().setText(text)
                cont.resume(Unit)
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
            false
        }
    }
}
