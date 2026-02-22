package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.plugins.asr.SUPPORTED_SHERPA_WHISPER_ASR_MODELS

const val DEFAULT_PROPERTIES_FILENAME = "$APPLICATION_SHORT.properties"

const val KEY_PORTALS_RESTORE_TOKEN = "portals-restore-token"
const val DEFAULT_PORTALS_RESTORE_TOKEN = ""

// General page

const val KEY_DEFAULT_LANGUAGE = "default-language"
val DEFAULT_LANGUAGE = Language.ENGLISH

const val KEY_SECONDARY_LANGUAGE = "secondary-language"
val DEFAULT_SECONDARY_LANGUAGE = Language.SPANISH

// Cloud Credentials page

const val KEY_CREDENTIALS = "credentials"
const val DEFAULT_CREDENTIALS = "[]"

// Voice Models page

// Initially exposing only Sherpa Whisper models. The ONNX provider is intentionally excluded
// as its current implementation is very limited and only offers another Whisper variant.
val SUPPORTED_LOCAL_ASR_MODELS = SUPPORTED_SHERPA_WHISPER_ASR_MODELS

const val KEY_VOICE_MODEL_PROVIDERS = "voice-model-providers"
const val DEFAULT_VOICE_MODEL_PROVIDERS = "[]"

const val KEY_SELECTED_VOICE_MODEL_PROVIDER_ID = "selected-voice-model-provider-id"
const val DEFAULT_VOICE_MODEL_PROVIDER_ID = "default-sherpa-provider"
const val DEFAULT_SELECTED_VOICE_MODEL_PROVIDER_ID = DEFAULT_VOICE_MODEL_PROVIDER_ID

// Text Models page

const val KEY_TEXT_PROCESSING_ENABLED = "text-processing-enabled"
const val DEFAULT_TEXT_PROCESSING_ENABLED = false

const val KEY_TEXT_MODEL_PROVIDERS = "text-model-providers"
const val DEFAULT_TEXT_MODEL_PROVIDERS = "[]"

const val KEY_SELECTED_TEXT_MODEL_PROVIDER_ID = "selected-text-model-provider-id"
const val DEFAULT_SELECTED_TEXT_MODEL_PROVIDER_ID = ""

// Personalization page

const val KEY_CUSTOM_CONTEXT = "custom-context"
const val DEFAULT_CUSTOM_CONTEXT = ""

const val KEY_CUSTOM_VOCABULARY = "custom-vocabulary"
val DEFAULT_CUSTOM_VOCABULARY = emptyList<String>()
