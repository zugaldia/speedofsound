#
# General
#

APPLICATION_ID = "io.speedofsound.App"
APPLICATION_NAME = "Speed of Sound"
APPLICATION_VERSION = "1.0.0"
APPLICATION_DEVELOPER = "Antonio Zugaldia"
APPLICATION_WEBSITE = "https://github.com/zugaldia/speedofsound"
APPLICATION_ISSUE_URL = "https://github.com/zugaldia/speedofsound/issues"
APPLICATION_DOCUMENTATION_URL = (
    "https://github.com/zugaldia/speedofsound/blob/main/README.md"
)

LOG_FILE = "speedofsound.log"

DEFAULT_SPACING = 10
DEFAULT_MARGIN = 20

DASHBOARD_WINDOW_DEFAULT_WIDTH = 800
DASHBOARD_WINDOW_DEFAULT_HEIGHT = 600

#
# Settings
#

SETTING_EXT_STATUS = "extension-status"
SETTING_EXT_ERROR = "extension-error"
SETTING_COPY_TO_CLIPBOARD = "copy-to-clipboard"
SETTING_SAVE_TRANSCRIPTIONS = "save-transcriptions"
SETTING_RECORDING_TIMEOUT_SECONDS = "recording-timeout-seconds"
SETTING_LANGUAGE = "language"
SETTING_JOYSTICK_ID = "joystick-id"
SETTING_JOYSTICK_LANGUAGE_LEFT = "joystick-language-left"
SETTING_JOYSTICK_LANGUAGE_RIGHT = "joystick-language-right"
SETTING_FASTER_WHISPER_MODEL = "faster-whisper-model"
SETTING_FASTER_WHISPER_DEVICE = "faster-whisper-device"
SETTING_OPENAI_BASE_URL = "openai-base-url"
SETTING_OPENAI_API_KEY = "openai-api-key"
SETTING_OPENAI_MODEL = "openai-model"
SETTING_FALLBACK_TIMEOUT_SECONDS = "fallback-timeout-seconds"
SETTING_TYPIST_BACKEND = "typist-backend"
SETTING_PREFERRED_TRANSCRIBER = "preferred-transcriber"
SETTING_INCLUDE_APPLICATION_NAME = "include-application-name"
SETTING_MICROPHONE_DEVICE = "microphone-device"

#
# Default values (consistent with gschema.xml)
#

DEFAULT_COPY_TO_CLIPBOARD = False
DEFAULT_EXT_STATUS = "white"
DEFAULT_EXT_ERROR = ""
DEFAULT_SAVE_TRANSCRIPTIONS = False
DEFAULT_RECORDING_TIMEOUT_SECONDS = 60
DEFAULT_LANGUAGE = "en"
DEFAULT_JOYSTICK_ID = -1
DEFAULT_JOYSTICK_LANGUAGE_LEFT = "en"
DEFAULT_JOYSTICK_LANGUAGE_RIGHT = "es"
DEFAULT_FASTER_WHISPER_MODEL = "small"
DEFAULT_FASTER_WHISPER_DEVICE = "auto"
DEFAULT_OPENAI_BASE_URL = ""
DEFAULT_OPENAI_API_KEY = ""
DEFAULT_OPENAI_MODEL = "gpt-4o-transcribe"
DEFAULT_FALLBACK_TIMEOUT_SECONDS = 2.0
DEFAULT_TYPIST_BACKEND = "auto"
DEFAULT_PREFERRED_TRANSCRIBER = "faster_whisper"
DEFAULT_INCLUDE_APPLICATION_NAME = False
DEFAULT_MICROPHONE_DEVICE = ""

#
# Actions
#

ACTION_PREFERENCES = "preferences"
ACTION_DOCUMENTATION = "documentation"
ACTION_ABOUT = "about"
ACTION_TRIGGER = "trigger"
ACTION_SHOW = "show"
ACTION_QUIT = "quit"

#
# Signals
#

# Configuration
PREFERRED_TRANSCRIBER_CHANGED_SIGNAL = "preferred-transcriber-changed"

# Orchestrator
ORCHESTRATOR_EVENT_SIGNAL = "orchestrator-event"
LANGUAGE_NAME_SIGNAL = "language-name"
MODEL_NAME_SIGNAL = "model-name"
WORDS_PER_MINUTE_SIGNAL = "words-per-minute"

# Control
CONTROL_EVENT_SIGNAL = "control-event"

# Recorder
RECORDER_RESPONSE_SIGNAL = "recorder-response"
VOLUME_LEVEL_SIGNAL = "volume-level"

# Transcriber
TRANSCRIBER_RESPONSE_SIGNAL = "transcriber-response"

# Typist
TYPIST_RESPONSE_SIGNAL = "typist-response"
