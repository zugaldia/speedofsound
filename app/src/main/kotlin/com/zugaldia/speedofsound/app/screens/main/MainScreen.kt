package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_WINDOW_WIDTH
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.gtk.Align
import org.gnome.gtk.Application
import org.gnome.gtk.ApplicationWindow
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Orientation

class MainScreen(private val app: Application) {
    private val viewModel = MainViewModel()

    fun present() {
        val window = ApplicationWindow(app)
        window.title = APPLICATION_NAME
        window.setDefaultSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT)

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
            window.close()
        }

        box.append(startButton)
        box.append(stopButton)
        box.append(exitButton)
        window.child = box
        window.present()
    }
}
