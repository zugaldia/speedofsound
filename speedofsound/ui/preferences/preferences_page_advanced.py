from gi.repository import Adw  # type: ignore

from speedofsound.constants import (
    SETTING_INCLUDE_APPLICATION_NAME,
    SETTING_JOYSTICK_ID,
    SETTING_JOYSTICK_LANGUAGE_LEFT,
    SETTING_JOYSTICK_LANGUAGE_RIGHT,
    SETTING_RECORDING_TIMEOUT_SECONDS,
    SETTING_SAVE_TRANSCRIPTIONS,
    SETTING_TYPIST_BACKEND,
)
from speedofsound.ui.preferences.preferences_page_base import PreferencesPageBase
from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageAdvanced(PreferencesPageBase):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__(view_model)
        self.set_title("Advanced")
        self.set_icon_name("applications-engineering-symbolic")
        self._build_ui()
        self._logger.info("Advanced preferences page initialized")

    def _build_ui(self) -> None:
        context_group = Adw.PreferencesGroup()
        context_group.set_title("Context")
        context_group.set_description("Configure context information for transcription")

        include_app_switch = Adw.SwitchRow()
        include_app_switch.set_title("Include Application Context")
        include_app_switch.set_subtitle(
            "Include active application and window information in transcription prompts"
        )
        self.bind_boolean_setting(SETTING_INCLUDE_APPLICATION_NAME, include_app_switch)
        context_group.add(include_app_switch)

        self.add(context_group)

        recording_group = Adw.PreferencesGroup()
        recording_group.set_title("Recording")
        recording_group.set_description("Configure recording behavior")

        timeout_spin = Adw.SpinRow.new_with_range(1, 300, 1)
        timeout_spin.set_title("Recording Timeout")
        timeout_spin.set_subtitle("Maximum recording duration in seconds")
        self.bind_int_setting(SETTING_RECORDING_TIMEOUT_SECONDS, timeout_spin)
        recording_group.add(timeout_spin)

        self.add(recording_group)

        benchmarking_group = Adw.PreferencesGroup()
        benchmarking_group.set_title("Benchmarking")
        benchmarking_group.set_description(
            "Configure benchmarking and analysis features"
        )

        save_transcriptions_switch = Adw.SwitchRow()
        save_transcriptions_switch.set_title("Save Transcriptions")
        save_transcriptions_switch.set_subtitle(
            "Save audio recordings and transcriptions for benchmarking"
        )

        self.bind_boolean_setting(
            SETTING_SAVE_TRANSCRIPTIONS, save_transcriptions_switch
        )

        benchmarking_group.add(save_transcriptions_switch)
        self.add(benchmarking_group)

        typist_group = Adw.PreferencesGroup()
        typist_group.set_title("Typist")
        typist_group.set_description("Configure text typing behavior")

        typist_backend_combo = self.create_typist_backend_combo(
            "Typist Backend", SETTING_TYPIST_BACKEND
        )
        typist_group.add(typist_backend_combo)

        self.add(typist_group)

        joystick_group = Adw.PreferencesGroup()
        joystick_group.set_title("Joystick")
        joystick_group.set_description("Configure joystick trigger settings")

        joystick_combo = self.create_joystick_combo(
            "Joystick Device", SETTING_JOYSTICK_ID
        )
        joystick_group.add(joystick_combo)

        joystick_left_combo = self.create_language_combo(
            "Left Button Language", SETTING_JOYSTICK_LANGUAGE_LEFT
        )
        joystick_group.add(joystick_left_combo)

        joystick_right_combo = self.create_language_combo(
            "Right Button Language", SETTING_JOYSTICK_LANGUAGE_RIGHT
        )
        joystick_group.add(joystick_right_combo)

        self.add(joystick_group)

    def create_joystick_combo(self, title: str, setting_key: str) -> Adw.ComboRow:
        """Create a joystick device combo row for the given setting key."""
        available_devices = self._view_model.configuration.available_joystick_devices

        options = [("No joystick", -1)]
        for device in available_devices:
            options.append((device.name, device.id))

        def get_current():
            return getattr(
                self._view_model.configuration, setting_key.replace("-", "_")
            )

        def set_value(value):
            settings = self._view_model.configuration.settings
            if settings:
                settings.set_int(setting_key, value)

        return self.create_combo_row(
            title=title,
            setting_key=setting_key,
            options=options,
            get_current_value=get_current,
            set_value=set_value,
            subtitle="Requires application restart to take effect",
        )

    def create_typist_backend_combo(self, title: str, setting_key: str) -> Adw.ComboRow:
        """Create a typist backend combo row for the given setting key."""
        options = [
            ("Auto", "auto"),
            ("AT-SPI", "atspi"),
            ("xdotool", "xdotool"),
            ("ydotool", "ydotool"),
        ]

        def get_current():
            return getattr(
                self._view_model.configuration, setting_key.replace("-", "_")
            )

        def set_value(value):
            settings = self._view_model.configuration.settings
            if settings:
                settings.set_string(setting_key, value)

        return self.create_combo_row(
            title=title,
            setting_key=setting_key,
            options=options,
            get_current_value=get_current,
            set_value=set_value,
            subtitle="Requires application restart to take effect",
        )
