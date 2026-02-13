package com.zugaldia.speedofsound.app

/*
 * We generally do not define custom colors. Instead, we rely on the ADW style classes:
 * https://gnome.pages.gitlab.gnome.org/libadwaita/doc/1.3/style-classes.html
 */

const val ENV_DISABLE_GIO_STORE = "SOS_DISABLE_GIO_STORE"

const val MIN_ADW_MAJOR_VERSION = 1
const val MIN_ADW_MINOR_VERSION = 5

const val DEFAULT_WINDOW_WIDTH = 400
const val DEFAULT_WINDOW_HEIGHT = 200

const val DEFAULT_PREFERENCES_DIALOG_WIDTH = 800
const val DEFAULT_PREFERENCES_DIALOG_HEIGHT = 600

const val DEFAULT_ADD_CREDENTIAL_DIALOG_WIDTH = 500
const val DEFAULT_ADD_CREDENTIAL_DIALOG_HEIGHT = 300

const val DEFAULT_PROGRESS_BAR_WIDTH = 300

const val DEFAULT_TEXT_VIEW_PADDING = 10
const val DEFAULT_TEXT_VIEW_HEIGHT = 200

const val DEFAULT_MARGIN = 10
const val DEFAULT_BOX_SPACING = 10

const val POST_HIDE_DELAY_MS = 100L
const val SETTINGS_SAVE_DEBOUNCE_MS = 500

// Personalization limits
const val MAX_VOCABULARY_WORDS = 50
const val MAX_CUSTOM_CONTEXT_CHARS = 2000 // Approximately 3 paragraphs

// Credential limits
const val MAX_CREDENTIALS = 25
const val MAX_CREDENTIAL_NAME_LENGTH = 100
const val MAX_CREDENTIAL_VALUE_LENGTH = 500

// Credential masking
const val MIN_CREDENTIAL_LENGTH_FOR_MASKING = 10
const val CREDENTIAL_MASK_PREFIX_LENGTH = 4
const val CREDENTIAL_MASK_SUFFIX_LENGTH = 4

// Text model provider limits
const val MAX_TEXT_MODEL_PROVIDERS = 10
const val MAX_PROVIDER_CONFIG_NAME_LENGTH = 100
const val DEFAULT_ADD_PROVIDER_DIALOG_WIDTH = 600
const val DEFAULT_ADD_PROVIDER_DIALOG_HEIGHT = 550

const val TRIGGER_ACTION = "trigger"

const val SIGNAL_STAGE_CHANGED = "stage-changed"
const val SIGNAL_RECORDING_LEVEL = "recording-level"
const val SIGNAL_PORTALS_RESTORE_TOKEN_MISSING = "portals-restore-token-missing"
const val SIGNAL_PIPELINE_COMPLETED = "pipeline-completed"
const val SIGNAL_LANGUAGE_CHANGED = "language-changed"
