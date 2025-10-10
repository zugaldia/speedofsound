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

    def _build_ui(self) -> None:
        advanced_group = Adw.PreferencesGroup()
        advanced_group.set_title("Advanced")
        advanced_group.set_description("Configure advanced settings")

        include_app_switch = Adw.SwitchRow()
        include_app_switch.set_title("Include Application Context (Experimental)")
        include_app_switch.set_subtitle(
            "Automatically detect and include active application name to adapt transcription context"
        )
        self.bind_boolean_setting(SETTING_INCLUDE_APPLICATION_NAME, include_app_switch)
        advanced_group.add(include_app_switch)

        timeout_spin = Adw.SpinRow.new_with_range(1, 300, 1)
        timeout_spin.set_title("Recording Timeout")
        timeout_spin.set_subtitle("Maximum recording duration in seconds")
        self.bind_int_setting(SETTING_RECORDING_TIMEOUT_SECONDS, timeout_spin)
        advanced_group.add(timeout_spin)

        save_transcriptions_switch = Adw.SwitchRow()
        save_transcriptions_switch.set_title("Save Transcriptions")
        save_transcriptions_switch.set_subtitle(
            "Save audio recordings and transcriptions for benchmarking"
        )
        self.bind_boolean_setting(
            SETTING_SAVE_TRANSCRIPTIONS, save_transcriptions_switch
        )
        advanced_group.add(save_transcriptions_switch)

        typist_backend_combo = self.create_typist_backend_combo(
            "Typist Backend", SETTING_TYPIST_BACKEND
        )
        advanced_group.add(typist_backend_combo)

        self.add(advanced_group)

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
