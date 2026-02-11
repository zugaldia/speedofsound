package com.zugaldia.speedofsound.app.screens.preferences.credentials

import com.zugaldia.speedofsound.app.CREDENTIAL_MASK_PREFIX_LENGTH
import com.zugaldia.speedofsound.app.CREDENTIAL_MASK_SUFFIX_LENGTH
import com.zugaldia.speedofsound.app.DEFAULT_BOX_SPACING
import com.zugaldia.speedofsound.app.MAX_CREDENTIALS
import com.zugaldia.speedofsound.app.MIN_CREDENTIAL_LENGTH_FOR_MASKING
import com.zugaldia.speedofsound.app.screens.preferences.PreferencesViewModel
import com.zugaldia.speedofsound.core.desktop.settings.CredentialSetting
import org.gnome.adw.ActionRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.PreferencesPage
import org.gnome.gtk.Align
import org.gnome.gtk.Box
import org.gnome.gtk.Button
import org.gnome.gtk.Label
import org.gnome.gtk.ListBox
import org.gnome.gtk.Orientation
import org.gnome.gtk.SelectionMode
import org.slf4j.LoggerFactory

class CloudCredentialsPage(private val viewModel: PreferencesViewModel) : PreferencesPage() {
    private val logger = LoggerFactory.getLogger(CloudCredentialsPage::class.java)

    private val credentialsListBox: ListBox
    private val placeholderBox: Box
    private val addButton: Button

    init {
        title = "Cloud Credentials"
        iconName = "network-server-symbolic"

        addButton = Button.withLabel("Add API Key").apply {
            addCssClass("suggested-action")
            onClicked { showAddCredentialDialog() }
        }

        credentialsListBox = ListBox().apply {
            addCssClass("boxed-list")
            marginTop = DEFAULT_BOX_SPACING
            selectionMode = SelectionMode.NONE
        }

        val placeholderLabel = Label("No cloud credentials configured").apply {
            addCssClass("dim-label")
            halign = Align.CENTER
        }

        placeholderBox = Box(Orientation.VERTICAL, 0).apply {
            vexpand = true
            halign = Align.FILL
            valign = Align.FILL

            // Add expanding spacers above and below to center the label vertically
            append(Box(Orientation.VERTICAL, 0).apply { vexpand = true })
            append(placeholderLabel)
            append(Box(Orientation.VERTICAL, 0).apply { vexpand = true })
        }

        val credentialsGroup = PreferencesGroup().apply {
            title = "Cloud Credentials"
            description = "(Optional) Add credentials for cloud services like Anthropic, Google, or OpenAI. " +
                    "These credentials can be referenced when adding Voice and Text Models."
            add(addButton)
            add(credentialsListBox)
            add(placeholderBox)
        }

        add(credentialsGroup)
        loadInitialCredentials()
    }

    private fun loadInitialCredentials() {
        val credentials = viewModel.getCredentials()
        credentials.sortedBy { it.name.lowercase() }.forEach { credential -> addCredentialToUI(credential) }
        updatePlaceholderVisibility()
    }

    private fun showAddCredentialDialog() {
        val existingNames = viewModel.getCredentials().map { it.name }.toSet()
        val dialog = AddCredentialDialog(existingNames) { credential -> onCredentialAdded(credential) }
        dialog.present(this)
    }

    private fun onCredentialAdded(credential: CredentialSetting) {
        val currentCredentials = viewModel.getCredentials()
        if (currentCredentials.size >= MAX_CREDENTIALS) {
            logger.warn("Cannot add credential: limit of $MAX_CREDENTIALS reached")
            return
        }

        val exists = currentCredentials.any { it.name == credential.name }
        if (exists) {
            logger.warn("Credential with name '${credential.name}' already exists")
            return
        }

        val updatedCredentials = currentCredentials + credential
        logger.info("Adding credential, total is now ${updatedCredentials.size} entries.")
        viewModel.setCredentials(updatedCredentials)
        addCredentialToUI(credential)
        updatePlaceholderVisibility()
    }

    private fun addCredentialToUI(credential: CredentialSetting) {
        val maskedValue = maskCredentialValue(credential.value)
        val row = ActionRow().apply {
            title = credential.name
            subtitle = maskedValue
        }

        val deleteButton = Button.fromIconName("user-trash-symbolic").apply {
            addCssClass("flat")
            valign = Align.CENTER
        }

        row.addSuffix(deleteButton)
        credentialsListBox.append(row)
        deleteButton.onClicked {
            credentialsListBox.remove(row)
            onCredentialDeleted(credential.name)
        }
    }

    private fun maskCredentialValue(value: String): String =
        if (value.length < MIN_CREDENTIAL_LENGTH_FOR_MASKING) {
            "..."
        } else {
            "${value.take(CREDENTIAL_MASK_PREFIX_LENGTH)}...${value.takeLast(CREDENTIAL_MASK_SUFFIX_LENGTH)}"
        }

    private fun onCredentialDeleted(credentialName: String) {
        val currentCredentials = viewModel.getCredentials()
        val credentialToDelete = currentCredentials.find { it.name == credentialName }
        if (credentialToDelete != null) {
            val providers = viewModel.getTextModelProviders()
            val referencingProviders = providers.filter { it.credentialId == credentialToDelete.id }
            if (referencingProviders.isNotEmpty()) {
                val providerNames = referencingProviders.joinToString(", ") { it.name }
                logger.warn("Cannot delete credential '$credentialName': used by providers: $providerNames")
                return
            }
        }

        // Proceed with deletion
        val updatedCredentials = currentCredentials.filter { it.name != credentialName }
        logger.info("Removing credential, total is now ${updatedCredentials.size} entries.")
        viewModel.setCredentials(updatedCredentials)
        updatePlaceholderVisibility()
    }

    private fun updatePlaceholderVisibility() {
        val credentials = viewModel.getCredentials()
        val hasCredentials = credentials.isNotEmpty()
        val atLimit = credentials.size >= MAX_CREDENTIALS
        credentialsListBox.visible = hasCredentials
        placeholderBox.visible = !hasCredentials
        addButton.sensitive = !atLimit
        if (atLimit) {
            logger.info("Credential limit of $MAX_CREDENTIALS reached")
        }
    }
}
