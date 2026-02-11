package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.Language

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
