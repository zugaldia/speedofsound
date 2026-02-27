package com.zugaldia.speedofsound.app.screens.about

import com.zugaldia.speedofsound.app.BuildConfig
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.AboutDialog
import org.gnome.gtk.License

fun buildAboutDialog(): AboutDialog {
    val dialog = AboutDialog()
    dialog.applicationName = APPLICATION_NAME
    dialog.developerName = "Antonio Zugaldia"
    dialog.version = BuildConfig.VERSION
    dialog.website = "https://github.com/zugaldia/speedofsound"
    dialog.issueUrl = "https://github.com/zugaldia/speedofsound/issues"
    dialog.licenseType = License.MIT_X11
    dialog.copyright = "Copyright (c) 2025-2026 Antonio Zugaldia"
    dialog.addAcknowledgementSection(
        "Built on the Shoulders of Giants",
        arrayOf(
            "The Java-GI team (https://github.com/jwharm/java-gi)",
            "The Sherpa ONNX team (https://github.com/k2-fsa/sherpa-onnx)",
            "The Whisper team (https://github.com/openai/whisper)",
            "The dbus-java team (https://github.com/hypfvieh/dbus-java)",
        )
    )

    return dialog
}
