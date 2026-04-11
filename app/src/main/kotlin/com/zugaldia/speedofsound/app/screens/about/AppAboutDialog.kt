package com.zugaldia.speedofsound.app.screens.about

import com.zugaldia.speedofsound.app.BuildConfig
import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.APPLICATION_URL
import com.zugaldia.speedofsound.core.RuntimeEnvironment
import com.zugaldia.speedofsound.core.getDataDir
import com.zugaldia.speedofsound.core.getRuntimeEnvironment
import com.zugaldia.speedofsound.core.getCacheDir
import org.gnome.adw.AboutDialog
import org.gnome.gtk.License

fun buildAboutDialog(): AboutDialog {
    val runtimeEnvironment = getRuntimeEnvironment()

    val dialog = AboutDialog()
    if (runtimeEnvironment == RuntimeEnvironment.FLATPAK || runtimeEnvironment == RuntimeEnvironment.SNAP) {
        dialog.applicationIcon = APPLICATION_ID
    }

    dialog.applicationName = APPLICATION_NAME
    dialog.developerName = "Antonio Zugaldia"
    dialog.version = "v${BuildConfig.VERSION} (${runtimeEnvironment.label})"
    dialog.website = APPLICATION_URL
    dialog.issueUrl = "https://github.com/zugaldia/speedofsound/issues"
    dialog.supportUrl = "https://github.com/zugaldia/speedofsound/issues"
    dialog.licenseType = License.MIT_X11
    dialog.copyright = "Copyright (c) 2025-2026 Antonio Zugaldia"
    dialog.debugInfo = buildDebugInfo()
    dialog.debugInfoFilename = "$APPLICATION_SHORT-debug.txt"
    dialog.addLink("Sponsor", "https://github.com/sponsors/zugaldia")
    dialog.addAcknowledgementSection(
        "Built on the Shoulders of Giants",
        arrayOf(
            "The Java-GI team https://github.com/jwharm/java-gi",
            "The Sherpa ONNX team https://github.com/k2-fsa/sherpa-onnx",
            "The Whisper team https://github.com/openai/whisper",
            "The dbus-java team https://github.com/hypfvieh/dbus-java",
        )
    )

    return dialog
}

private fun buildDebugInfo(): String = buildString {
    appendLine("$APPLICATION_NAME v${BuildConfig.VERSION}")
    appendLine()
    appendLine("Runtime: ${getRuntimeEnvironment().label}")
    appendLine("Data directory: ${getDataDir()}")
    appendLine("Cache directory: ${getCacheDir()}")
    appendLine()
    appendLine("OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")}")
    appendLine("Architecture: ${System.getProperty("os.arch")}")
    appendLine("Java: ${System.getProperty("java.version")} (${System.getProperty("java.vendor")})")
    appendLine("Kotlin: ${KotlinVersion.CURRENT}")
}
