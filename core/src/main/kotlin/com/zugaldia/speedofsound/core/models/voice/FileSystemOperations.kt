package com.zugaldia.speedofsound.core.models.voice

import java.io.File
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

/**
 * Abstraction for file system operations.
 */
interface FileSystemOperations {
    fun exists(path: Path): Boolean
    fun isDirectory(path: Path): Boolean
    fun mkdirs(path: Path)
    fun deleteRecursively(path: Path)
    fun copyFile(source: File, destination: File, overwrite: Boolean = true)
    fun fileLength(file: File): Long
}

/**
 * Production implementation that performs actual file system operations.
 */
class DefaultFileSystemOperations : FileSystemOperations {
    override fun exists(path: Path): Boolean {
        return path.toFile().exists()
    }

    override fun isDirectory(path: Path): Boolean {
        return path.toFile().isDirectory
    }

    override fun mkdirs(path: Path) {
        path.toFile().mkdirs()
    }

    @OptIn(ExperimentalPathApi::class)
    override fun deleteRecursively(path: Path) {
        path.deleteRecursively()
    }

    override fun copyFile(source: File, destination: File, overwrite: Boolean) {
        source.copyTo(destination, overwrite)
    }

    override fun fileLength(file: File): Long {
        return file.length()
    }
}
