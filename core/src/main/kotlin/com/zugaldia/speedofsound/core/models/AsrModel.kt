package com.zugaldia.speedofsound.core.models

import com.zugaldia.speedofsound.core.Language

data class AsrModel(
    val id: String,
    val name: String, // User-friendly name
    val archiveUrl: String,
    val archiveSha256sum: String,
    val dataSizeMegabytes: Long, // Model files uncompressed in the data folder (not the archive file download)
    val decoder: String,
    val encoder: String,
    val tokens: String,
    val languages: List<Language> = emptyList() // Only if language restrictions apply, empty for multilingual models
)
