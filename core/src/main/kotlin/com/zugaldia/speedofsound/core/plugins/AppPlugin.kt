package com.zugaldia.speedofsound.core.plugins

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class AppPlugin<State : AppPluginState, Options : AppPluginOptions>(
    initialOptions: Options,
    initialState: State,
) {
    val log: Logger = LogManager.getLogger()

    open fun initialize() {
        log.info("Initializing.")
    }

    open fun enable() {
        log.info("Enabling.")
    }

    open fun disable() {
        log.info("Disabling.")
    }

    open fun shutdown() {
        log.info("Shutting down.")
    }
}
