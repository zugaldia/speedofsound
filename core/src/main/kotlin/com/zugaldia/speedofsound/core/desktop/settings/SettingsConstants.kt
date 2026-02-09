package com.zugaldia.speedofsound.core.desktop.settings

import com.zugaldia.speedofsound.core.APPLICATION_SHORT
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.plugins.llm.DEFAULT_ANTHROPIC_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.llm.DEFAULT_GOOGLE_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.llm.DEFAULT_OPENAI_MODEL_ID

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

const val KEY_GOOGLE_MODEL_NAME = "google-model-name"
const val DEFAULT_GOOGLE_MODEL_NAME = DEFAULT_GOOGLE_MODEL_ID

const val KEY_GOOGLE_BASE_URL = "google-base-url"
const val DEFAULT_GOOGLE_BASE_URL = ""

const val KEY_GOOGLE_TEXT_USE_API_KEY = "google-text-use-api-key"
const val DEFAULT_GOOGLE_TEXT_USE_API_KEY = false

const val KEY_ANTHROPIC_MODEL_NAME = "anthropic-model-name"
val DEFAULT_ANTHROPIC_MODEL_NAME = DEFAULT_ANTHROPIC_MODEL_ID

const val KEY_ANTHROPIC_BASE_URL = "anthropic-base-url"
const val DEFAULT_ANTHROPIC_BASE_URL = ""

const val KEY_ANTHROPIC_TEXT_USE_API_KEY = "anthropic-text-use-api-key"
const val DEFAULT_ANTHROPIC_TEXT_USE_API_KEY = false

const val KEY_OPENAI_MODEL_NAME = "openai-model-name"
val DEFAULT_OPENAI_MODEL_NAME = DEFAULT_OPENAI_MODEL_ID

const val KEY_OPENAI_BASE_URL = "openai-base-url"
const val DEFAULT_OPENAI_BASE_URL = ""

const val KEY_OPENAI_TEXT_USE_API_KEY = "openai-text-use-api-key"
const val DEFAULT_OPENAI_TEXT_USE_API_KEY = false

// Personalization page

const val KEY_CUSTOM_CONTEXT = "custom-context"
const val DEFAULT_CUSTOM_CONTEXT = ""

const val KEY_CUSTOM_VOCABULARY = "custom-vocabulary"
val DEFAULT_CUSTOM_VOCABULARY = emptyList<String>()
