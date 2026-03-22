package com.zugaldia.speedofsound.app.portals

import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.stargate.sdk.globalshortcuts.ShortcutActivation
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.stargate.sdk.isSandboxed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class PortalsSessionManager(
    private val portalsClient: PortalsClient,
    private val settingsClient: SettingsClient,
    initialSessionDisconnected: Boolean = true,
    initialRestoreTokenMissing: Boolean = true,
) {
    private val logger = LoggerFactory.getLogger(PortalsSessionManager::class.java)

    private var portalsEventsJob: Job? = null

    private val _isSessionDisconnected = MutableStateFlow(initialSessionDisconnected)
    val isSessionDisconnected: StateFlow<Boolean> = _isSessionDisconnected.asStateFlow()

    private val _isRestoreTokenMissing = MutableStateFlow(initialRestoreTokenMissing)
    val isRestoreTokenMissing: StateFlow<Boolean> = _isRestoreTokenMissing.asStateFlow()

    private val _shortcutActivated = MutableSharedFlow<ShortcutActivation>(extraBufferCapacity = 1)
    val shortcutActivated: Flow<ShortcutActivation> = _shortcutActivated.asSharedFlow()

    fun initialize(scope: CoroutineScope) {
        scope.launch {
            // Registration must happen before any other portal call.
            // Otherwise, registration will result in an error.
            if (!isSandboxed()) {
                portalsClient.registerApplication()
            }

            // Attempt to create the Global Shortcuts session at startup on a best-effort basis.
            // Some desktop environments do not support this portal, failure is expected and non-fatal.
            portalsClient.createGlobalShortcutsSession()
                .onFailure { logger.warn("Global Shortcuts session creation failed: {}", it.message) }
                .onSuccess {
                    logger.info("Global Shortcuts session created successfully.")
                    // If the user previously configured a shortcut, rebind it silently on startup.
                    // (System UI is only shown to the user the first time.)
                    if (settingsClient.getShortcutConfigured()) {
                        portalsClient.bindGlobalShortcuts()
                            .onSuccess { logger.info("Global shortcut rebound successfully.") }
                            .onFailure { logger.warn("Failed to rebind global shortcut: {}", it.message) }
                    }
                    scope.launch {
                        portalsClient.observeShortcutActivated().collect { _shortcutActivated.emit(it) }
                    }
                }

            // We can still use portals even if registration above fails.
            // (Some portals might not work, e.g. Global Shortcuts, or have degraded experience though).
            val token = settingsClient.getPortalsRestoreToken()
            if (token.isNotBlank()) {
                startSession(scope, token)
            } else {
                logger.info("No previous session found.")
            }
        }
    }

    fun startSession(scope: CoroutineScope, token: String? = null) {
        scope.launch {
            val restoreToken = token?.ifBlank { null }
            logger.info(restoreToken?.let { "Trying to restore previous session: $it" } ?: "Starting a new session")
            portalsClient.startRemoteDesktopSession(restoreToken).onSuccess { response ->
                val newToken = response.restoreToken
                if (!newToken.isNullOrBlank()) {
                    logger.info("Got a fresh restore token: $newToken")
                    settingsClient.setPortalsRestoreToken(newToken)
                }
                collectPortalsEvents(scope)
                _isRestoreTokenMissing.value = newToken.isNullOrBlank()
                _isSessionDisconnected.value = false
            }.onFailure { error ->
                logger.error("Failed to start portals session", error)
                _isSessionDisconnected.value = true
                _isRestoreTokenMissing.value = true
                settingsClient.setPortalsRestoreToken("")
            }
        }
    }

    fun attemptReconnect(scope: CoroutineScope) {
        if (_isSessionDisconnected.value) {
            logger.info("Portal session disconnected, attempting to reconnect.")
            val restoreToken = settingsClient.getPortalsRestoreToken()
            startSession(scope, restoreToken.ifBlank { null })
        }
    }

    private fun collectPortalsEvents(scope: CoroutineScope) {
        portalsEventsJob?.cancel()
        portalsEventsJob = scope.launch {
            portalsClient.sessionClosedEvents.collect { event ->
                logger.warn("Remote desktop portal session has been closed: $event")
                _isSessionDisconnected.value = true
            }
        }
    }

    fun shutdown() {
        logger.info("Shutting down portals session manager")
        portalsEventsJob?.cancel()
        portalsEventsJob = null
    }
}
