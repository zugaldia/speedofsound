from gi.repository import Adw  # type: ignore

from speedofsound.constants import (
    SETTING_RECORDING_TIMEOUT_SECONDS,
    SETTING_SAVE_TRANSCRIPTIONS,
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
