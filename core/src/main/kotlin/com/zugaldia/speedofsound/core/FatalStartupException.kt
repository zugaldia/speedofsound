package com.zugaldia.speedofsound.core

/**
 * Thrown when the application encounters an unrecoverable error during startup.
 * The application should log the message and exit immediately without attempting recovery.
 */
open class FatalStartupException(message: String) : Exception(message)
