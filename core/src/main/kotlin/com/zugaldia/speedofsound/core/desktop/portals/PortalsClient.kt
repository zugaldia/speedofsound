package com.zugaldia.speedofsound.core.desktop.portals

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.APPLICATION_SHORTCUT_TRIGGER
import com.zugaldia.speedofsound.core.generateUniqueId
import com.zugaldia.stargate.sdk.DesktopPortal
import com.zugaldia.stargate.sdk.globalshortcuts.BoundShortcut
import com.zugaldia.stargate.sdk.globalshortcuts.Shortcut
import com.zugaldia.stargate.sdk.globalshortcuts.ShortcutActivation
import com.zugaldia.stargate.sdk.notification.NotificationPriority
import com.zugaldia.stargate.sdk.remotedesktop.DeviceType
import com.zugaldia.stargate.sdk.remotedesktop.InputState
import com.zugaldia.stargate.sdk.remotedesktop.PersistMode
import com.zugaldia.stargate.sdk.remotedesktop.StartResponse
import com.zugaldia.stargate.sdk.session.CreateSessionResponse
import com.zugaldia.stargate.sdk.session.SessionClosedEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import org.freedesktop.dbus.DBusPath
import org.slf4j.LoggerFactory

private const val SHORTCUT_ID = "$APPLICATION_SHORT-trigger"
private const val SHORTCUT_DESCRIPTION = "Start or stop voice typing"

class PortalsClient {

    private val logger = LoggerFactory.getLogger(PortalsClient::class.java)
    private val portal = DesktopPortal.connect()
    private var shortcutsSessionHandle: DBusPath? = null

    val sessionClosedEvents: Flow<SessionClosedEvent>
        get() = portal.remoteDesktop.observeSessionClosed()

    /**
     * Registers the application with the XDG Registry portal.
     * This should only be called when not running in a sandboxed environment (Flatpak/Snap),
     * as sandboxed apps are registered automatically.
     */
    suspend fun registerApplication() =
        portal.registry.register(APPLICATION_ID)
            .onSuccess { logger.info("Registered application ID: {}", APPLICATION_ID) }
            .onFailure { logger.warn("Failed to register application ID: {} ({})", APPLICATION_ID, it.message) }

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
     * Creates a global shortcuts session via the XDG Global Shortcuts portal.
     *
     * This is idempotent — if a session already exists, it returns success without creating a new one.
     * This is expected to fail on older desktop environments that do not support the portal.
     */
    suspend fun createGlobalShortcutsSession(): Result<CreateSessionResponse> {
        val existingHandle = shortcutsSessionHandle
        if (existingHandle != null) {
            logger.info("Global shortcuts session already exists, skipping creation.")
            return Result.success(CreateSessionResponse(existingHandle))
        }

        return portal.globalShortcuts.createSession()
            .onSuccess { response -> shortcutsSessionHandle = response.sessionHandle }
    }

    /**
     * Returns a Flow that emits a [ShortcutActivation] each time the global shortcut is activated.
     *
     * Filters activations to only the application's shortcut ID and only when activated (not released).
     */
    fun observeShortcutActivated(): Flow<ShortcutActivation> =
        portal.globalShortcuts.activations()
            .filter { it.shortcutId == SHORTCUT_ID && it.activated }

    /**
     * Returns the version of the Global Shortcuts portal, or 0 if unavailable.
     *
     * Version 2 or higher indicates support for [configureGlobalShortcuts].
     */
    val globalShortcutsVersion: Int
        get() = runCatching { portal.globalShortcuts.version }.getOrDefault(0)

    /**
     * Opens the system dialog for configuring the application's global shortcuts.
     *
     * Only available on portal version 2 or higher. Check [globalShortcutsVersion] before calling.
     */
    fun configureGlobalShortcuts(): Result<Unit> = portal.globalShortcuts.configureShortcuts()

    /**
     * Lists all shortcuts currently bound to the Global Shortcuts session.
     */
    suspend fun listGlobalShortcuts(): Result<List<BoundShortcut>> {
        val handle = shortcutsSessionHandle
            ?: return Result.failure(IllegalStateException("No active global shortcuts session"))
        return portal.globalShortcuts.listShortcuts(handle)
    }

    /**
     * Binds the application's global shortcut to the active session.
     */
    suspend fun bindGlobalShortcuts(): Result<List<BoundShortcut>> {
        val handle = shortcutsSessionHandle
            ?: return Result.failure(IllegalStateException("No active global shortcuts session"))
        val shortcut = Shortcut(
            id = SHORTCUT_ID,
            description = SHORTCUT_DESCRIPTION,
            preferredTrigger = APPLICATION_SHORTCUT_TRIGGER
        )
        return portal.globalShortcuts.bindShortcuts(handle, listOf(shortcut))
    }

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
