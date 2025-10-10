from speedofsound.services.configuration import ConfigurationService
from speedofsound.ui.base_view_model import BaseViewModel


class PreferencesViewModel(BaseViewModel):
    def __init__(self, configuration: ConfigurationService) -> None:
        super().__init__()
        self._configuration = configuration

    @property
    def configuration(self) -> ConfigurationService:
        """Get the ConfigurationService instance."""
        return self._configuration

    def shutdown(self) -> None:
        pass
