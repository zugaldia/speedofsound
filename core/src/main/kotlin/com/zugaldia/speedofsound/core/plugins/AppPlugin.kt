package com.zugaldia.speedofsound.core.plugins

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Base class for all application plugins with a managed lifecycle.
 *
 * Lifecycle flow: initialize() → enable() → [updateOptions()] → disable() → [enable() ...] → shutdown()
 *
 * Plugins are initialized once at creation and can be enabled/disabled multiple times during their lifetime
 * before final shutdown when the application exits.
 *
 * @param Options Type of plugin-specific configuration options. Must be immutable (all val properties).
 *                Option changes go through updateOptions() where the plugin can react.
 */
abstract class AppPlugin<Options : AppPluginOptions>(initialOptions: Options) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Unique identifier for this plugin.
     */
    abstract val id: String

    protected var currentOptions: Options = initialOptions

    /**
     * Provides read-only access to plugin events.
     */
    private val _events = MutableSharedFlow<AppPluginEvent>(extraBufferCapacity = 10)
    val events: SharedFlow<AppPluginEvent> = _events.asSharedFlow()

    /**
     * Provides read-only access to the current options.
     *
     * To modify options, use updateOptions() so the plugin can react to changes.
     */
    fun getOptions(): Options = currentOptions

    /**
     * Called when the user updates plugin configuration at runtime. Override to respond to option changes.
     */
    open fun updateOptions(options: Options) {
        currentOptions = options
    }

    /**
     * Suspend event emission.
     */
    protected suspend fun emitEvent(event: AppPluginEvent) {
        _events.emit(event)
    }

    /**
     * Non-suspend event emission. Returns true if the event was emitted, false if dropped.
     */
    protected fun tryEmitEvent(event: AppPluginEvent): Boolean {
        return _events.tryEmit(event)
    }

    /**
     * Called once when the plugin is first created during application initialization.
     *
     * This function must be lightweight as it's called for all plugins at startup. Heavy operations
     * (model loading, resource allocation) should be deferred to enable() when the plugin becomes active.
     */
    open fun initialize() {
    }

    /**
     * Called when the plugin becomes active. Can be called multiple times during the plugin's lifetime.
     *
     * This happens when the plugin is the preferred option at startup or when the user switches to this
     * plugin at runtime. Perform resource allocation and model loading here.
     */
    open fun enable() {
    }

    /**
     * Called when the plugin becomes inactive. Can be called multiple times during the plugin's lifetime.
     *
     * This happens when the user switches to another plugin or disables this plugin category entirely.
     * Release resources and clean up the state here.
     */
    open fun disable() {
        log.info("Disabling.")
    }

    /**
     * Called once when the application is shutting down. Perform final cleanup here.
     */
    open fun shutdown() {
        log.info("Shutting down.")
    }
}
