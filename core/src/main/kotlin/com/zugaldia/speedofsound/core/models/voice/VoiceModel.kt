package com.zugaldia.speedofsound.core.models.voice

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.models.SelectableModel
import com.zugaldia.speedofsound.core.plugins.asr.AsrProvider

data class VoiceModelFile(
    val name: String,
    val url: String? = null,
    val sha256sum: String? = null,
)

data class VoiceModel(
    override val id: String,
    override val name: String, // User-friendly name
    val provider: AsrProvider,
    val languages: List<Language> = emptyList(), // Only if language restrictions apply, empty for multilingual models
    val dataSizeMegabytes: Long = 0L, // Model files uncompressed (not the archive file download), 0 for cloud models
    val archiveFile: VoiceModelFile? = null,
    val components: List<VoiceModelFile> = emptyList()
) : SelectableModel
