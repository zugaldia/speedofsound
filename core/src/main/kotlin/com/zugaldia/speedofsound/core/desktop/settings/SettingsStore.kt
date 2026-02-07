package com.zugaldia.speedofsound.core.desktop.settings

interface SettingsStore {
    fun isAvailable(): Boolean
    fun getString(key: String, defaultValue: String): String
    fun setString(key: String, value: String): Boolean
    fun getStringArray(key: String, defaultValue: List<String>): List<String>
    fun setStringArray(key: String, value: List<String>): Boolean
}
