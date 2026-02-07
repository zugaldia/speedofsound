package com.zugaldia.speedofsound.app.settings

/*
 * Store backed by Gio.
 *
 * It can be managed from the CLI. Examples:
 *
 * gsettings list-keys io.speedofsound.App
 * gsettings get io.speedofsound.App language
 * gsettings set io.speedofsound.App language en
 *
 */

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.desktop.settings.SettingsStore
import org.gnome.gio.Settings
import org.gnome.gio.SettingsSchema
import org.gnome.gio.SettingsSchemaSource
import org.slf4j.LoggerFactory

class GioStore(val schemaId: String = APPLICATION_ID): SettingsStore {
    private val logger = LoggerFactory.getLogger(GioStore::class.java)

    private val settingsSchema: SettingsSchema?
    private val settings: Settings?
    private val isAvailable: Boolean

    init {
        val source = SettingsSchemaSource.getDefault()
        settingsSchema = source?.lookup(schemaId, true)
        settings = settingsSchema?.let { Settings(schemaId) }
        isAvailable = settings != null
    }

    override fun isAvailable(): Boolean = isAvailable

    private fun ensureKeyExists(key: String) {
        if (settingsSchema?.hasKey(key) != true) {
            // We throw the exception ourselves because otherwise the GLib-GIO-ERROR
            // is not caught and crashes the app. Example:
            // GLib-GIO-ERROR **: Settings schema 'io.speedofsound.App' does not contain a key named 'x'
            throw IllegalArgumentException("Schema ($schemaId) or key ($key) not found")
        }
    }

    override fun getString(key: String, defaultValue: String): String = try {
        ensureKeyExists(key)
        settings?.getString(key) ?: defaultValue
    } catch (e: IllegalArgumentException) {
        logger.error("Error getting setting ($key), using default ($defaultValue): ${e.message}")
        defaultValue
    }

    override fun setString(key: String, value: String): Boolean = try {
        ensureKeyExists(key)
        settings?.setString(key, value) ?: false
    } catch (e: IllegalArgumentException) {
        logger.error("Error setting value ($key -> $value): ${e.message}")
        false
    }

    override fun getStringArray(key: String, defaultValue: List<String>): List<String> = try {
        ensureKeyExists(key)
        settings?.getStrv(key)?.toList() ?: defaultValue
    } catch (e: IllegalArgumentException) {
        logger.error("Error getting array setting ($key), using default: ${e.message}")
        defaultValue
    }

    override fun setStringArray(key: String, value: List<String>): Boolean = try {
        ensureKeyExists(key)
        settings?.setStrv(key, value.toTypedArray()) ?: false
    } catch (e: IllegalArgumentException) {
        logger.error("Error setting array value ($key): ${e.message}")
        false
    }
}
