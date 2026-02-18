package com.zugaldia.speedofsound.core.models.text

import com.zugaldia.speedofsound.core.models.SelectableModel
import com.zugaldia.speedofsound.core.plugins.llm.LlmProvider

data class TextModel(
    override val id: String,
    override val name: String, // User-friendly name
    val provider: LlmProvider
) : SelectableModel
