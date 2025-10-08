import logging

from gi.repository import Adw  # type: ignore

from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageAdvanced(Adw.PreferencesPage):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__()
        self._logger = logging.getLogger(__name__)
        self._view_model = view_model

        self.set_title("Advanced")
        self.set_icon_name("applications-engineering-symbolic")

        self._logger.info("Advanced preferences page initialized")
