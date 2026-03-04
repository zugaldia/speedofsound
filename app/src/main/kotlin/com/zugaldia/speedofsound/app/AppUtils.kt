package com.zugaldia.speedofsound.app

import org.gnome.adw.ColorScheme

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
