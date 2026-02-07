package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.getDataDir
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties

class PropertiesStore(filename: String = DEFAULT_PROPERTIES_FILENAME) : SettingsStore {
    private val logger = LoggerFactory.getLogger(PropertiesStore::class.java)
    private val properties = Properties()
    private val filePath = getDataDir().resolve(filename).toFile()

    init {
        load()
    }

    private fun load() {
        if (filePath.exists()) {
            try {
                logger.info("Loading properties from $filePath")
                FileInputStream(filePath).use { properties.load(it) }
            } catch (e: IOException) {
                logger.error("Error loading properties from $filePath: ${e.message}")
            }
        }
    }

    private fun save(): Boolean = try {
        filePath.parentFile?.mkdirs()
        FileOutputStream(filePath).use { properties.store(it, null) }
        true
    } catch (e: IOException) {
        logger.error("Error saving properties to $filePath: ${e.message}")
        false
    }

    override fun isAvailable(): Boolean = true

    override fun getString(key: String, defaultValue: String): String =
        properties.getProperty(key, defaultValue)

    override fun setString(key: String, value: String): Boolean {
        properties.setProperty(key, value)
        return save()
    }

    override fun getStringArray(key: String, defaultValue: List<String>): List<String> {
        val value = properties.getProperty(key)
        return when {
            value == null -> defaultValue
            value.isEmpty() -> emptyList()
            else -> value.split(ARRAY_DELIMITER)
        }
    }

    override fun setStringArray(key: String, value: List<String>): Boolean {
        properties.setProperty(key, value.joinToString(ARRAY_DELIMITER))
        return save()
    }

    companion object {
        private const val ARRAY_DELIMITER = "|||"
    }
}
