package com.zugaldia.speedofsound.app

import com.zugaldia.speedofsound.app.screens.main.MainViewModel
import com.zugaldia.speedofsound.app.screens.main.MainWindow
import com.zugaldia.speedofsound.app.screens.welcome.WelcomeWindow
import com.zugaldia.speedofsound.app.settings.GioStore
import com.zugaldia.speedofsound.core.desktop.settings.PropertiesStore
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.desktop.settings.SettingsStore
import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import org.gnome.adw.Adw
import org.gnome.adw.Application
import org.gnome.adw.StyleManager
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
            val adwVersion = "${Adw.getMajorVersion()}.${Adw.getMinorVersion()}.${Adw.getMicroVersion()}"
            logger.info("Application started with Adw v$adwVersion.")
            if (!isAdwVersionAtLeast(MIN_ADW_MAJOR_VERSION, MIN_ADW_MINOR_VERSION)) {
                logger.warn(
                    "Detected libadwaita v$adwVersion, but v$MIN_ADW_MAJOR_VERSION.$MIN_ADW_MINOR_VERSION " +
                        "or newer is required. The application might not work correctly."
                )
            }

            // Override the color scheme if SOS_COLOR_SCHEME is set. Users should leave this unset
            // and rely on system preferences instead. Intended for development and troubleshooting
            // to verify the app's appearance in both light and dark modes.
            getColorSchemeOverride()?.let { colorScheme ->
                StyleManager.getDefault().colorScheme = colorScheme
                logger.info("Color scheme override set to: $colorScheme")
            }

            settingsClient = SettingsClient(buildSettingsStore())
            portalsClient = PortalsClient()
            mainViewModel = MainViewModel(settingsClient, portalsClient)
            registerTriggerAction()
        }

        onActivate {
            logger.info("Application activated.")
            val isFirstLaunch = !settingsClient.getWelcomeScreenShown()
            ensureMainWindow()
            if (isFirstLaunch) {
                WelcomeWindow(this) {
                    settingsClient.setWelcomeScreenShown(true)
                    mainWindow?.present()
                    mainWindow?.openPreferences()
                }.present()
            } else {
                mainWindow?.present()
            }
        }

        onShutdown {
            logger.info("Application shutting down.")
            mainViewModel.shutdown()
        }
    }

    private fun buildSettingsStore(): SettingsStore {
        if (isGioStoreDisabled()) {
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

    private fun ensureMainWindow() {
        if (mainWindow == null) {
            mainWindow = MainWindow(this, mainViewModel, settingsClient)
        }
    }

    /**
     * Registers the trigger action to handle D-Bus calls from scripts/trigger.sh
     */
    private fun registerTriggerAction() {
        val triggerAction = SimpleAction(TRIGGER_ACTION, null)
        triggerAction.onActivate {
            ensureMainWindow()
            if (!settingsClient.getBackgroundRecording()) mainWindow?.present()
            mainWindow?.let { mainViewModel.onTriggerAction() }
        }

        addAction(triggerAction)
    }
}
