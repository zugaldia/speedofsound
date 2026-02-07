package com.zugaldia.speedofsound.app

import com.zugaldia.speedofsound.app.screens.main.MainViewModel
import com.zugaldia.speedofsound.app.screens.main.MainWindow
import com.zugaldia.speedofsound.app.settings.GioStore
import com.zugaldia.speedofsound.core.desktop.settings.PropertiesStore
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.desktop.settings.SettingsStore
import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import org.gnome.adw.Adw
import org.gnome.adw.Application
import org.gnome.gio.ApplicationFlags
import org.gnome.gio.SimpleAction
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.zugaldia.speedofsound.app.App")

fun main(args: Array<String>) {
    logger.info("Running application.")
    SosApplication(APPLICATION_ID, setOf(ApplicationFlags.DEFAULT_FLAGS)).run(args)
}

class SosApplication(applicationId: String, flags: Set<ApplicationFlags>) : Application(applicationId, flags) {
    private var mainWindow: MainWindow? = null

    private lateinit var settingsClient: SettingsClient
    private lateinit var portalsClient: PortalsClient
    private lateinit var mainViewModel: MainViewModel

    init {
        onStartup {
            // We should update to a newer version of Adw once the new Ubuntu 26.04 LTS is out (and update
            // the Flatpak + Snap targets accordingly). There are some nice widgets we could use (e.g., WrapBox
            // in the preferences screen, ShortcutsDialog).
            val adwVersion = "${Adw.getMajorVersion()}.${Adw.getMinorVersion()}.${Adw.getMicroVersion()}"
            logger.info("Application started with Adw v$adwVersion.")
            if (Adw.getMajorVersion() < MIN_ADW_MAJOR_VERSION ||
                (Adw.getMajorVersion() == MIN_ADW_MAJOR_VERSION && Adw.getMinorVersion() < MIN_ADW_MINOR_VERSION)) {
                logger.warn(
                    "Detected libadwaita v$adwVersion, but v1.5 or newer is required. " +
                        "The application might not work correctly."
                )
            }

            settingsClient = SettingsClient(buildSettingsStore())
            portalsClient = PortalsClient()
            mainViewModel = MainViewModel(settingsClient, portalsClient)
            registerTriggerAction()
        }

        onActivate {
            logger.info("Application activated.")
            if (mainWindow == null) { mainWindow = MainWindow(this, mainViewModel, settingsClient) }
            mainWindow?.present()
        }

        onShutdown {
            logger.info("Application shutting down.")
            mainViewModel.shutdown()
        }
    }

    private fun buildSettingsStore(): SettingsStore {
        if (System.getenv(ENV_DISABLE_GIO_STORE)?.lowercase() == "true") {
            logger.info("GIO settings store disabled via $ENV_DISABLE_GIO_STORE, using properties store")
            return PropertiesStore()
        }

        val gioStore = GioStore()
        return if (gioStore.isAvailable()) {
            logger.info("Using GIO settings store")
            gioStore
        } else {
            logger.info("GIO settings not available, falling back to properties store")
            PropertiesStore()
        }
    }

    /**
     * Registers the trigger action to handle D-Bus calls from scripts/trigger.sh
     */
    private fun registerTriggerAction() {
        val triggerAction = SimpleAction(TRIGGER_ACTION, null)
        triggerAction.onActivate {
            activate() // Make sure the MainWindow is ready
            mainWindow?.let { mainViewModel.onTriggerAction() }
        }

        addAction(triggerAction)
    }
}
