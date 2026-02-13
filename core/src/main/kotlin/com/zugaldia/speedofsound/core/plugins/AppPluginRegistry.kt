package com.zugaldia.speedofsound.core.plugins

import org.slf4j.LoggerFactory

/**
 * Manages the lifecycle of all plugins in the system.
 */
class AppPluginRegistry {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val plugins = mutableMapOf<AppPluginCategory, MutableList<AppPlugin<*>>>()
    private val activePlugins = mutableMapOf<AppPluginCategory, String>()

    /**
     * Registers a plugin for a given category and initializes it.
     */
    fun register(category: AppPluginCategory, plugin: AppPlugin<*>) {
        log.info("Registering plugin ${plugin.id} for category $category")
        plugins.getOrPut(category) { mutableListOf() }.add(plugin)
        plugin.initialize()
    }

    /**
     * Sets a specific plugin as active for a given category by its ID.
     * Disables the currently active plugin (if any) and enables the new one.
     */
    fun setActiveById(category: AppPluginCategory, pluginId: String) {
        if (activePlugins[category] == pluginId) {
            log.info("Plugin $pluginId is already active for category $category, skipping")
            return
        }

        val plugin = getPluginById(category, pluginId)
            ?: error("Plugin with ID $pluginId not found in category $category")

        // Disable the currently active plugin if any
        activePlugins[category]?.let { currentActiveId ->
            getPluginById(category, currentActiveId)?.let { currentActive ->
                log.info("Disabling currently active plugin ${currentActive.id}")
                currentActive.disable()
            }
        }

        // Enable the new plugin
        log.info("Setting plugin ${plugin.id} as active for category $category")
        plugin.enable()
        activePlugins[category] = plugin.id
    }

    /**
     * Gets a specific plugin by its ID within a category.
     * Returns null if not found.
     */
    fun getPluginById(category: AppPluginCategory, pluginId: String): AppPlugin<*>? {
        return plugins[category]?.find { it.id == pluginId }
    }

    /**
     * Gets the currently active plugin for a given category.
     * Returns null if no plugin is active for this category.
     */
    fun getActive(category: AppPluginCategory): AppPlugin<*>? {
        val activeId = activePlugins[category] ?: return null
        return getPluginById(category, activeId)
    }

    /**
     * Shuts down all registered plugins. Called when the application is shutting down.
     */
    fun shutdownAll() {
        log.info("Shutting down all plugins")
        plugins.values.flatten().forEach { plugin ->
            plugin.disable()
            plugin.shutdown()
        }
    }
}
