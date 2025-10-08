from gi.repository import Adw, Gtk  # type: ignore

from speedofsound.constants import SETTING_COPY_TO_CLIPBOARD
from speedofsound.languages import LANGUAGES
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

        language_combo = Adw.ComboRow()
        language_combo.set_title("Transcription Language")

        string_list = Gtk.StringList()
        for language_name in LANGUAGES.keys():
            string_list.append(language_name)

        language_combo.set_model(string_list)

        language_combo.connect("notify::selected", self._on_language_changed)

        current_language = self._view_model.configuration.language
        for i, (language_name, language_code) in enumerate(LANGUAGES.items()):
            if language_code == current_language:
                language_combo.set_selected(i)
                language_combo.set_subtitle(f"{language_name} ({language_code})")
                break

        self._language_combo = language_combo
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

    def _on_language_changed(self, combo_row, _pspec) -> None:
        """Handle language selection change."""
        selected_index = combo_row.get_selected()
        language_name = list(LANGUAGES.keys())[selected_index]
        language_code = list(LANGUAGES.values())[selected_index]
        combo_row.set_subtitle(f"{language_name} ({language_code})")
        self._view_model.configuration.language = language_code
        self._logger.info(f"Language changed to: {language_code}")
