package com.zugaldia.speedofsound.app

import com.zugaldia.speedofsound.app.screens.main.MainScreen
import com.zugaldia.speedofsound.core.APPLICATION_ID
import org.apache.logging.log4j.LogManager
import org.gnome.gio.ApplicationFlags
import org.gnome.gio.SimpleAction
import org.gnome.gtk.Application

private val logger = LogManager.getLogger()

fun main(args: Array<String>) {
    val app = Application(APPLICATION_ID, ApplicationFlags.DEFAULT_FLAGS)
    registerTriggerAction(app)
    app.onActivate { MainScreen(app).present() }
    app.run(args)
}

/**
 * Registers the trigger action to handle D-Bus calls from scripts/trigger.sh
 */
private fun registerTriggerAction(app: Application) {
    val triggerAction = SimpleAction(TRIGGER_ACTION, null)
    triggerAction.onActivate { logger.info("Trigger action received via D-Bus") }
    app.addAction(triggerAction)
}
