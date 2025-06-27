#
# General
#

APPLICATION_ID = "io.speedofsound.App"
APPLICATION_NAME = "Speed of Sound"

EXTENSION_SCHEMA = "org.gnome.shell.extensions.speedofsound"

CONFIG_FILE = "config.toml"
CONFIG_EXAMPLE_FILE = "config.example.toml"
LOG_FILE = "speedofsound.log"

DEFAULT_SPACING = 10
DEFAULT_MARGIN = 20

#
# Signals
#

# Orchestrator
ORCHESTRATOR_EVENT_SIGNAL = "orchestrator-event"
LANGUAGE_NAME_SIGNAL = "language-name"
MICROPHONE_NAME_SIGNAL = "microphone-name"
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
