package com.zugaldia.speedofsound.core

import com.zugaldia.stargate.sdk.isFlatpak
import com.zugaldia.stargate.sdk.isSnap
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val NANOSECONDS_PER_SECOND = 1_000_000_000L

/**
 * Generate a unique ID using UUID v4.
 */
@OptIn(ExperimentalUuidApi::class)
fun generateUniqueId(): String = Uuid.random().toString()

/**
 * Generate a timestamp using nanosecond precision since Unix epoch.
 */
@OptIn(ExperimentalTime::class)
fun generateTimestamp(): Long {
    val instant = Clock.System.now()
    return instant.epochSeconds * NANOSECONDS_PER_SECOND + instant.nanosecondsOfSecond
}

/**
 * Finds a Language by its ISO2 code.
 *
 * @param iso2 the two-letter ISO 639-1 language code
 * @return the matching Language, or null if not found
 */
fun languageFromIso2(iso2: String): Language? =
    Language.all.firstOrNull { it.iso2 == iso2 }

/**
 * Resolves and ensures an application directory exists, adapting to the runtime environment.
 *
 * @param xdgEnvVar the XDG environment variable to check (e.g. XDG_DATA_HOME, XDG_CACHE_HOME)
 * @param fallbackPath path segments under $HOME to use when no XDG variable is set (APPLICATION_SHORT is appended)
 * @param snapSubDir optional subdirectory to append under SNAP_USER_COMMON (e.g. "cache")
 */
private fun resolveAppDir(xdgEnvVar: String, fallbackPath: List<String>, snapSubDir: String? = null): Path {
    val snapUserCommon = System.getenv("SNAP_USER_COMMON") // In a Snap
    val xdgDir = System.getenv(xdgEnvVar) // Typically, in a Flatpak
    val appDir = if (!snapUserCommon.isNullOrEmpty()) {
        if (snapSubDir != null) Paths.get(snapUserCommon, snapSubDir) else Paths.get(snapUserCommon)
    } else if (!xdgDir.isNullOrEmpty()) {
        if (APPLICATION_ID in xdgDir) {
            Paths.get(xdgDir) // APPLICATION_ID already included in Flatpak sandboxes
        } else {
            Paths.get(xdgDir, APPLICATION_SHORT)
        }
    } else {
        val home = System.getProperty("user.home")
        Paths.get(home, *fallbackPath.toTypedArray(), APPLICATION_SHORT)
    }

    // Ensure it exists
    val dir = appDir.toFile()
    if (!dir.exists()) {
        dir.mkdirs()
    }

    return appDir
}

/**
 * Get the data directory path depending on the environment.
 * Returns $SNAP_USER_COMMON or $XDG_DATA_HOME (if set), falling back to $HOME/.local/share/speedofsound
 */
fun getDataDir(): Path = resolveAppDir("XDG_DATA_HOME", listOf(".local", "share"))

/**
 * Get the cache directory path depending on the environment.
 * Returns $SNAP_USER_COMMON/cache or $XDG_CACHE_HOME (if set), falling back to $HOME/.cache/speedofsound
 */
fun getCacheDir(): Path = resolveAppDir("XDG_CACHE_HOME", listOf(".cache"), snapSubDir = "cache")

/**
 * Generate a temporary WAV file path with a timestamp.
 * Format: {cacheDir}/speedofsound-{timestamp}.wav
 */
fun generateTmpWavFilePath(): Path {
    val cacheDir = getCacheDir()
    val timestamp = generateTimestamp()
    return cacheDir.resolve("$APPLICATION_SHORT-$timestamp.wav")
}

/**
 * Represents the runtime environment in which the application is running.
 */
enum class RuntimeEnvironment(val label: String) {
    FLATPAK("Flatpak"),
    SNAP("Snap"),
    APPIMAGE("AppImage"),
    JVM("JVM"),
}

/**
 * Detects the runtime environment (Flatpak, Snap, AppImage, or regular JVM Linux).
 */
fun getRuntimeEnvironment(): RuntimeEnvironment {
    // https://docs.appimage.org/packaging-guide/environment-variables.html
    val isAppImage = !System.getenv("APPIMAGE").isNullOrEmpty()
    return when {
        isSnap() -> RuntimeEnvironment.SNAP
        isFlatpak() -> RuntimeEnvironment.FLATPAK
        isAppImage -> RuntimeEnvironment.APPIMAGE
        else -> RuntimeEnvironment.JVM
    }
}

/**
 * Validates if a string is a valid URL.
 *
 * @param url the URL string to validate
 * @return true if the URL is valid, false otherwise
 */
fun isValidUrl(url: String): Boolean = runCatching {
    URI(url).toURL()
    true
}.getOrDefault(false)
