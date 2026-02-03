package com.zugaldia.speedofsound.app.screens.preferences

import org.slf4j.LoggerFactory

class PreferencesViewModel {
    private val logger = LoggerFactory.getLogger(PreferencesViewModel::class.java)

    var state: PreferencesState = PreferencesState()
        private set

    init {
        logger.info("Initializing.")
    }

    fun shutdown() {
        logger.info("Shutting down.")
    }
}
