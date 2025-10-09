from gi.repository import Adw  # type: ignore

from speedofsound.constants import SETTING_COPY_TO_CLIPBOARD, SETTING_LANGUAGE
from speedofsound.ui.preferences.preferences_page_base import PreferencesPageBase
from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageGeneral(PreferencesPageBase):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__(view_model)
        self.set_title("General")
        self.set_icon_name("preferences-system-symbolic")
        self._build_ui()
        self._logger.info("General preferences page initialized")

    def _build_ui(self) -> None:
        language_group = Adw.PreferencesGroup()
        language_group.set_title("Language")
        language_group.set_description("Configure transcription language")

        language_combo = self.create_language_combo(
            "Transcription Language", SETTING_LANGUAGE
        )
        language_group.add(language_combo)
        self.add(language_group)

        clipboard_group = Adw.PreferencesGroup()
        clipboard_group.set_title("Clipboard")
        clipboard_group.set_description("Configure clipboard behavior")

        clipboard_switch = Adw.SwitchRow()
        clipboard_switch.set_title("Copy to Clipboard")
        clipboard_switch.set_subtitle(
            "Automatically copy transcribed text to clipboard"
        )

        self.bind_boolean_setting(SETTING_COPY_TO_CLIPBOARD, clipboard_switch)

        clipboard_group.add(clipboard_switch)
        self.add(clipboard_group)
