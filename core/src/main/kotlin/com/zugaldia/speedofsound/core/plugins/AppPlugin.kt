package com.zugaldia.speedofsound.core.plugins

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AppPlugin<Options : AppPluginOptions>(initialOptions: Options) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val _events = MutableSharedFlow<AppPluginEvent>(extraBufferCapacity = 10)
    val events: SharedFlow<AppPluginEvent> = _events.asSharedFlow()

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

    protected suspend fun emitEvent(event: AppPluginEvent) {
        _events.emit(event)
    }

    /**
     * Non-suspend event emission. Returns true if the event was emitted, false if dropped.
     */
    protected fun tryEmitEvent(event: AppPluginEvent): Boolean {
        return _events.tryEmit(event)
    }
}
