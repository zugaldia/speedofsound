from gi.repository import Adw  # type: ignore

from speedofsound.constants import APPLICATION_NAME
from speedofsound.ui.preferences import (
    PreferencesPageAdvanced,
    PreferencesPageAsr,
    PreferencesPageGeneral,
    PreferencesViewModel,
)


class PreferencesWindow(Adw.PreferencesDialog):
    def __init__(
        self,
        view_model: PreferencesViewModel,
    ) -> None:
        super().__init__()
        self.set_title(APPLICATION_NAME)
        self._view_model = view_model

        general_page = PreferencesPageGeneral(view_model=view_model)
        self.add(general_page)

        asr_page = PreferencesPageAsr(view_model=view_model)
        self.add(asr_page)

        advanced_page = PreferencesPageAdvanced(view_model=view_model)
        self.add(advanced_page)
