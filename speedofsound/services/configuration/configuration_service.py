import tomllib
from pathlib import Path
from typing import Optional

from gi.repository import Gio  # type: ignore
from pydantic import ValidationError

from speedofsound.constants import (
    APPLICATION_ID,
    CONFIG_FILE,
    DEFAULT_COPY_TO_CLIPBOARD,
    DEFAULT_EXT_ERROR,
    DEFAULT_EXT_STATUS,
    SETTING_COPY_TO_CLIPBOARD,
    SETTING_EXT_ERROR,
    SETTING_EXT_STATUS,
)
from speedofsound.models import AppConfig
from speedofsound.services.base_service import BaseService
from speedofsound.utils import get_config_path

DEFAULT_CONFIG = """
transcriber = "faster_whisper"

[faster_whisper]
enabled = true
model = "small"
""".strip()


class ConfigurationService(BaseService):
    SERVICE_NAME = "configuration"

    def __init__(self):
        super().__init__(service_name=self.SERVICE_NAME)
        self._schema: Optional[Gio.SettingsSchema] = None
        self._settings: Optional[Gio.Settings] = self._initialize_settings()
        self._config: AppConfig = self._load_configuration()
        self._logger.info(
            f"Initialized (GSettings available: {self._settings is not None})."
        )

    def _load_configuration(self) -> AppConfig:
        """Load configuration from config.toml file."""
        config_path = get_config_path() / CONFIG_FILE
        if not config_path.exists():
            self._create_default_config(config_path)

        try:
            with open(config_path, "rb") as f:
                config_data = tomllib.load(f)
            config = AppConfig(**config_data)
            self._logger.info(f"Configuration loaded from {config_path}")
            return config
        except ValidationError as e:
            raise ValueError(f"Configuration validation error: {e}")
        except Exception as e:
            raise RuntimeError(f"Error loading configuration: {e}")

    def _initialize_settings(self) -> Optional[Gio.Settings]:
        """Initialize GSettings for the application."""
        try:
            source = Gio.SettingsSchemaSource.get_default()
            if source is None:
                self._logger.error("System source schema not found.")
                return None
            schema = source.lookup(schema_id=APPLICATION_ID, recursive=True)
            if schema is None:
                self._logger.error("Application schema not found.")
                return None
            self._schema = schema
            return Gio.Settings.new(schema_id=APPLICATION_ID)
        except Exception as e:
            self._logger.error(f"Failed to initialize settings: {e}")
            return None

    def _create_default_config(self, config_path: Path) -> None:
        """Create default configuration by copying from example file."""
        self._logger.warning(f"Config file not found at {config_path}.")
        self._logger.info("Creating default config using (local) Faster Whisper.")

        try:
            with open(config_path, "w") as f:
                f.write(DEFAULT_CONFIG)
        except Exception as e:
            raise RuntimeError(f"Failed to create default configuration: {e}")

    @property
    def config(self) -> AppConfig:
        """Get the current configuration."""
        return self._config

    @property
    def settings(self) -> Optional[Gio.Settings]:
        """Get the GSettings instance."""
        return self._settings

    def has_key(self, key: str) -> bool:
        """Check if a key exists in the schema."""
        if self._schema is None:
            return False
        return self._schema.has_key(key)

    @property
    def copy_to_clipboard(self) -> bool:
        """Get the copy to clipboard setting from GSettings or fallback to default constant."""
        if self._settings is not None and self.has_key(SETTING_COPY_TO_CLIPBOARD):
            return self._settings.get_boolean(SETTING_COPY_TO_CLIPBOARD)
        return DEFAULT_COPY_TO_CLIPBOARD

    @property
    def extension_status(self) -> str:
        """Get the extension status indicator."""
        if self._settings is not None and self.has_key(SETTING_EXT_STATUS):
            return self._settings.get_string(SETTING_EXT_STATUS)
        return DEFAULT_EXT_STATUS

    @extension_status.setter
    def extension_status(self, status: str) -> None:
        """Set the extension status indicator."""
        if self._settings is not None and self.has_key(SETTING_EXT_STATUS):
            self._settings.set_string(SETTING_EXT_STATUS, status)

    @property
    def extension_error(self) -> str:
        """Get the extension error message."""
        if self._settings is not None and self.has_key(SETTING_EXT_ERROR):
            return self._settings.get_string(SETTING_EXT_ERROR)
        return DEFAULT_EXT_ERROR

    @extension_error.setter
    def extension_error(self, error: str) -> None:
        """Set the extension error message."""
        if self._settings is not None and self.has_key(SETTING_EXT_ERROR):
            self._settings.set_string(SETTING_EXT_ERROR, error)

    def shutdown(self):
        pass
