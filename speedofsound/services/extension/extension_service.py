"""

We use this service to communicate with the extension. The way we do this is
via GSettings. I'm not sure this is the most idiomatic way to have a GNOME app
communicating with an extension, but I want the extension to eventually be
able to modify application settings anyway, and hence this approach.

"""

from speedofsound.models import OrchestratorStage
from speedofsound.services.base_service import BaseService
from speedofsound.services.configuration import ConfigurationService


class ExtensionService(BaseService):
    SERVICE_NAME = "extension"

    def __init__(self, configuration: ConfigurationService):
        super().__init__(service_name=self.SERVICE_NAME)
        self._configuration = configuration
        self._logger.info("Initialized.")

    def set_app_status(self, stage: OrchestratorStage):
        color = "white"  # Default color (OrchestratorStage.READY)
        if stage == OrchestratorStage.INITIALIZING:
            color = "gray"
        elif stage == OrchestratorStage.RECORDING:
            color = "orange"
        elif stage == OrchestratorStage.TRANSCRIBING:
            color = "purple"
        elif stage == OrchestratorStage.TYPING:
            color = "green"

        self._configuration.extension_status = color
        self._logger.info(f"App status updated: {stage.name}/{color}.")

    def set_app_error(self, error_message: str):
        self._configuration.extension_status = "red"
        self._configuration.extension_error = error_message
        self._logger.error(f"App error updated: {error_message}")

    def shutdown(self):
        pass
