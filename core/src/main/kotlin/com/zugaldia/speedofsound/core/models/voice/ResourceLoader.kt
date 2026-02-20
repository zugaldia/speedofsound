package com.zugaldia.speedofsound.core.models.voice

import java.io.InputStream

/**
 * Abstraction for loading resources.
 */
interface ResourceLoader {
    fun loadResource(path: String): InputStream?
}

/**
 * Production implementation that loads resources from the classpath.
 */
class ClasspathResourceLoader : ResourceLoader {
    override fun loadResource(path: String): InputStream? {
        return this::class.java.getResourceAsStream(path)
    }
}
