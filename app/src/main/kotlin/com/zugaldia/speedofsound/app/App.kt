package com.zugaldia.speedofsound.app

import com.zugaldia.speedofsound.app.screens.main.MainScreen
import com.zugaldia.speedofsound.core.APPLICATION_ID
import org.gnome.gio.ApplicationFlags
import org.gnome.gtk.Application

fun main(args: Array<String>) {
    val app = Application(APPLICATION_ID, ApplicationFlags.DEFAULT_FLAGS)
    app.onActivate { MainScreen(app).present() }
    app.run(args)
}
