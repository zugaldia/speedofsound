package com.zugaldia.speedofsound.app

import com.zugaldia.speedofsound.app.screens.main.MainWindow
import com.zugaldia.speedofsound.core.APPLICATION_ID
import org.apache.logging.log4j.LogManager
import org.gnome.adw.Application
import org.gnome.gio.ApplicationFlags
import org.gnome.gio.SimpleAction

private val logger = LogManager.getLogger()

fun main(args: Array<String>) {
    logger.info("Running application.")
    SosApplication(APPLICATION_ID, setOf(ApplicationFlags.DEFAULT_FLAGS)).run(args)
}

class SosApplication(applicationId: String, flags: Set<ApplicationFlags>) : Application(applicationId, flags) {
    private lateinit var mainWindow: MainWindow

    init {
        onStartup {
            logger.info("Application started.")
            registerTriggerAction()
        }

        onActivate {
            logger.info("Application activated.")
            mainWindow = MainWindow(this)
            mainWindow.present()
        }

        onShutdown {
            logger.info("Application shutting down.")
        }
    }

    /**
     * Registers the trigger action to handle D-Bus calls from scripts/trigger.sh
     */
    private fun registerTriggerAction() {
        val triggerAction = SimpleAction(TRIGGER_ACTION, null)
        triggerAction.onActivate { onTriggerAction() }
        addAction(triggerAction)
    }

    private fun onTriggerAction() {
        logger.info("Trigger action received via CLI")
        mainWindow.visible = true
    }
}
