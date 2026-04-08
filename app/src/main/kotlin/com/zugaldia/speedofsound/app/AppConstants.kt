package com.zugaldia.speedofsound.app

/*
 * We generally do not define custom colors. Instead, we rely on the ADW style classes:
 * https://gnome.pages.gitlab.gnome.org/libadwaita/doc/1.3/style-classes.html
 */

const val ENV_DISABLE_GIO_STORE = "SOS_DISABLE_GIO_STORE"
const val ENV_DISABLE_GSTREAMER = "SOS_DISABLE_GSTREAMER"
const val ENV_COLOR_SCHEME = "SOS_COLOR_SCHEME"

const val MIN_ADW_MAJOR_VERSION = 1
const val MIN_ADW_MINOR_VERSION = 5

const val ADW_MAX_LENGTH_MIN_MAJOR_VERSION = 1
const val ADW_MAX_LENGTH_MIN_MINOR_VERSION = 6

const val DEFAULT_WINDOW_WIDTH = 450
const val DEFAULT_WINDOW_HEIGHT = 275

const val DEFAULT_PREFERENCES_DIALOG_WIDTH = 800
const val DEFAULT_PREFERENCES_DIALOG_HEIGHT = 800

const val DEFAULT_ADD_CREDENTIAL_DIALOG_WIDTH = 500
const val DEFAULT_ADD_CREDENTIAL_DIALOG_HEIGHT = 300

const val DEFAULT_PROGRESS_BAR_WIDTH = 300

const val DEFAULT_TEXT_VIEW_PADDING = 10
const val DEFAULT_TEXT_VIEW_HEIGHT = 200

const val DEFAULT_MARGIN = 10
const val DEFAULT_BOX_SPACING = 10

const val SETTINGS_SAVE_DEBOUNCE_MS = 500

const val SEPARATOR_CHARACTER = "·"

// Adw CSS style classes
// https://gnome.pages.gitlab.gnome.org/libadwaita/doc/1-latest/style-classes.html
const val STYLE_CLASS_TITLE_1 = "title-1"
const val STYLE_CLASS_BODY = "body"
const val STYLE_CLASS_ACCENT = "accent"
const val STYLE_CLASS_SUCCESS = "success"
const val STYLE_CLASS_WARNING = "warning"
const val STYLE_CLASS_ERROR = "error"
const val STYLE_CLASS_SUGGESTED_ACTION = "suggested-action"
const val STYLE_CLASS_DESTRUCTIVE_ACTION = "destructive-action"
const val STYLE_CLASS_BOXED_LIST = "boxed-list"
const val STYLE_CLASS_DIM_LABEL = "dim-label"
const val STYLE_CLASS_FLAT = "flat"
const val STYLE_CLASS_LINKED = "linked"

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

// Voice + Text model provider limits
const val MAX_VOICE_MODEL_PROVIDERS = 10
const val MAX_TEXT_MODEL_PROVIDERS = 10
const val MAX_PROVIDER_CONFIG_NAME_LENGTH = 100
const val DEFAULT_ADD_PROVIDER_DIALOG_WIDTH = 600
const val DEFAULT_ADD_PROVIDER_DIALOG_HEIGHT = 500

// Transcription output
const val APPEND_SPACE_TEXT = " "

const val TRIGGER_ACTION = "trigger"

const val SIGNAL_STAGE_CHANGED = "stage-changed"
const val SIGNAL_RECORDING_LEVEL = "recording-level"
const val SIGNAL_REMOTE_DESKTOP_STATUS = "remote-desktop-status"
const val SIGNAL_PIPELINE_COMPLETED = "pipeline-completed"
const val SIGNAL_LANGUAGE_CHANGED = "language-changed"
const val SIGNAL_ASR_MODEL_CHANGED = "asr-model-changed"
const val SIGNAL_LLM_MODEL_CHANGED = "llm-model-changed"

// Icon names (all bundled)
const val ICON_AUTO_ADJUST = "sos-image-auto-adjust-symbolic"
const val ICON_DOWNLOAD = "sos-folder-download-symbolic"
const val ICON_EDIT = "sos-document-edit-symbolic"
const val ICON_MENU = "sos-open-menu-symbolic"
const val ICON_MICROPHONE = "sos-audio-input-microphone-symbolic"
const val ICON_PASSWORD = "sos-dialog-password-symbolic"
const val ICON_PREFERENCES_OTHER = "sos-preferences-other-symbolic"
const val ICON_PREFERENCES_SYSTEM = "sos-preferences-system-symbolic"
const val ICON_REFRESH = "sos-view-refresh-symbolic"
const val ICON_SEND = "sos-document-send-symbolic"
const val ICON_SERVER = "sos-network-server-symbolic"
const val ICON_SOUND_WAVE = "sos-sound-wave-symbolic"
const val ICON_STOP = "sos-stop-symbolic"
const val ICON_TEXT_EDITOR = "sos-accessories-text-editor-symbolic"
const val ICON_TRASH = "sos-user-trash-symbolic"
