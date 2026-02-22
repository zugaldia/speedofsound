package com.zugaldia.speedofsound.app

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
