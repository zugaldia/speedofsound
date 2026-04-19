package com.zugaldia.speedofsound.app.status

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.RuntimeEnvironment
import com.zugaldia.speedofsound.core.getRuntimeEnvironment
import com.zugaldia.stargate.sdk.status.StargateMenu
import com.zugaldia.stargate.sdk.status.StargateStatusNotifierItem

// Standard FreeDesktop icon name, available on any compliant desktop.
// Used as fallback when the app icon isn't in the system theme.
// https://specifications.freedesktop.org/icon-naming/latest/
private const val STATUS_NOTIFIER_ICON_FALLBACK = "audio-input-microphone"

class SosStatusNotifierItem(
    menu: StargateMenu,
    onActivate: (token: String?) -> Unit,
    onSecondaryActivate: (token: String?) -> Unit,
) : StargateStatusNotifierItem(menu, onActivate, onSecondaryActivate) {
    override fun getId(): String = APPLICATION_ID
    override fun getTitle(): String = APPLICATION_NAME

    // In Flatpak and Snap the app icon is registered in the system theme under APPLICATION_ID.
    // In other environments (app-image, JAR) it isn't, so fall back to the standard
    // FreeDesktop microphone icon which is always available.
    override fun getIconName(): String = when (getRuntimeEnvironment()) {
        RuntimeEnvironment.FLATPAK, RuntimeEnvironment.SNAP -> APPLICATION_ID
        else -> STATUS_NOTIFIER_ICON_FALLBACK
    }

    // In Flatpak and Snap the icons are inside the sandbox (e.g. $SNAP/usr/share/icons)
    // which is not on the host's default icon theme search path. Provide the path so
    // the tray host can locate our icon.
    override fun getIconThemePath(): String = when (getRuntimeEnvironment()) {
        RuntimeEnvironment.SNAP -> "${System.getenv("SNAP") ?: ""}/usr/share/icons"
        RuntimeEnvironment.FLATPAK -> "/app/share/icons"
        else -> ""
    }

    override fun getToolTipInfo(): Pair<String, String> =
        Pair(APPLICATION_NAME, "Voice typing for the Linux desktop")
}
