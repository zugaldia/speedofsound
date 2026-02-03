package com.zugaldia.speedofsound.app.screens.preferences

import org.apache.logging.log4j.LogManager

class PreferencesViewModel {
    private val logger = LogManager.getLogger()

    var state: PreferencesState = PreferencesState()
        private set

    init {
        logger.info("Initializing.")
    }

    fun shutdown() {
        logger.info("Shutting down.")
    }
}
