package com.zugaldia.speedofsound.app.status

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.RuntimeEnvironment

// Standard FreeDesktop icon names, available on any compliant desktop.
// Used as fallback when the app icons aren't in the system theme.
// https://specifications.freedesktop.org/icon-naming/latest/
const val STATUS_ICON_FALLBACK = "audio-input-microphone"
const val STATUS_ICON_FALLBACK_SYMBOLIC = "audio-input-microphone-symbolic"

/**
 * Picks the icon name the status notifier item reports to the desktop.
 *
 * Flatpak exports the app icons to the host theme, so they can be requested by name. Snap does NOT export
 * them, and the shell extension cannot resolve names that only exist inside the sandbox (it shows the
 * three-dots placeholder instead), so those environments get the standard FreeDesktop icons.
 * See: https://github.com/ubuntu/gnome-shell-extension-appindicator/issues/232
 *
 * @param monochrome whether the user asked for the symbolic (monochrome) variant, which the desktop
 * recolors to match its panel.
 */
fun statusIconName(monochrome: Boolean, environment: RuntimeEnvironment): String = when {
    monochrome && environment == RuntimeEnvironment.FLATPAK -> "$APPLICATION_ID-symbolic"
    monochrome -> STATUS_ICON_FALLBACK_SYMBOLIC
    environment == RuntimeEnvironment.FLATPAK -> APPLICATION_ID
    else -> STATUS_ICON_FALLBACK
}
