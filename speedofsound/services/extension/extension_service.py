"""

We use this service to communicate with the extension. The way we do this is
via GSettings. I'm not sure this is the most idiomatic way to have a GNOME app
communicating with an extension, but I want the extension to eventually be
able to modify application settings anyway, and hence this approach.

"""

from typing import Optional

from gi.repository import Gio  # type: ignore

from speedofsound.constants import SETTING_EXT_ERROR, SETTING_EXT_STATUS
from speedofsound.models import OrchestratorStage
from speedofsound.services.base_service import BaseService


class ExtensionService(BaseService):
    SERVICE_NAME = "extension"

    def __init__(self, settings: Optional[Gio.Settings] = None):
        super().__init__(service_name=self.SERVICE_NAME)
        self._settings = settings
        self._logger.info("Initialized.")

    def set_app_status(self, stage: OrchestratorStage):
        if self._settings is None:
            return

        color = "white"  # Default color (OrchestratorStage.READY)
        if stage == OrchestratorStage.INITIALIZING:
            color = "gray"
        elif stage == OrchestratorStage.RECORDING:
            color = "orange"
        elif stage == OrchestratorStage.TRANSCRIBING:
            color = "purple"
        elif stage == OrchestratorStage.TYPING:
            color = "green"

        self._settings.set_string(SETTING_EXT_STATUS, color)
        self._logger.info(f"App status updated: {stage.name}/{color}.")

    def set_app_error(self, error_message: str):
        if self._settings is None:
            return

        self._settings.set_string(SETTING_EXT_STATUS, "red")
        self._settings.set_string(SETTING_EXT_ERROR, error_message)
        self._logger.error(f"App error updated: {error_message}")

    def shutdown(self):
        pass
