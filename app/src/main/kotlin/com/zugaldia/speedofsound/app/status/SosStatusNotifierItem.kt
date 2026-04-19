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

    // Flatpak exports icons to the host theme, so the app icon is available by APPLICATION_ID.
    // Snap does NOT export icons to the host, the extension cannot resolve names that only
    // exist inside the sandbox, resulting in the three-dots fallback. Use a standard FreeDesktop
    // icon instead until we implement IconPixmap (raw pixel data over D-Bus) or manually set
    // getIconThemePath to /snap/speedofsound/current/usr/share/icons/ (?).
    // See: https://github.com/ubuntu/gnome-shell-extension-appindicator/issues/232
    // See: https://github.com/ubuntu/gnome-shell-extension-appindicator/issues/544
    override fun getIconName(): String = when (getRuntimeEnvironment()) {
        RuntimeEnvironment.FLATPAK -> APPLICATION_ID
        else -> STATUS_NOTIFIER_ICON_FALLBACK
    }

    override fun getToolTipInfo(): Pair<String, String> =
        Pair(APPLICATION_NAME, "Voice typing for the Linux desktop")
}
