package com.zugaldia.speedofsound.core.plugins.director

import com.zugaldia.speedofsound.core.plugins.AppPlugin

/**
 * Director plugin that orchestrates recorder, ASR, and LLM plugins
 * to create the end-to-end user experience.
 */
abstract class DirectorPlugin(
    options: DirectorOptions = DirectorOptions(),
) : AppPlugin<DirectorOptions>(
    initialOptions = options,
) {
    abstract suspend fun start()
    abstract suspend fun stop()
    abstract suspend fun cancel()
}
