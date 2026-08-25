package com.zugaldia.speedofsound.app.status

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.stargate.sdk.status.StargateMenu
import com.zugaldia.stargate.sdk.status.StargateMenuItem
import com.zugaldia.stargate.sdk.status.StatusNotifierManager
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.gnome.glib.GLib
import org.slf4j.LoggerFactory

class StatusNotifierService(
    private val onTrigger: (token: String?) -> Unit,
    private val onOpen: (token: String?) -> Unit,
    private val onQuit: () -> Unit,
    private val isMonochrome: () -> Boolean = { false },
) : AutoCloseable {
    private val logger = LoggerFactory.getLogger(StatusNotifierService::class.java)

    private val job: Job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    private var manager: StatusNotifierManager? = null
    private var registrations = 0

    private fun onMenuClick(label: String, action: (token: String?) -> Unit): (tokenSupplier: () -> String?) -> Unit =
        { tokenSupplier ->
            GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                val token = tokenSupplier()
                logger.info("Menu: $label (token=$token)")
                action(token)
                false
            }
        }

    private fun buildMenu(): StargateMenu = StargateMenu(
        items = listOf(
            StargateMenuItem.Action(
                id = 1, label = "Start/Stop Listening",
                onClick = onMenuClick("Start/Stop Listening", onTrigger),
            ),
            StargateMenuItem.Action(
                id = 2, label = "Open App",
                onClick = onMenuClick("Open App", onOpen),
            ),
            StargateMenuItem.Separator(id = 3),
            StargateMenuItem.Action(
                id = 4, label = "Quit",
                onClick = onMenuClick("Quit") { onQuit() },
            ),
        ),
        onMenuOpened = { logger.info("Menu: opened") },
        onMenuClosed = { logger.info("Menu: closed") },
    )

    @Suppress("TooGenericExceptionCaught")
    fun connect() {
        scope.launch {
            try {
                manager?.close()
                // A private connection, not the shared session one: re-exporting the item on the shared
                // connection throws "Object already exported", and closing it would affect other users of
                // it. With a private connection the item can be torn down and rebuilt on a setting change.
                val connected = StatusNotifierManager(DBusConnectionBuilder.forSessionBus().withShared(false).build())
                manager = connected
                logger.info("Connected to StatusNotifierWatcher")

                val item = SosStatusNotifierItem(
                    monochrome = isMonochrome,
                    menu = buildMenu(),
                    onActivate = { token ->
                        GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                            logger.info("Activate (token=$token)")
                            onOpen(token)
                            false
                        }
                    },
                    onSecondaryActivate = { token ->
                        GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                            logger.info("SecondaryActivate (token=$token)")
                            onOpen(token)
                            false
                        }
                    },
                )

                // Use a dot-separated child of APPLICATION_ID so the bus name
                // (e.g. "io.speedofsound.SpeedOfSound.StatusNotifier-{pid}-1")
                // matches the Snap AppArmor policy for our D-Bus slot. Later registrations get a distinct
                // name, so a host that keys items by bus name sees a new item rather than the old one.
                registrations++
                val suffix = if (registrations == 1) "" else registrations.toString()
                val serviceName = connected.registerItem(item, "$APPLICATION_ID.StatusNotifier$suffix")
                logger.info("Registered StatusNotifierItem as $serviceName")
            } catch (e: Exception) {
                logger.error("Failed to connect to StatusNotifierWatcher", e)
            }
        }
    }

    /**
     * Applies a new status icon by tearing the item down and registering it again.
     *
     * The desktop reads the icon when the item registers. GNOME's AppIndicator extension ignores the
     * spec's NewIcon signal, so re-registering is the only way to change the icon without restarting.
     */
    fun refreshIcon() {
        logger.info("Re-registering the status notifier item to apply a new icon.")
        connect()
    }

    override fun close() {
        job.cancel()
        manager?.close()
        manager = null
    }
}
