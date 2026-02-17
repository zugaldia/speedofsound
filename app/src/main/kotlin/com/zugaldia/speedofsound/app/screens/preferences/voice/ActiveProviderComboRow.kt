package com.zugaldia.speedofsound.app.screens.preferences.voice

import com.zugaldia.speedofsound.core.desktop.settings.VoiceModelProviderSetting
import org.gnome.adw.ComboRow
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

/**
 * ComboRow for selecting the active voice model provider from the list of configured providers.
 */
class ActiveProviderComboRow(
    private val getSelectedProviderId: () -> String,
    private val setSelectedProviderId: (String) -> Unit
) : ComboRow() {
    private val logger = LoggerFactory.getLogger(ActiveProviderComboRow::class.java)

    private var providers: List<VoiceModelProviderSetting> = emptyList()

    // Prevents onNotify("selected") from firing while rebuilding the model, which would
    // overwrite the saved selection before we can restore it
    private var isUpdating = false

    init {
        title = "Active Provider"
        subtitle = "Select which provider to use for speech recognition"
        useSubtitle = true
        enableSearch = false
    }

    /**
     * Update the list of available providers and refresh the UI.
     */
    fun updateProviders(newProviders: List<VoiceModelProviderSetting>) {
        isUpdating = true
        try {
            providers = newProviders.sortedBy { it.name.lowercase() }
            if (providers.isEmpty()) {
                model = StringList(arrayOf())
                visible = false
                return
            }

            visible = true
            val providerNames = providers.map { it.name }.toTypedArray()
            model = StringList(providerNames)

            val savedProviderId = getSelectedProviderId()
            val selectedIndex = providers.indexOfFirst { it.id == savedProviderId }
            if (selectedIndex >= 0) {
                selected = selectedIndex
            } else if (providers.isNotEmpty()) {
                selected = 0 // Select the first provider
                setSelectedProviderId(providers[0].id)
            }
        } finally {
            isUpdating = false
        }
    }

    /**
     * Set up notification callbacks. Call this after all widgets are initialized.
     */
    fun setupNotifications() {
        onNotify("selected") {
            if (isUpdating || providers.isEmpty()) return@onNotify
            val selectedIndex = selected
            if (selectedIndex in providers.indices) {
                val selectedProvider = providers[selectedIndex]
                logger.info("Selected voice model provider changed to ${selectedProvider.name}")
                setSelectedProviderId(selectedProvider.id)
            }
        }
    }
}
