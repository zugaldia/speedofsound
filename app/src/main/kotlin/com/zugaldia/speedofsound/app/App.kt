package com.zugaldia.speedofsound.app

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.gio.ApplicationFlags
import org.gnome.gtk.Align
import org.gnome.gtk.Application
import org.gnome.gtk.ApplicationWindow
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Orientation

private const val DEFAULT_WINDOW_WIDTH = 300
private const val DEFAULT_WINDOW_HEIGHT = 200

fun main(args: Array<String>) {
    val app = Application(APPLICATION_ID, ApplicationFlags.DEFAULT_FLAGS)
    app.onActivate { activate(app) }
    app.run(args)
}

private fun activate(app: Application) {
    val window = ApplicationWindow(app)
    window.title = APPLICATION_NAME
    window.setDefaultSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT)

    val box = Box.builder()
        .setOrientation(Orientation.VERTICAL)
        .setHalign(Align.CENTER)
        .setValign(Align.CENTER)
        .build()

    val button = Button.withLabel("Exit")
    button.onClicked(window::close)

    box.append(button)
    window.child = box
    window.present()
}
