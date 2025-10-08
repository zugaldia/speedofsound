from speedofsound.ui.preferences.preferences_page_base import PreferencesPageBase
from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageAsr(PreferencesPageBase):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__(view_model)
        self.set_title("Speech Recognition")
        self.set_icon_name("audio-input-microphone-symbolic")
        self._logger.info("Speech Recognition preferences page initialized")
