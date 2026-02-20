package com.zugaldia.speedofsound.core.audio

/**
 * Represents an available audio input device (microphone).
 *
 * @property deviceId Technical identifier used to select this device (e.g., ALSA device path)
 * @property name User-friendly display name
 * @property vendor Vendor/manufacturer name (optional)
 * @property description Device description (optional)
 * @property version Device version (optional)
 */
data class AudioInputDevice(
    val deviceId: String,
    val name: String,
    val vendor: String? = null,
    val description: String? = null,
    val version: String? = null,
)
