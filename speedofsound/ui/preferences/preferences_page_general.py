from gi.repository import Adw  # type: ignore

from speedofsound.constants import (
    SETTING_COPY_TO_CLIPBOARD,
    SETTING_LANGUAGE,
    SETTING_MICROPHONE_DEVICE,
)
from speedofsound.ui.preferences.preferences_page_base import PreferencesPageBase
from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel
from speedofsound.utils import get_cache_path, get_config_path, get_data_path


class PreferencesPageGeneral(PreferencesPageBase):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__(view_model)
        self.set_title("General")
        self.set_icon_name("preferences-system-symbolic")
        self._build_ui()

    def _build_ui(self) -> None:
        general_group = Adw.PreferencesGroup()
        general_group.set_title("General")
        general_group.set_description("Configure general settings")

        microphone_combo = self.create_microphone_combo(
            "Microphone Device", SETTING_MICROPHONE_DEVICE
        )
        general_group.add(microphone_combo)

        language_combo = self.create_language_combo(
            "Transcription Language", SETTING_LANGUAGE
        )
        general_group.add(language_combo)

        clipboard_switch = Adw.SwitchRow()
        clipboard_switch.set_title("Copy to Clipboard")
        clipboard_switch.set_subtitle(
            "Copy transcription to clipboard for manual pasting in unsupported applications"
        )
        self.bind_boolean_setting(SETTING_COPY_TO_CLIPBOARD, clipboard_switch)
        general_group.add(clipboard_switch)

        self.add(general_group)

        paths_group = Adw.PreferencesGroup()
        paths_group.set_title("Storage Locations")
        paths_group.set_description("Local storage paths used by the application")

        cache_path_row = Adw.ActionRow()
        cache_path_row.set_title("Cache Directory")
        cache_path_row.set_subtitle(
            f"{get_cache_path()}\nTemporary audio files during recording and transcription"
        )
        cache_path_row.set_selectable(True)
        paths_group.add(cache_path_row)

        config_path_row = Adw.ActionRow()
        config_path_row.set_title("Configuration Directory")
        config_path_row.set_subtitle(
            f"{get_config_path()}\nCustom prompts (e.g., prompt_en.md)"
        )
        config_path_row.set_selectable(True)
        paths_group.add(config_path_row)

        data_path_row = Adw.ActionRow()
        data_path_row.set_title("Data Directory")
        data_path_row.set_subtitle(
            f"{get_data_path()}\nFaster Whisper models and benchmark recordings"
        )
        data_path_row.set_selectable(True)
        paths_group.add(data_path_row)

        self.add(paths_group)
