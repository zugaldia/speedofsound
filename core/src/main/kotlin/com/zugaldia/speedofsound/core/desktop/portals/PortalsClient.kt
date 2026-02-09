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

    suspend fun startRemoteDesktopSession(restoreToken: String?): Result<StartResponse> =
        portal.remoteDesktop.startSession(
            types = setOf(DeviceType.KEYBOARD),
            restoreToken = restoreToken,
            persistMode = PersistMode.UNTIL_REVOKED
        )

    suspend fun typeText(text: List<Int>, delayMs: Long = 10L) {
        logger.info("Typing ${text.size} characters.")
        for (key in text) {
            portal.remoteDesktop.notifyKeyboardKeySym(key, InputState.PRESSED)
            portal.remoteDesktop.notifyKeyboardKeySym(key, InputState.RELEASED)
            delay(delayMs)
        }
    }
}
