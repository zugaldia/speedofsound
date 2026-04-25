package com.zugaldia.speedofsound.app.plugins.textoutput

import com.zugaldia.speedofsound.app.portals.TextUtils
import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.speedofsound.core.plugins.textoutput.TextOutputPlugin
import com.zugaldia.speedofsound.core.plugins.textoutput.TextOutputRequest

/**
 * Text output plugin that simulates keyboard input via the XDG Remote Desktop Portal.
 *
 * Converts text to GDK key symbol values and sends each character as a key press/release pair
 * through the portal's remote desktop interface.
 */
class PortalTextOutput(
    private val portalsClient: PortalsClient,
    options: PortalTextOutputOptions = PortalTextOutputOptions(),
) : TextOutputPlugin<PortalTextOutputOptions>(options) {
    override val id: String = ID

    companion object {
        const val ID = "TEXT_OUTPUT_PORTAL"
    }

    override suspend fun outputText(request: TextOutputRequest): Result<Unit> {
        return TextUtils.textToKeySymbols(
            text = request.text,
            filterNoKeySymbol = false,
            sanitize = currentOptions.sanitizeSpecialChars,
        ).mapCatching { keySymbols ->
            portalsClient.typeText(keySymbols, currentOptions.typingDelayMs).getOrThrow()
        }
    }
}
