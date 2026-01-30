package com.zugaldia.speedofsound.core

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
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
 * Generate the local date and time in ISO format. E.g., 2026-01-12T16:13:01.337760845
 */
@OptIn(ExperimentalTime::class)
fun getIsoLocalDateTime(): String {
    val currentMoment: Instant = Clock.System.now()
    val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    return datetimeInSystemZone.toString()
}

/**
 * Compute the number of milliseconds between two instants.
 * Returns a positive value if [end] is after [start], negative otherwise.
 */
@OptIn(ExperimentalTime::class)
fun millisBetween(start: Instant, end: Instant): Long {
    return (end - start).inWholeMilliseconds
}

/**
 * Get the data directory path depending on the environment.
 * Returns $SNAP_USER_COMMON or $XDG_DATA_HOME (if set), falling back to $HOME/.local/share/speedofsound
 */
fun getDataDir(): Path {
    val snapUserCommon = System.getenv("SNAP_USER_COMMON") // In a Snap
    val xdgDataHome = System.getenv("XDG_DATA_HOME") // Typically, in a Flatpak
    val appDataDir = if (!snapUserCommon.isNullOrEmpty()) {
        Paths.get(snapUserCommon) // Already includes the Snap name
    } else if (!xdgDataHome.isNullOrEmpty()) {
        if (APPLICATION_ID in xdgDataHome) {
            Paths.get(xdgDataHome) // APPLICATION_ID already included in Flatpak sandboxes
        } else {
            Paths.get(xdgDataHome, APPLICATION_SHORT)
        }
    } else {
        val home = System.getProperty("user.home")
        Paths.get(home, ".local", "share", APPLICATION_SHORT)
    }

    // Ensure it exists
    val dir = appDataDir.toFile()
    if (!dir.exists()) {
        dir.mkdirs()
    }

    return appDataDir
}

/**
 * Get the temporary data directory path.
 * Returns {dataDir}/tmp, creating it if it doesn't exist.
 * This ensures temporary files work in sandboxed environments like Flatpak and Snap.
 */
fun getTmpDataDir(): Path {
    val dataDir = getDataDir()
    val tmpDir = dataDir.resolve("tmp")
    val dir = tmpDir.toFile()
    if (!dir.exists()) {
        dir.mkdirs()
    }

    return tmpDir
}

/**
 * Generate a temporary WAV file path with a timestamp.
 * Format: {tmpDataDir}/speedofsound-{timestamp}.wav
 */
fun generateTmpWavFilePath(): Path {
    val dataDir = getTmpDataDir()
    val timestamp = generateTimestamp()
    return dataDir.resolve("$APPLICATION_SHORT-$timestamp.wav")
}
