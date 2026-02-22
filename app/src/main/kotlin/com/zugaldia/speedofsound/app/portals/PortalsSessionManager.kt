package com.zugaldia.speedofsound.app.portals

import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun initialize(scope: CoroutineScope) {
        val token = settingsClient.getPortalsRestoreToken()
        if (token.isNotBlank()) {
            startSession(scope, token)
        } else {
            logger.info("No previous session found.")
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
