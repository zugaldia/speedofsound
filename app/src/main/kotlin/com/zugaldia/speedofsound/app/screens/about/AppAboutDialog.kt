package com.zugaldia.speedofsound.app.screens.about

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.AboutDialog
import org.gnome.gtk.License

fun buildAboutDialog(): AboutDialog {
    val dialog = AboutDialog()
    dialog.applicationName = APPLICATION_NAME
    dialog.developerName = "Antonio Zugaldia"
    dialog.version = "0.1.0"
    dialog.website = "https://github.com/zugaldia/speedofsound"
    dialog.issueUrl = "https://github.com/zugaldia/speedofsound/issues"
    dialog.licenseType = License.MIT_X11
    dialog.copyright = "Copyright (c) 2025-2026 Antonio Zugaldia"
    dialog.addAcknowledgementSection(
        "Special Thanks",
        arrayOf(
            "David M. (@hypfvieh) and the Java D-Bus team.",
            "Jan-Willem Harmannij (@jwharm) and the Java GI team."
        )
    )

    return dialog
}
