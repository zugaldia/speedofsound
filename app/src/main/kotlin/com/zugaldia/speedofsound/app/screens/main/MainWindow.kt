package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_WIDTH
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.apache.logging.log4j.LogManager
import org.gnome.gtk.Align
import org.gnome.adw.Application
import org.gnome.adw.ApplicationWindow
import org.gnome.gdk.Gdk
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.EventControllerKey
import org.gnome.gtk.Orientation

class MainWindow(app: Application): ApplicationWindow() {
    private val logger = LogManager.getLogger()
    private val viewModel = MainViewModel()

    init {
        application = app
        title = APPLICATION_NAME
        setDefaultSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT)
        content = buildContent()
        addController(EventControllerKey().apply {
            onKeyPressed { keyval, _, _ -> keyPressed(keyval) }
        })
    }

    private fun keyPressed(keyval: Int): Boolean {
        val key = Gdk.keyvalName(keyval)
        logger.info("Key pressed: $key")
        when (key) {
            "Escape" -> visible = false
        }

        return true
    }

    fun buildContent(): Box {
        val box = Box.builder()
            .setOrientation(Orientation.VERTICAL)
            .setHalign(Align.CENTER)
            .setValign(Align.CENTER)
            .setSpacing(DEFAULT_BOX_SPACING)
            .build()

        val startButton = Button.withLabel("Start Recording")
        startButton.onClicked { viewModel.startRecording() }

        val stopButton = Button.withLabel("Stop Recording")
        stopButton.onClicked { viewModel.stopRecording() }

        val exitButton = Button.withLabel("Exit")
        exitButton.onClicked {
            viewModel.shutdown()
            close()
        }

        box.append(startButton)
        box.append(stopButton)
        box.append(exitButton)
        return box
    }
}
