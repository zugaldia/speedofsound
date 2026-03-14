package com.zugaldia.speedofsound.app

import org.gnome.adw.Adw
import org.gnome.adw.ColorScheme

/**
 * Returns true if the runtime libadwaita version is at least [major].[minor].[micro].
 */
fun isAdwVersionAtLeast(major: Int, minor: Int = 0, micro: Int = 0): Boolean {
    val runtimeMajor = Adw.getMajorVersion()
    val runtimeMinor = Adw.getMinorVersion()
    val runtimeMicro = Adw.getMicroVersion()
    return when {
        runtimeMajor != major -> runtimeMajor > major
        runtimeMinor != minor -> runtimeMinor > minor
        else -> runtimeMicro >= micro
    }
}

/**
 * Checks if the GIO store is disabled.
 */
fun isGioStoreDisabled(): Boolean {
    val envValue = System.getenv(ENV_DISABLE_GIO_STORE)
    return envValue?.equals("true", ignoreCase = true) ?: BuildConfig.DISABLE_GIO_STORE
}

/**
 * Checks if GStreamer is disabled.
 */
fun isGStreamerDisabled(): Boolean {
    val envValue = System.getenv(ENV_DISABLE_GSTREAMER)
    return envValue?.equals("true", ignoreCase = true) ?: BuildConfig.DISABLE_GSTREAMER
}

/**
 * Returns a color scheme override from the SOS_COLOR_SCHEME environment variable, or null if unset.
 * Accepted values: "light" (force light) or "dark" (force dark).
 */
fun getColorSchemeOverride(): ColorScheme? = when (System.getenv(ENV_COLOR_SCHEME)?.lowercase()) {
    "light" -> ColorScheme.FORCE_LIGHT
    "dark" -> ColorScheme.FORCE_DARK
    else -> null
}
