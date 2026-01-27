package com.zugaldia.speedofsound.core

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.nio.file.Paths

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
    val isoDateTime = datetimeInSystemZone.toString()
    return isoDateTime
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
 * Generate a temporary WAV file path with timestamp.
 * Format: {tempDir}/speedofsound-{timestamp}.wav
 */
fun generateTempWavFilePath(): String {
    val tempDir = System.getProperty("java.io.tmpdir").takeUnless { it.isNullOrEmpty() } ?: "/tmp"
    val timestamp = generateTimestamp()
    return Paths.get(tempDir, "speedofsound-$timestamp.wav").toString()
}
