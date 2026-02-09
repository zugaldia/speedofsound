package com.zugaldia.speedofsound.app.screens.preferences.personalization

import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.app.DEFAULT_TEXT_VIEW_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_TEXT_VIEW_PADDING
import com.zugaldia.speedofsound.app.MAX_CUSTOM_CONTEXT_CHARS
import com.zugaldia.speedofsound.app.MAX_VOCABULARY_WORDS
import com.zugaldia.speedofsound.app.SETTINGS_SAVE_DEBOUNCE_MS
import org.gnome.adw.ActionRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.glib.GLib
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Entry
import org.gnome.gtk.ListBox
import org.gnome.gtk.Orientation
import org.gnome.gtk.ScrolledWindow
import org.gnome.gtk.SelectionMode
import org.gnome.gtk.TextIter
import org.gnome.gtk.TextView
import org.gnome.gtk.WrapMode
import org.slf4j.LoggerFactory

/*
 * We will want to switch to Adw.WrapBox once we target a higher Adw version.
 * https://gnome.pages.gitlab.gnome.org/libadwaita/doc/1-latest/class.WrapBox.html
 */
class PersonalizationPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(PersonalizationPage::class.java)

    private val vocabularyListBox: ListBox
    private val instructionsTextView: TextView
    private var saveInstructionsCounter: Int = 0

    init {
        title = "Personalization"
        iconName = "document-edit-symbolic"

        instructionsTextView = TextView().apply {
            wrapMode = WrapMode.WORD_CHAR
            topMargin = DEFAULT_TEXT_VIEW_PADDING
            bottomMargin = DEFAULT_TEXT_VIEW_PADDING
            leftMargin = DEFAULT_TEXT_VIEW_PADDING
            rightMargin = DEFAULT_TEXT_VIEW_PADDING
        }

        val instructionsScrolledWindow = ScrolledWindow().apply {
            child = instructionsTextView
            minContentHeight = DEFAULT_TEXT_VIEW_HEIGHT
        }

        val instructionsGroup = PreferencesGroup().apply {
            title = "Custom Context"
            description = "Optionally share details like your location or writing style to help improve " +
                "transcriptions (max $MAX_CUSTOM_CONTEXT_CHARS characters)"
            add(instructionsScrolledWindow)
        }

        vocabularyListBox = ListBox().apply {
            addCssClass("boxed-list")
            marginTop = DEFAULT_BOX_SPACING
            selectionMode = SelectionMode.NONE
        }

        val vocabularyEntry = Entry().apply {
            hexpand = true
            placeholderText = "Add a word or short phrase"
        }

        val vocabularyAddButton = Button.withLabel("Add").apply {
            addCssClass("suggested-action")
        }

        val vocabularyEntryBox = Box(Orientation.HORIZONTAL, 0).apply {
            addCssClass("linked")
            append(vocabularyEntry)
            append(vocabularyAddButton)
        }

        val vocabularyGroup = PreferencesGroup().apply {
            title = "Custom Vocabulary"
            description = "Optionally add entries the model should recognize, such as names, " +
                "technical terms, or acronyms (max $MAX_VOCABULARY_WORDS words)"
            add(vocabularyEntryBox)
            add(vocabularyListBox)
        }

        vocabularyAddButton.onClicked { addVocabularyWord(vocabularyEntry) }
        vocabularyEntry.onActivate { addVocabularyWord(vocabularyEntry) }

        add(instructionsGroup)
        add(vocabularyGroup)

        loadInitialValues()
        instructionsTextView.buffer.onChanged {
            enforceTextLimit()
            scheduleSaveInstructions()
        }
    }

    private fun loadInitialValues() {
        val instructions = viewModel.getCustomContext()
        instructionsTextView.buffer.setText(instructions, -1)

        val vocabulary = viewModel.getCustomVocabulary()
        vocabulary.sortedWith(String.CASE_INSENSITIVE_ORDER).forEach { word -> addVocabularyWordToUI(word) }
    }

    /**
     * Schedule a save operation after the user stops typing using a counter-based debounced approach.
     * This avoids unnecessary disk writes with every keystroke.
     * Future alternative: use Kotlin coroutines with Flow.debounce() for automatic cancellation.
     */
    private fun scheduleSaveInstructions() {
        saveInstructionsCounter++
        val currentCounter = saveInstructionsCounter
        GLib.timeoutAdd(GLib.PRIORITY_DEFAULT, SETTINGS_SAVE_DEBOUNCE_MS) {
            if (saveInstructionsCounter == currentCounter) {
                saveInstructions()
            }
            false // Don't repeat
        }
    }

    /**
     * Force immediate save of instructions.
     * Should be called when the dialog is closed to ensure no data is lost.
     */
    fun forceSaveInstructions() {
        saveInstructionsCounter++ // Invalidate any pending saves
        saveInstructions()
    }

    private fun saveInstructions() {
        val buffer = instructionsTextView.buffer
        val start = TextIter()
        val end = TextIter()
        buffer.getBounds(start, end)
        val text = buffer.getText(start, end, false)
        logger.info("Saving instructions: ${text.length} chars.")
        viewModel.setCustomContext(text)
    }

    /**
     * Enforce character limit on custom context text.
     * GTK TextView doesn't have a built-in max-length property, so we implement it manually.
     */
    private fun enforceTextLimit() {
        val buffer = instructionsTextView.buffer
        val charCount = buffer.charCount
        if (charCount > MAX_CUSTOM_CONTEXT_CHARS) {
            val start = TextIter()
            val end = TextIter()
            buffer.getBounds(start, end)
            val text = buffer.getText(start, end, false)
            val truncated = text.substring(0, MAX_CUSTOM_CONTEXT_CHARS)
            buffer.setText(truncated, -1)
            buffer.getEndIter(end)
            buffer.placeCursor(end)
            logger.warn("Text truncated to $MAX_CUSTOM_CONTEXT_CHARS characters")
        }
    }

    private fun saveVocabulary() {
        val vocabulary = mutableListOf<String>()
        var child = vocabularyListBox.firstChild
        while (child != null) {
            if (child is ActionRow) { vocabulary.add(child.title) }
            child = child.nextSibling
        }

        logger.info("Saving vocabulary: ${vocabulary.size} words.")
        viewModel.setCustomVocabulary(vocabulary)
    }

    private fun addVocabularyWord(entry: Entry) {
        val word = entry.text.trim()
        if (word.isEmpty()) return

        var wordCount = 0
        var child = vocabularyListBox.firstChild
        while (child != null) {
            if (child is ActionRow) wordCount++
            child = child.nextSibling
        }

        if (wordCount >= MAX_VOCABULARY_WORDS) {
            logger.warn("Cannot add word: vocabulary limit of $MAX_VOCABULARY_WORDS reached")
            return
        }

        addVocabularyWordToUI(word)
        entry.text = ""
        saveVocabulary()
    }

    private fun addVocabularyWordToUI(word: String) {
        val row = ActionRow().apply { title = word }
        val deleteButton = Button.fromIconName("user-trash-symbolic").apply {
            addCssClass("flat")
            valign = Align.CENTER
            onClicked {
                vocabularyListBox.remove(row)
                saveVocabulary()
            }
        }

        row.addSuffix(deleteButton)
        vocabularyListBox.append(row)
    }
}
