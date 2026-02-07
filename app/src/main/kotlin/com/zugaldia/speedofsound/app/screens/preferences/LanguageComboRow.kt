package com.zugaldia.speedofsound.app.screens.preferences

import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.languageFromIso2
import org.gnome.adw.ComboRow
import org.gnome.gtk.StringList
import org.slf4j.LoggerFactory

class LanguageComboRow(
    rowTitle: String,
    rowSubtitle: String,
    getLanguage: () -> String,
    private val setLanguage: (String) -> Unit
) : ComboRow() {
    private val logger = LoggerFactory.getLogger(LanguageComboRow::class.java)

    private val languages = Language.all

    init {
        val languageNames = languages.map { it.name }.toTypedArray()
        val stringList = StringList(languageNames)

        title = rowTitle
        subtitle = rowSubtitle
        model = stringList
        useSubtitle = false
        enableSearch = true

        // Load initial value
        val savedIso2 = getLanguage()
        languageFromIso2(savedIso2)?.let { language ->
            val index = languages.indexOf(language)
            if (index >= 0) { selected = index }
        }

        // Save on change
        onNotify("selected") {
            val selectedIndex = selected
            if (selectedIndex in languages.indices) {
                logger.info("Default language changed to ${languages[selectedIndex].name}")
                setLanguage(languages[selectedIndex].iso2)
            }
        }
    }
}
