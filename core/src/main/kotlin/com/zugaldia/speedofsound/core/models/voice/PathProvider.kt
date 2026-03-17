package com.zugaldia.speedofsound.core.models.voice

import com.zugaldia.speedofsound.core.getDataDir as getCoreDataDir
import com.zugaldia.speedofsound.core.getCacheDir as getCoreCacheDir
import java.nio.file.Path

/**
 * Provides paths for data and cache directories.
 */
interface PathProvider {
    fun getDataDir(): Path
    fun getCacheDir(): Path
}

/**
 * Production implementation that uses the global path functions.
 */
class DefaultPathProvider : PathProvider {
    override fun getDataDir(): Path = getCoreDataDir()
    override fun getCacheDir(): Path = getCoreCacheDir()
}
