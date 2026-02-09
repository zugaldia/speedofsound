package com.zugaldia.speedofsound.core.plugins.director

import com.zugaldia.speedofsound.core.plugins.AppPlugin

/**
 * Base class for director plugins.
 *
 * Director plugin orchestrates recorder, ASR, and LLM plugins
 * to create the end-to-end user experience.
 */
abstract class DirectorPlugin<Options : DirectorPluginOptions>(initialOptions: Options) :
    AppPlugin<Options>(initialOptions) {

    abstract suspend fun start()
    abstract suspend fun stop()
    abstract suspend fun cancel()
}
