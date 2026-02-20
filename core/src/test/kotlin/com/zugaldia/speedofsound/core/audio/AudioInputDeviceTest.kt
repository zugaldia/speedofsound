package com.zugaldia.speedofsound.core.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class AudioInputDeviceTest {

    @Test
    fun `construction with required fields only`() {
        val device = AudioInputDevice(
            deviceId = "hw:0,0",
            name = "Built-in Microphone"
        )

        assertEquals("hw:0,0", device.deviceId)
        assertEquals("Built-in Microphone", device.name)
        assertNull(device.vendor)
        assertNull(device.description)
        assertNull(device.version)
    }

    @Test
    fun `construction with all fields`() {
        val device = AudioInputDevice(
            deviceId = "hw:1,0",
            name = "USB Microphone",
            vendor = "ACME Audio Corp",
            description = "Professional USB microphone",
            version = "2.0"
        )

        assertEquals("hw:1,0", device.deviceId)
        assertEquals("USB Microphone", device.name)
        assertEquals("ACME Audio Corp", device.vendor)
        assertEquals("Professional USB microphone", device.description)
        assertEquals("2.0", device.version)
    }

    @Test
    fun `data class equality works correctly`() {
        val device1 = AudioInputDevice(
            deviceId = "hw:0,0",
            name = "Built-in Microphone",
            vendor = "Audio Corp",
            description = "Test device",
            version = "1.0"
        )

        val device2 = AudioInputDevice(
            deviceId = "hw:0,0",
            name = "Built-in Microphone",
            vendor = "Audio Corp",
            description = "Test device",
            version = "1.0"
        )

        assertEquals(device1, device2)
        assertEquals(device1.hashCode(), device2.hashCode())
    }

    @Test
    fun `data class inequality works correctly`() {
        val device1 = AudioInputDevice(
            deviceId = "hw:0,0",
            name = "Built-in Microphone"
        )

        val device2 = AudioInputDevice(
            deviceId = "hw:1,0",
            name = "USB Microphone"
        )

        assertNotEquals(device1, device2)
    }

    @Test
    fun `data class copy works correctly`() {
        val original = AudioInputDevice(
            deviceId = "hw:0,0",
            name = "Built-in Microphone",
            vendor = "Audio Corp"
        )

        val modified = original.copy(name = "Updated Microphone")

        assertEquals("hw:0,0", original.deviceId)
        assertEquals("Built-in Microphone", original.name)
        assertEquals("Audio Corp", original.vendor)

        assertEquals("hw:0,0", modified.deviceId)
        assertEquals("Updated Microphone", modified.name)
        assertEquals("Audio Corp", modified.vendor)
    }

    @Test
    fun `data class copy can modify optional fields`() {
        val original = AudioInputDevice(
            deviceId = "hw:0,0",
            name = "Microphone"
        )

        val modified = original.copy(
            vendor = "New Vendor",
            description = "New Description",
            version = "1.0"
        )

        assertNull(original.vendor)
        assertNull(original.description)
        assertNull(original.version)

        assertEquals("New Vendor", modified.vendor)
        assertEquals("New Description", modified.description)
        assertEquals("1.0", modified.version)
    }
}
