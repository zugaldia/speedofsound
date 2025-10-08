from speedofsound.services.configuration import ConfigurationService
from speedofsound.ui.base_view_model import BaseViewModel


class PreferencesViewModel(BaseViewModel):
    def __init__(self, configuration: ConfigurationService) -> None:
        super().__init__()
        self._configuration = configuration

    def shutdown(self) -> None:
        self._logger.info("Shutting down")
