import logging

from gi.repository import Adw, Gio  # type: ignore

from speedofsound.constants import SETTING_COPY_TO_CLIPBOARD
from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageGeneral(Adw.PreferencesPage):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__()
        self._logger = logging.getLogger(__name__)
        self._view_model = view_model

        self.set_title("General")
        self.set_icon_name("preferences-system-symbolic")

        self._build_ui()

        self._logger.info("General preferences page initialized")

    def _build_ui(self) -> None:
        group = Adw.PreferencesGroup()
        group.set_title("Clipboard")
        group.set_description("Configure clipboard behavior")

        clipboard_switch = Adw.SwitchRow()
        clipboard_switch.set_title("Copy to Clipboard")
        clipboard_switch.set_subtitle(
            "Automatically copy transcribed text to clipboard"
        )

        configuration = self._view_model.configuration
        settings = configuration.settings
        if settings is not None and configuration.has_key(SETTING_COPY_TO_CLIPBOARD):
            settings.bind(
                SETTING_COPY_TO_CLIPBOARD,
                clipboard_switch,
                "active",
                Gio.SettingsBindFlags.DEFAULT,
            )
        else:
            clipboard_switch.set_sensitive(False)
            self._logger.warning(
                "GSettings key not available, clipboard switch will not be functional"
            )

        group.add(clipboard_switch)
        self.add(group)
