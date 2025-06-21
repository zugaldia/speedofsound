#
# General
#

APPLICATION_ID = "io.speedofsound.App"
APPLICATION_NAME = "Speed of Sound"

EXTENSION_SCHEMA = "org.gnome.shell.extensions.speedofsound"

CONFIG_FILE = "config.toml"
LOG_FILE = "speedofsound.log"

DEFAULT_WIDTH = 500
DEFAULT_HEIGHT = 250

DEFAULT_PADDING = 10
DEFAULT_SPACING = 10
DEFAULT_MARGIN = 10

#
# Signals
#

# Input
INPUT_BUTTON_CLICKED_SIGNAL = "input-button-clicked"

# Orchestrator
ORCHESTRATOR_EVENT_SIGNAL = "orchestrator-event"

# Control
CONTROL_EVENT_SIGNAL = "control-event"

# Recorder
RECORDER_RESPONSE_SIGNAL = "recorder-response"
VOLUME_LEVEL_SIGNAL = "volume-level"

# Transcriber
TRANSCRIBER_RESPONSE_SIGNAL = "transcriber-response"

# Typist
TYPIST_RESPONSE_SIGNAL = "typist-response"
