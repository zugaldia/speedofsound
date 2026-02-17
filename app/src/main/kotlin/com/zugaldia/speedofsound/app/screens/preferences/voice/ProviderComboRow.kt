package com.zugaldia.speedofsound.app.screens.preferences.voice

import com.zugaldia.speedofsound.core.plugins.asr.AsrProvider
import org.gnome.adw.ComboRow
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

/**
 * Custom ComboRow for selecting an ASR provider.
 */
class ProviderComboRow(
    rowTitle: String,
    rowSubtitle: String,
    getCurrentProvider: () -> AsrProvider?,
    private val onProviderSelected: (AsrProvider) -> Unit
) : ComboRow() {
    private val logger = LoggerFactory.getLogger(ProviderComboRow::class.java)
    private val providers = AsrProvider.entries

    init {
        val providerNames = providers.map { it.displayName }.toTypedArray()
        val stringList = StringList(providerNames)

        title = rowTitle
        subtitle = rowSubtitle
        model = stringList
        useSubtitle = false // Do not use the _current value_ as the subtitle
        enableSearch = false

        // Load initial value
        getCurrentProvider()?.let { provider ->
            val index = providers.indexOf(provider)
            if (index >= 0) { selected = index }
        }
    }

    /**
     * Set up notification callbacks. Call this after all widgets are initialized.
     */
    fun setupNotifications() {
        onNotify("selected") {
            val selectedIndex = selected
            if (selectedIndex in providers.indices) {
                logger.info("Provider changed to ${providers[selectedIndex].name}")
                onProviderSelected(providers[selectedIndex])
            }
        }
    }
}
