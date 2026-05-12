package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.plugins.asr.DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.asr.SUPPORTED_SHERPA_CANARY_ASR_MODELS
import com.zugaldia.speedofsound.core.plugins.asr.SUPPORTED_SHERPA_PARAKEET_ASR_MODELS
import com.zugaldia.speedofsound.core.plugins.asr.SUPPORTED_SHERPA_WHISPER_ASR_MODELS

const val DEFAULT_PROPERTIES_FILENAME = "$APPLICATION_SHORT.properties"

const val KEY_PORTALS_RESTORE_TOKEN = "portals-restore-token"
const val DEFAULT_PORTALS_RESTORE_TOKEN = ""

const val KEY_WELCOME_SCREEN_SHOWN = "welcome-screen-shown"
const val DEFAULT_WELCOME_SCREEN_SHOWN = false

// General page

const val KEY_SHORTCUT_CONFIGURED = "shortcut-configured"
const val DEFAULT_SHORTCUT_CONFIGURED = false

const val KEY_DEFAULT_LANGUAGE = "default-language"
val DEFAULT_LANGUAGE = Language.ENGLISH

const val KEY_SECONDARY_LANGUAGE = "secondary-language"
val DEFAULT_SECONDARY_LANGUAGE = Language.SPANISH

const val KEY_BACKGROUND_RECORDING = "background-recording"
const val DEFAULT_BACKGROUND_RECORDING = false

const val KEY_HIDE_INSTEAD_OF_MINIMIZE = "hide-instead-of-minimize"
const val DEFAULT_HIDE_INSTEAD_OF_MINIMIZE = false

const val KEY_STAY_HIDDEN_ON_ACTIVATION = "stay-hidden-on-activation"
const val DEFAULT_STAY_HIDDEN_ON_ACTIVATION = false

const val KEY_MAX_RECORDING_DURATION_S = "max-recording-duration-s"
const val DEFAULT_MAX_RECORDING_DURATION_S = 30

const val KEY_APPEND_SPACE = "append-space"
const val DEFAULT_APPEND_SPACE = false

const val KEY_TEXT_OUTPUT_METHOD = "text-output-method"
const val TEXT_OUTPUT_METHOD_PORTAL = "portal"
const val TEXT_OUTPUT_METHOD_CLIPBOARD = "clipboard"
const val DEFAULT_TEXT_OUTPUT_METHOD = TEXT_OUTPUT_METHOD_PORTAL

// Cloud Credentials page

const val KEY_CREDENTIALS = "credentials"
const val DEFAULT_CREDENTIALS = "[]"

// Voice Models page

// The ONNX provider is intentionally excluded as its current implementation is very limited
// and only offers another Whisper variant.
val SUPPORTED_LOCAL_ASR_MODELS =
    SUPPORTED_SHERPA_WHISPER_ASR_MODELS + SUPPORTED_SHERPA_CANARY_ASR_MODELS + SUPPORTED_SHERPA_PARAKEET_ASR_MODELS

const val KEY_VOICE_MODEL_PROVIDERS = "voice-model-providers"
const val DEFAULT_VOICE_MODEL_PROVIDERS = "[]"

const val KEY_SELECTED_VOICE_MODEL_PROVIDER_ID = "selected-voice-model-provider-id"
const val DEFAULT_SELECTED_VOICE_MODEL_PROVIDER_ID = DEFAULT_ASR_SHERPA_WHISPER_MODEL_ID

// Text Models page

const val KEY_TEXT_PROCESSING_ENABLED = "text-processing-enabled"
const val DEFAULT_TEXT_PROCESSING_ENABLED = false

const val KEY_TEXT_MODEL_PROVIDERS = "text-model-providers"
const val DEFAULT_TEXT_MODEL_PROVIDERS = "[]"

const val KEY_SELECTED_TEXT_MODEL_PROVIDER_ID = "selected-text-model-provider-id"
const val DEFAULT_SELECTED_TEXT_MODEL_PROVIDER_ID = ""

// Advanced page

const val KEY_SANITIZE_SPECIAL_CHARS = "sanitize-special-chars"
const val DEFAULT_SANITIZE_SPECIAL_CHARS = false

const val KEY_POST_HIDE_DELAY_MS = "post-hide-delay-ms"
const val DEFAULT_POST_HIDE_DELAY_MS = 100

const val KEY_TYPING_DELAY_MS = "typing-delay-ms"
const val DEFAULT_TYPING_DELAY_MS = 10

// Personalization page

const val KEY_CUSTOM_CONTEXT = "custom-context"
const val DEFAULT_CUSTOM_CONTEXT = ""

const val KEY_CUSTOM_VOCABULARY = "custom-vocabulary"
val DEFAULT_CUSTOM_VOCABULARY = emptyList<String>()
