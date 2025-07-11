from speedofsound.constants import (
    APPLICATION_ID,
    APPLICATION_NAME,
    CONFIG_FILE,
    CONTROL_EVENT_SIGNAL,
    DEFAULT_MARGIN,
    DEFAULT_SPACING,
    LANGUAGE_NAME_SIGNAL,
    LOG_FILE,
    MODEL_NAME_SIGNAL,
    ORCHESTRATOR_EVENT_SIGNAL,
    RECORDER_RESPONSE_SIGNAL,
    SETTING_EXT_ERROR,
    SETTING_EXT_STATUS,
    SETTING_SHOW_WELCOME,
    TRANSCRIBER_RESPONSE_SIGNAL,
    TYPIST_RESPONSE_SIGNAL,
    VOLUME_LEVEL_SIGNAL,
    WORDS_PER_MINUTE_SIGNAL,
)


class TestGeneralConstants:
    """Test general application constants."""


    def test_application_id(self):
        assert APPLICATION_ID == "io.speedofsound.App"
        assert isinstance(APPLICATION_ID, str)
        assert APPLICATION_ID.startswith("io.speedofsound")

    def test_application_name(self):
        assert APPLICATION_NAME == "Speed of Sound"
        assert isinstance(APPLICATION_NAME, str)
        assert len(APPLICATION_NAME) > 0

    def test_config_files(self):
        assert CONFIG_FILE == "config.toml"
        assert isinstance(CONFIG_FILE, str)
        assert CONFIG_FILE.endswith(".toml")

    def test_log_file(self):
        assert LOG_FILE == "speedofsound.log"
        assert isinstance(LOG_FILE, str)
        assert LOG_FILE.endswith(".log")

    def test_default_spacing_and_margin(self):
        assert DEFAULT_SPACING == 10
        assert DEFAULT_MARGIN == 20
        assert isinstance(DEFAULT_SPACING, int)
        assert isinstance(DEFAULT_MARGIN, int)
        assert DEFAULT_SPACING > 0
        assert DEFAULT_MARGIN > 0


class TestSettingConstants:
    """Test setting-related constants."""

    def test_setting_constants_are_strings(self):
        settings = [
            SETTING_SHOW_WELCOME,
            SETTING_EXT_STATUS,
            SETTING_EXT_ERROR,
        ]
        for setting in settings:
            assert isinstance(setting, str)
            assert len(setting) > 0

    def test_setting_values(self):
        assert SETTING_SHOW_WELCOME == "show-welcome"
        assert SETTING_EXT_STATUS == "extension-status"
        assert SETTING_EXT_ERROR == "extension-error"

    def test_setting_naming_convention(self):
        settings = [
            SETTING_SHOW_WELCOME,
            SETTING_EXT_STATUS,
            SETTING_EXT_ERROR,
        ]
        for setting in settings:
            assert "-" in setting
            assert setting.islower()


class TestSignalConstants:
    """Test signal-related constants."""

    def test_orchestrator_signals(self):
        orchestrator_signals = [
            ORCHESTRATOR_EVENT_SIGNAL,
            LANGUAGE_NAME_SIGNAL,
            MODEL_NAME_SIGNAL,
            WORDS_PER_MINUTE_SIGNAL,
        ]
        for signal in orchestrator_signals:
            assert isinstance(signal, str)
            assert len(signal) > 0

    def test_orchestrator_signal_values(self):
        assert ORCHESTRATOR_EVENT_SIGNAL == "orchestrator-event"
        assert LANGUAGE_NAME_SIGNAL == "language-name"
        assert MODEL_NAME_SIGNAL == "model-name"
        assert WORDS_PER_MINUTE_SIGNAL == "words-per-minute"

    def test_control_signal(self):
        assert CONTROL_EVENT_SIGNAL == "control-event"
        assert isinstance(CONTROL_EVENT_SIGNAL, str)

    def test_component_signals(self):
        component_signals = [
            RECORDER_RESPONSE_SIGNAL,
            VOLUME_LEVEL_SIGNAL,
            TRANSCRIBER_RESPONSE_SIGNAL,
            TYPIST_RESPONSE_SIGNAL,
        ]
        for signal in component_signals:
            assert isinstance(signal, str)
            assert len(signal) > 0

    def test_component_signal_values(self):
        assert RECORDER_RESPONSE_SIGNAL == "recorder-response"
        assert VOLUME_LEVEL_SIGNAL == "volume-level"
        assert TRANSCRIBER_RESPONSE_SIGNAL == "transcriber-response"
        assert TYPIST_RESPONSE_SIGNAL == "typist-response"

    def test_signal_naming_convention(self):
        signals = [
            ORCHESTRATOR_EVENT_SIGNAL,
            LANGUAGE_NAME_SIGNAL,
            MODEL_NAME_SIGNAL,
            WORDS_PER_MINUTE_SIGNAL,
            CONTROL_EVENT_SIGNAL,
            RECORDER_RESPONSE_SIGNAL,
            VOLUME_LEVEL_SIGNAL,
            TRANSCRIBER_RESPONSE_SIGNAL,
            TYPIST_RESPONSE_SIGNAL,
        ]
        for signal in signals:
            assert "-" in signal
            assert signal.islower()


class TestConstantsUniqueness:
    """Test that constants are unique where appropriate."""

    def test_signal_constants_are_unique(self):
        signals = [
            ORCHESTRATOR_EVENT_SIGNAL,
            LANGUAGE_NAME_SIGNAL,
            MODEL_NAME_SIGNAL,
            WORDS_PER_MINUTE_SIGNAL,
            CONTROL_EVENT_SIGNAL,
            RECORDER_RESPONSE_SIGNAL,
            VOLUME_LEVEL_SIGNAL,
            TRANSCRIBER_RESPONSE_SIGNAL,
            TYPIST_RESPONSE_SIGNAL,
        ]
        assert len(signals) == len(set(signals))

    def test_setting_constants_are_unique(self):
        settings = [
            SETTING_SHOW_WELCOME,
            SETTING_EXT_STATUS,
            SETTING_EXT_ERROR,
        ]
        assert len(settings) == len(set(settings))
