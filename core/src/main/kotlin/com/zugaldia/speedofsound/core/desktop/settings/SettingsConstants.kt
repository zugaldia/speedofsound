package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.Language

const val DEFAULT_PROPERTIES_FILENAME = "$APPLICATION_SHORT.properties"

const val KEY_PORTALS_RESTORE_TOKEN = "portals-restore-token"
const val DEFAULT_PORTALS_RESTORE_TOKEN = ""

const val KEY_CUSTOM_CONTEXT = "custom-context"
const val DEFAULT_CUSTOM_CONTEXT = ""

const val KEY_CUSTOM_VOCABULARY = "custom-vocabulary"
val DEFAULT_CUSTOM_VOCABULARY = emptyList<String>()

const val KEY_DEFAULT_LANGUAGE = "default-language"
val DEFAULT_LANGUAGE = Language.ENGLISH

const val KEY_SECONDARY_LANGUAGE = "secondary-language"
val DEFAULT_SECONDARY_LANGUAGE = Language.SPANISH

const val KEY_CLOUD_ENABLED = "cloud-enabled"
const val DEFAULT_CLOUD_ENABLED = false

const val KEY_ANTHROPIC_API_KEY = "anthropic-api-key"
const val DEFAULT_ANTHROPIC_API_KEY = ""

const val KEY_GOOGLE_API_KEY = "google-api-key"
const val DEFAULT_GOOGLE_API_KEY = ""

const val KEY_OPENAI_API_KEY = "openai-api-key"
const val DEFAULT_OPENAI_API_KEY = ""
