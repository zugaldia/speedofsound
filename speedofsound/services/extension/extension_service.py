from gi.repository import Gio  # type: ignore

from speedofsound.constants import EXTENSION_SCHEMA
from speedofsound.models import OrchestratorStage
from speedofsound.services.base_service import BaseService

SETTING_APP_STATUS = "app-status"
SETTING_APP_ERROR = "app-error"


class ExtensionService(BaseService):
    SERVICE_NAME = "extension"

    def __init__(self):
        super().__init__(service_name=self.SERVICE_NAME)
        self._settings = None
        self._setup_settings()
        self._logger.info("Initialized.")

    def _setup_settings(self):
        try:
            source = Gio.SettingsSchemaSource.get_default()
            if source is None:
                self._logger.error("No system schema source found.")
                return
            result = source.lookup(schema_id=EXTENSION_SCHEMA, recursive=True)
            if result is None:
                self._logger.error(
                    "Extension schema not found, did you install the extension? "
                    "See https://github.com/zugaldia/speedofsound/blob/main/README.md for instructions."
                )
                return
            self._settings = Gio.Settings.new(schema_id=EXTENSION_SCHEMA)
        except Exception as e:
            self._logger.error(f"Failed to initialize: {e}")

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

        self._settings.set_string(SETTING_APP_STATUS, color)
        self._logger.info(f"App status updated: {stage.name}/{color}.")

    def set_app_error(self, error_message: str):
        if self._settings is None:
            return

        self._settings.set_string(SETTING_APP_STATUS, "red")
        self._settings.set_string(SETTING_APP_ERROR, error_message)
        self._logger.error(f"App error updated: {error_message}")

    def shutdown(self):
        pass
