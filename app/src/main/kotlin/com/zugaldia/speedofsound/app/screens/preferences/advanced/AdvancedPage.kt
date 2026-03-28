package com.zugaldia.speedofsound.app.screens.preferences.advanced

import com.zugaldia.speedofsound.app.ICON_PREFERENCES_OTHER
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.adw.SpinRow
import org.gnome.adw.SwitchRow

class AdvancedPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val sanitizeSpecialCharsRow: SwitchRow
    private val postHideDelayRow: SpinRow
    private val typingDelayRow: SpinRow

    init {
        title = "Advanced"
        iconName = ICON_PREFERENCES_OTHER

        sanitizeSpecialCharsRow = SwitchRow().apply {
            title = "Sanitize Special Characters"
            subtitle = "Replace accented and special characters with ASCII equivalents before typing. " +
                    "Enable this if your desktop portal does not correctly handle Unicode keysyms."
            active = viewModel.getSanitizeSpecialChars()
            onNotify("active") {
                viewModel.setSanitizeSpecialChars(active)
            }
        }

        postHideDelayRow = SpinRow.withRange(POST_HIDE_DELAY_MIN, POST_HIDE_DELAY_MAX, STEP).apply {
            title = "Post-Hide Delay (ms)"
            subtitle = "Time to wait after hiding the window before typing. Set to 0 to disable. " +
                    "Increase this if the beginning of the typed text is missing."
            digits = 0
            value = viewModel.getPostHideDelayMs().toDouble()
            onNotify("value") {
                viewModel.setPostHideDelayMs(value.toInt())
            }
        }

        typingDelayRow = SpinRow.withRange(TYPING_DELAY_MIN, TYPING_DELAY_MAX, STEP).apply {
            title = "Typing Delay (ms)"
            subtitle = "Delay between each keystroke. Set to 0 to disable. " +
                    "Increase this if characters are dropped, out of order, " +
                    "or like a slower typing effect."
            digits = 0
            value = viewModel.getTypingDelayMs().toDouble()
            onNotify("value") {
                viewModel.setTypingDelayMs(value.toInt())
            }
        }

        val group = PreferencesGroup().apply {
            title = "Typing"
            description = "Settings on this section control low-level typing behavior. " +
                    "The defaults are safe for most desktop environments and generally do not need to be changed."
            add(sanitizeSpecialCharsRow)
            add(postHideDelayRow)
            add(typingDelayRow)
        }

        add(group)
    }

    companion object {
        private const val STEP = 1.0
        private const val POST_HIDE_DELAY_MIN = 0.0
        private const val POST_HIDE_DELAY_MAX = 1000.0
        private const val TYPING_DELAY_MIN = 0.0
        private const val TYPING_DELAY_MAX = 100.0
    }
}
