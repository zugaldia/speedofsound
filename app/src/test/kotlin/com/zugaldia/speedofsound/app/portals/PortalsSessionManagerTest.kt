package com.zugaldia.speedofsound.app.portals

import com.zugaldia.speedofsound.core.desktop.portals.PortalsGateway
import com.zugaldia.speedofsound.core.desktop.settings.KEY_PORTALS_RESTORE_TOKEN
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.desktop.settings.SettingsStore
import com.zugaldia.stargate.sdk.globalshortcuts.BoundShortcut
import com.zugaldia.stargate.sdk.globalshortcuts.ShortcutActivation
import com.zugaldia.stargate.sdk.remotedesktop.StartResponse
import com.zugaldia.stargate.sdk.session.CreateSessionResponse
import com.zugaldia.stargate.sdk.session.SessionClosedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val AWAIT_TIMEOUT_SECONDS = 5L
private const val SETTLE_TIMEOUT_MS = 2_000L
private const val POLL_INTERVAL_MS = 10L
private const val RESTORE_TOKEN = "test-restore-token"

/**
 * Covers the session lifecycle: the app must hold a remote desktop session while dictating and release it
 * when idle, without ever ending up with two sessions (the desktop shows a screen sharing indicator for
 * each) or with none when a dictation is about to type.
 */
class PortalsSessionManagerTest {

    private class FakeStore(private val values: MutableMap<String, String> = mutableMapOf()) : SettingsStore {
        override fun isAvailable(): Boolean = true
        override fun getString(key: String, defaultValue: String): String = values[key] ?: defaultValue
        override fun setString(key: String, value: String): Boolean = true.also { values[key] = value }
        override fun getStringArray(key: String, defaultValue: List<String>): List<String> = defaultValue
        override fun setStringArray(key: String, value: List<String>): Boolean = true
        override fun getBoolean(key: String, defaultValue: Boolean): Boolean = defaultValue
        override fun setBoolean(key: String, value: Boolean): Boolean = true
        override fun getInt(key: String, defaultValue: Int): Int = defaultValue
        override fun setInt(key: String, value: Int): Boolean = true
    }

    /**
     * Records session starts and stops. [blockStopUntilReleased] holds a stop mid-flight, which is how the
     * "release still in progress" window is reproduced deterministically.
     */
    private class FakeGateway(private val blockStopUntilReleased: Boolean = false) : PortalsGateway {
        val starts = ConcurrentLinkedQueue<String?>()
        val stops = AtomicInteger(0)
        val stopEntered = CountDownLatch(1)
        private val stopMayFinish = CountDownLatch(1)

        override val sessionClosedEvents: Flow<SessionClosedEvent> = emptyFlow()
        override suspend fun registerApplication(): Result<Unit> = Result.success(Unit)
        override suspend fun createGlobalShortcutsSession(): Result<CreateSessionResponse> =
            Result.failure(UnsupportedOperationException("not used in these tests"))
        override suspend fun bindGlobalShortcuts(): Result<List<BoundShortcut>> = Result.success(emptyList())
        override fun observeShortcutActivated(): Flow<ShortcutActivation> = emptyFlow()

        override suspend fun startRemoteDesktopSession(restoreToken: String?): Result<StartResponse> {
            starts.add(restoreToken)
            return Result.success(
                StartResponse(devices = emptySet(), clipboardEnabled = false, restoreToken = RESTORE_TOKEN)
            )
        }

        override fun stopRemoteDesktopSession(): Result<Unit> {
            stops.incrementAndGet()
            stopEntered.countDown()
            if (blockStopUntilReleased) stopMayFinish.await(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            return Result.success(Unit)
        }

        fun letStopFinish() = stopMayFinish.countDown()
    }

    private fun managerWith(
        gateway: PortalsGateway,
        sessionDisconnected: Boolean,
    ): Pair<PortalsSessionManager, SettingsClient> {
        val settingsClient = SettingsClient(FakeStore(mutableMapOf(KEY_PORTALS_RESTORE_TOKEN to RESTORE_TOKEN)))
        val manager = PortalsSessionManager(
            portalsClient = gateway,
            settingsClient = settingsClient,
            initialSessionDisconnected = sessionDisconnected,
            initialRemoteDesktopStatus = RemoteDesktopStatus.Ready,
        )
        return manager to settingsClient
    }

    private fun waitUntil(description: String, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + SETTLE_TIMEOUT_MS
        while (System.currentTimeMillis() < deadline) {
            if (condition()) return
            Thread.sleep(POLL_INTERVAL_MS)
        }
        throw AssertionError("Timed out waiting for: $description")
    }

    @Test
    fun `ensureSession starts a session while a release is still in flight`() {
        val gateway = FakeGateway(blockStopUntilReleased = true)
        val (manager, _) = managerWith(gateway, sessionDisconnected = false)
        val scope = CoroutineScope(Dispatchers.Default)
        try {
            manager.releaseSession(scope)
            assertTrue(
                gateway.stopEntered.await(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "the release should have reached the portal call",
            )

            // The user re-triggers before the close has finished. The session must still be restored,
            // otherwise the dictation has nothing to type through.
            manager.ensureSession(scope)
            waitUntil("a session start to be requested") { gateway.starts.isNotEmpty() }
            assertEquals(1, gateway.starts.size)
        } finally {
            gateway.letStopFinish()
            scope.cancel()
        }
    }

    @Test
    fun `concurrent session starts open a single session`() {
        val gateway = FakeGateway()
        val (manager, _) = managerWith(gateway, sessionDisconnected = true)
        val scope = CoroutineScope(Dispatchers.Default)
        try {
            manager.ensureSession(scope)
            manager.ensureSession(scope)
            waitUntil("the first session start") { gateway.starts.isNotEmpty() }
            Thread.sleep(POLL_INTERVAL_MS * 5) // give a second start a chance to slip through
            assertEquals(1, gateway.starts.size, "a second session would light a second sharing indicator")
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun `releasing an already released session does not call the portal again`() {
        val gateway = FakeGateway()
        val (manager, _) = managerWith(gateway, sessionDisconnected = true)
        val scope = CoroutineScope(Dispatchers.Default)
        try {
            manager.releaseSession(scope)
            Thread.sleep(POLL_INTERVAL_MS * 5)
            assertEquals(0, gateway.stops.get())
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun `a released session is restored on the next dictation`() {
        val gateway = FakeGateway()
        val (manager, _) = managerWith(gateway, sessionDisconnected = false)
        val scope = CoroutineScope(Dispatchers.Default)
        try {
            manager.releaseSession(scope)
            waitUntil("the session to be closed") { gateway.stops.get() == 1 }
            manager.ensureSession(scope)
            waitUntil("the session to be restored") { gateway.starts.isNotEmpty() }
            assertEquals(listOf(RESTORE_TOKEN), gateway.starts.toList(), "restore must reuse the stored token")
        } finally {
            scope.cancel()
        }
    }
}
