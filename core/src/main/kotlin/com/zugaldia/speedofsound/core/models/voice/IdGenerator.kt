package com.zugaldia.speedofsound.core.models.voice

import com.zugaldia.speedofsound.core.generateUniqueId as coreGenerateUniqueId

/**
 * Abstraction for generating unique IDs.
 */
interface IdGenerator {
    fun generateUniqueId(): String
}

/**
 * Production implementation that uses the global generateUniqueId function.
 */
class DefaultIdGenerator : IdGenerator {
    override fun generateUniqueId(): String {
        return coreGenerateUniqueId()
    }
}
