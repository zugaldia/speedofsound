package com.zugaldia.speedofsound.core.models.voice

import com.zugaldia.speedofsound.core.getDataDir
import com.zugaldia.speedofsound.core.getTmpDataDir
import java.nio.file.Path

/**
 * Provides paths for data and temporary directories.
 */
interface PathProvider {
    fun getDataDir(): Path
    fun getTmpDataDir(): Path
}

/**
 * Production implementation that uses the global path functions.
 */
class DefaultPathProvider : PathProvider {
    override fun getDataDir(): Path = getDataDir()
    override fun getTmpDataDir(): Path = getTmpDataDir()
}
