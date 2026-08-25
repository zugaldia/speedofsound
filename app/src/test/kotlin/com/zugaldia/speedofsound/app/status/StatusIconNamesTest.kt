package com.zugaldia.speedofsound.app.status

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusIconNamesTest {

    @Test
    fun `flatpak uses the exported app icon`() {
        assertEquals(APPLICATION_ID, statusIconName(monochrome = false, environment = RuntimeEnvironment.FLATPAK))
    }

    @Test
    fun `flatpak uses the exported symbolic icon when monochrome`() {
        assertEquals(
            "$APPLICATION_ID-symbolic",
            statusIconName(monochrome = true, environment = RuntimeEnvironment.FLATPAK),
        )
    }

    @Test
    fun `sandboxes without exported icons fall back to the standard icon`() {
        for (environment in listOf(RuntimeEnvironment.SNAP, RuntimeEnvironment.APPIMAGE, RuntimeEnvironment.JVM)) {
            assertEquals(STATUS_ICON_FALLBACK, statusIconName(monochrome = false, environment = environment))
        }
    }

    @Test
    fun `sandboxes without exported icons fall back to the standard symbolic icon`() {
        for (environment in listOf(RuntimeEnvironment.SNAP, RuntimeEnvironment.APPIMAGE, RuntimeEnvironment.JVM)) {
            assertEquals(STATUS_ICON_FALLBACK_SYMBOLIC, statusIconName(monochrome = true, environment = environment))
        }
    }
}
