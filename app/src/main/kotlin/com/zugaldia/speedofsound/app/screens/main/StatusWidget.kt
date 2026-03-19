package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_MARGIN
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Label
import org.gnome.gtk.Orientation
import org.gnome.pango.EllipsizeMode

class StatusWidget : Box() {
    private val asrModelLabel: Label
    private val llmModelSeparator: Label
    private val llmModelLabel: Label
    private val languageLabel: Label

    companion object {
        private const val SEPARATOR_CHARACTER = "·"
        private const val MAX_MODEL_LABEL_LENGTH = 15
    }

    init {
        orientation = Orientation.HORIZONTAL
        hexpand = true
        halign = Align.CENTER
        vexpand = false
        spacing = DEFAULT_BOX_SPACING
        marginTop = DEFAULT_MARGIN
        marginBottom = DEFAULT_MARGIN
        marginStart = DEFAULT_MARGIN
        marginEnd = DEFAULT_MARGIN

        asrModelLabel = createModelLabel()
        append(asrModelLabel)

        llmModelSeparator = createStatusLabel(SEPARATOR_CHARACTER, isDimmed = true)
        append(llmModelSeparator)

        llmModelLabel = createModelLabel()
        append(llmModelLabel)

        append(createStatusLabel(SEPARATOR_CHARACTER, isDimmed = true))

        languageLabel = createStatusLabel("", isDimmed = true)
        append(languageLabel)
    }

    fun setAsrModel(model: String) {
        asrModelLabel.label = model
    }

    fun setLlmModel(model: String) {
        llmModelLabel.label = model
        llmModelSeparator.visible = model.isNotEmpty()
        llmModelLabel.visible = model.isNotEmpty()
    }

    fun setLanguage(language: String) {
        languageLabel.label = language
    }

    private fun createModelLabel(): Label {
        return Label("").apply {
            cssClasses = arrayOf("caption", "dim-label")
            maxWidthChars = MAX_MODEL_LABEL_LENGTH
            ellipsize = EllipsizeMode.END
        }
    }

    private fun createStatusLabel(text: String, isFramed: Boolean = false, isDimmed: Boolean = false): Label {
        val classes = mutableListOf("caption")
        if (isFramed) classes.add("frame")
        if (isDimmed) classes.add("dim-label")
        val finalText = if (isFramed) "  $text  " else text
        return Label(finalText).apply { cssClasses = classes.toTypedArray() }
    }
}
