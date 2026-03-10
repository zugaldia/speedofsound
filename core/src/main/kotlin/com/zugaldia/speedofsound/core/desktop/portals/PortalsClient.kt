package com.zugaldia.speedofsound.core.desktop.portals

import com.zugaldia.stargate.sdk.DesktopPortal
import com.zugaldia.stargate.sdk.remotedesktop.DeviceType
import com.zugaldia.stargate.sdk.remotedesktop.InputState
import com.zugaldia.stargate.sdk.remotedesktop.PersistMode
import com.zugaldia.stargate.sdk.remotedesktop.StartResponse
import com.zugaldia.stargate.sdk.session.SessionClosedEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory

class PortalsClient {
    private val logger = LoggerFactory.getLogger(PortalsClient::class.java)
    private val portal = DesktopPortal.connect()

    val sessionClosedEvents: Flow<SessionClosedEvent>
        get() = portal.remoteDesktop.observeSessionClosed()

    /**
     * Starts a remote desktop session with keyboard input support.
     *
     * @param restoreToken Optional token from a previous session. When provided, the portal will
     * attempt to restore the session without prompting the user for authorization again.
     */
    suspend fun startRemoteDesktopSession(restoreToken: String?): Result<StartResponse> =
        portal.remoteDesktop.startSession(
            types = setOf(DeviceType.KEYBOARD),
            restoreToken = restoreToken,
            persistMode = PersistMode.UNTIL_REVOKED
        )

    /**
     * Simulates keyboard input by sending each character as a key press/release pair.
     *
     * @param text List of X11 keysym values to type, one per character.
     * @param delayMs Delay in milliseconds between consecutive keystrokes. Set to 0 to disable.
     * Increase this if characters are dropped or appear out of order (or like a slower typing effect).
     */
    suspend fun typeText(text: List<Int>, delayMs: Long) {
        logger.info("Typing ${text.size} characters.")
        for (key in text) {
            portal.remoteDesktop.notifyKeyboardKeySym(key, InputState.PRESSED)
            portal.remoteDesktop.notifyKeyboardKeySym(key, InputState.RELEASED)
            if (delayMs > 0) delay(delayMs)
        }
    }
}
