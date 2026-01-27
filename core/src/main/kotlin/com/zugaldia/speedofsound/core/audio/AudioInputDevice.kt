package com.zugaldia.speedofsound.core.audio

/**
 * Represents an available audio input device (microphone).
 */
data class AudioInputDevice(
    val name: String,
    val vendor: String,
    val description: String,
    val version: String,
)
