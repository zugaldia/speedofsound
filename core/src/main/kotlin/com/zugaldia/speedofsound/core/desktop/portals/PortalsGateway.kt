package com.zugaldia.speedofsound.core.desktop.portals

import com.zugaldia.stargate.sdk.globalshortcuts.BoundShortcut
import com.zugaldia.stargate.sdk.globalshortcuts.ShortcutActivation
import com.zugaldia.stargate.sdk.remotedesktop.StartResponse
import com.zugaldia.stargate.sdk.session.CreateSessionResponse
import com.zugaldia.stargate.sdk.session.SessionClosedEvent
import kotlinx.coroutines.flow.Flow

/**
 * The portal operations the session manager depends on.
 *
 * [PortalsClient] is the real implementation, which opens a D-Bus connection in its constructor. This
 * interface exists so the session lifecycle (start, restore on demand, release when idle) can be tested
 * without a desktop portal.
 */
interface PortalsGateway {
    val sessionClosedEvents: Flow<SessionClosedEvent>

    suspend fun registerApplication(): Result<Unit>

    suspend fun createGlobalShortcutsSession(): Result<CreateSessionResponse>

    suspend fun bindGlobalShortcuts(): Result<List<BoundShortcut>>

    fun observeShortcutActivated(): Flow<ShortcutActivation>

    suspend fun startRemoteDesktopSession(restoreToken: String?): Result<StartResponse>

    fun stopRemoteDesktopSession(): Result<Unit>
}
