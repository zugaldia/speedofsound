package com.zugaldia.speedofsound.app.screens.preferences.shared

import com.zugaldia.speedofsound.core.plugins.SelectableProvider
import org.gnome.adw.ComboRow
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

/**
 * Generic ComboRow for selecting a provider (ASR or LLM).
 *
 * @param T The provider type that implements SelectableProvider
 * @param providers List of providers to show
 */
class ProviderComboRow<T>(
    rowTitle: String,
    rowSubtitle: String,
    getCurrentProvider: () -> T?,
    private val onProviderSelected: (T) -> Unit,
    private val providers: List<T>
) : ComboRow() where T : SelectableProvider {
    private val logger = LoggerFactory.getLogger(ProviderComboRow::class.java)

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
