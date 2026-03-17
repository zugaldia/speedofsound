package com.zugaldia.speedofsound.core.desktop.portals

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.generateUniqueId
import com.zugaldia.stargate.sdk.DesktopPortal
import com.zugaldia.stargate.sdk.notification.NotificationPriority
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
     * Sends a desktop notification via the XDG Notification portal.
     *
     * @param body The main notification message shown to the user.
     * @param id Unique identifier for the notification. Defaults to a generated ID. Sending a new
     * notification with the same ID replaces any existing notification with that ID.
     * @param title The notification title. Defaults to the application name.
     * @param priority The notification priority level. Defaults to [NotificationPriority.NORMAL],
     * which is appropriate for most informational messages and errors.
     */
    fun showNotification(
        body: String,
        id: String = generateUniqueId(),
        title: String = APPLICATION_NAME,
        priority: NotificationPriority = NotificationPriority.NORMAL
    ) = runCatching { portal.notification.addNotification(id = id, title = title, body = body, priority = priority) }
        .onFailure { error -> logger.error("Failed to send notification: ${error.message}") }

    /**
     * Opens a URI in the user's preferred application via the XDG OpenURI portal.
     *
     * @param uri The URI to open (e.g. "https://example.com").
     */
    suspend fun openUri(uri: String) =
        portal.openUri.openUri(uri)
            .onFailure { error -> logger.error("Failed to open URI: ${error.message}") }

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
