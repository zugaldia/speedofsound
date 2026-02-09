package com.zugaldia.speedofsound.app.screens.preferences.credentials

import com.zugaldia.speedofsound.app.DEFAULT_ADD_CREDENTIAL_DIALOG_HEIGHT
import com.zugaldia.speedofsound.app.DEFAULT_ADD_CREDENTIAL_DIALOG_WIDTH
import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.DEFAULT_MARGIN
import com.zugaldia.speedofsound.app.MAX_CREDENTIAL_NAME_LENGTH
import com.zugaldia.speedofsound.app.MAX_CREDENTIAL_VALUE_LENGTH
import com.zugaldia.speedofsound.core.Credential
import com.zugaldia.speedofsound.core.CredentialType
import com.zugaldia.speedofsound.core.generateUniqueId
import org.gnome.adw.Dialog
import org.gnome.adw.EntryRow
import org.gnome.adw.PasswordEntryRow
import org.gnome.adw.PreferencesGroup
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Orientation
import org.slf4j.LoggerFactory

class AddCredentialDialog(
    private val existingNames: Set<String>,
    private val onCredentialAdded: (Credential) -> Unit
) : Dialog() {
    private val logger = LoggerFactory.getLogger(AddCredentialDialog::class.java)

    private val nameEntry: EntryRow
    private val apiKeyEntry: PasswordEntryRow
    private val addButton: Button

    init {
        title = "Add Credential"
        contentWidth = DEFAULT_ADD_CREDENTIAL_DIALOG_WIDTH
        contentHeight = DEFAULT_ADD_CREDENTIAL_DIALOG_HEIGHT

        nameEntry = EntryRow().apply {
            title = "Name"
            // Cannot set (report upstream?), otherwise the app crashes with:
            //  (java:1284576): java-gi-WARNING **: 09:04:54.126: java.lang.AssertionError:
            //  java.lang.invoke.WrongMethodTypeException: handle's method type (Object[])Object
            //  but found (MemorySegment, int)void in ClickedCallback
            //maxLength = MAX_CREDENTIAL_NAME_LENGTH
        }

        apiKeyEntry = PasswordEntryRow().apply {
            title = "API Key"
            // Do not set (see comment above)
            //maxLength = MAX_CREDENTIAL_VALUE_LENGTH
        }

        val preferencesGroup = PreferencesGroup().apply {
            title = "Add Credential"
            description = "Use descriptive names like \"Anthropic (Work)\" or \"Gemini (Personal)\""
            vexpand = true
            add(nameEntry)
            add(apiKeyEntry)
        }

        val cancelButton = Button.withLabel("Cancel").apply {
            onClicked { close() }
        }

        addButton = Button.withLabel("Add").apply {
            addCssClass("suggested-action")
            sensitive = false
            onClicked {
                val name = nameEntry.text.trim()
                val apiKey = apiKeyEntry.text.trim()
                if (validateInput(name, apiKey)) {
                    val credential = Credential(
                        id = generateUniqueId(),
                        type = CredentialType.API_KEY,
                        name = name,
                        value = apiKey
                    )
                    onCredentialAdded(credential)
                    close()
                }
            }
        }

        val buttonBox = Box(Orientation.HORIZONTAL, DEFAULT_BOX_SPACING).apply {
            halign = Align.END
            valign = Align.END
            append(cancelButton)
            append(addButton)
        }

        val contentBox = Box(Orientation.VERTICAL, DEFAULT_BOX_SPACING).apply {
            marginTop = DEFAULT_MARGIN
            marginBottom = DEFAULT_MARGIN
            marginStart = DEFAULT_MARGIN
            marginEnd = DEFAULT_MARGIN
            vexpand = true
            append(preferencesGroup)
            append(buttonBox)
        }

        child = contentBox
        nameEntry.onNotify("text") { updateAddButtonState() }
        apiKeyEntry.onNotify("text") { updateAddButtonState() }
    }

    private fun updateAddButtonState() {
        val name = nameEntry.text.trim()
        val apiKey = apiKeyEntry.text.trim()
        addButton.sensitive = validateInput(name, apiKey)
    }

    @Suppress("ReturnCount")
    private fun validateInput(name: String, apiKey: String): Boolean {
        if (name.isEmpty() || apiKey.isEmpty()) { return false }
        if (name.length > MAX_CREDENTIAL_NAME_LENGTH) {
            logger.warn("Credential name too long: ${name.length} > $MAX_CREDENTIAL_NAME_LENGTH")
            return false
        }
        if (apiKey.length > MAX_CREDENTIAL_VALUE_LENGTH) {
            logger.warn("Credential value too long: ${apiKey.length} > $MAX_CREDENTIAL_VALUE_LENGTH")
            return false
        }
        if (existingNames.contains(name)) {
            logger.warn("Credential name already exists: $name")
            return false
        }

        return true
    }
}
