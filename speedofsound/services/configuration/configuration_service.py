import tomllib
from pathlib import Path
from typing import List, Optional

from gi.repository import Gio  # type: ignore
from pydantic import ValidationError

from speedofsound.constants import (
    APPLICATION_ID,
    CONFIG_FILE,
    DEFAULT_COPY_TO_CLIPBOARD,
    DEFAULT_EXT_ERROR,
    DEFAULT_EXT_STATUS,
    DEFAULT_JOYSTICK_ID,
    DEFAULT_JOYSTICK_LANGUAGE_LEFT,
    DEFAULT_JOYSTICK_LANGUAGE_RIGHT,
    DEFAULT_LANGUAGE,
    DEFAULT_RECORDING_TIMEOUT_SECONDS,
    DEFAULT_SAVE_TRANSCRIPTIONS,
    SETTING_COPY_TO_CLIPBOARD,
    SETTING_EXT_ERROR,
    SETTING_EXT_STATUS,
    SETTING_JOYSTICK_ID,
    SETTING_JOYSTICK_LANGUAGE_LEFT,
    SETTING_JOYSTICK_LANGUAGE_RIGHT,
    SETTING_LANGUAGE,
    SETTING_RECORDING_TIMEOUT_SECONDS,
    SETTING_SAVE_TRANSCRIPTIONS,
)
from speedofsound.models import AppConfig, JoystickDevice
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
        self._available_joystick_devices: List[JoystickDevice] = []
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

    def _get_boolean(self, key: str, default: bool) -> bool:
        """Get a boolean setting from GSettings or fallback to default."""
        if self._settings is not None and self.has_key(key):
            return self._settings.get_boolean(key)
        return default

    def _get_string(self, key: str, default: str) -> str:
        """Get a string setting from GSettings or fallback to default."""
        if self._settings is not None and self.has_key(key):
            return self._settings.get_string(key)
        return default

    def _set_string(self, key: str, value: str) -> None:
        """Set a string setting in GSettings."""
        if self._settings is not None and self.has_key(key):
            self._settings.set_string(key, value)

    def _get_int(self, key: str, default: int) -> int:
        """Get an integer setting from GSettings or fallback to default."""
        if self._settings is not None and self.has_key(key):
            return self._settings.get_int(key)
        return default

    @property
    def copy_to_clipboard(self) -> bool:
        """Get the copy to clipboard setting from GSettings or fallback to default constant."""
        return self._get_boolean(SETTING_COPY_TO_CLIPBOARD, DEFAULT_COPY_TO_CLIPBOARD)

    @property
    def extension_status(self) -> str:
        """Get the extension status indicator."""
        return self._get_string(SETTING_EXT_STATUS, DEFAULT_EXT_STATUS)

    @extension_status.setter
    def extension_status(self, status: str) -> None:
        """Set the extension status indicator."""
        self._set_string(SETTING_EXT_STATUS, status)

    @property
    def extension_error(self) -> str:
        """Get the extension error message."""
        return self._get_string(SETTING_EXT_ERROR, DEFAULT_EXT_ERROR)

    @extension_error.setter
    def extension_error(self, error: str) -> None:
        """Set the extension error message."""
        self._set_string(SETTING_EXT_ERROR, error)

    @property
    def save_transcriptions(self) -> bool:
        """Get the save transcriptions setting from GSettings or fallback to default constant."""
        return self._get_boolean(
            SETTING_SAVE_TRANSCRIPTIONS, DEFAULT_SAVE_TRANSCRIPTIONS
        )

    @property
    def recording_timeout_seconds(self) -> int:
        """Get the recording timeout setting from GSettings or fallback to default constant."""
        return self._get_int(
            SETTING_RECORDING_TIMEOUT_SECONDS, DEFAULT_RECORDING_TIMEOUT_SECONDS
        )

    @property
    def language(self) -> str:
        """Get the language setting from GSettings or fallback to default constant."""
        return self._get_string(SETTING_LANGUAGE, DEFAULT_LANGUAGE)

    @language.setter
    def language(self, language: str) -> None:
        """Set the language setting."""
        self._set_string(SETTING_LANGUAGE, language)

    @property
    def joystick_id(self) -> int:
        """Get the joystick ID setting from GSettings or fallback to default constant."""
        return self._get_int(SETTING_JOYSTICK_ID, DEFAULT_JOYSTICK_ID)

    @property
    def joystick_language_left(self) -> str:
        """Get the joystick left button language from GSettings or fallback to default constant."""
        return self._get_string(
            SETTING_JOYSTICK_LANGUAGE_LEFT, DEFAULT_JOYSTICK_LANGUAGE_LEFT
        )

    @property
    def joystick_language_right(self) -> str:
        """Get the joystick right button language from GSettings or fallback to default constant."""
        return self._get_string(
            SETTING_JOYSTICK_LANGUAGE_RIGHT, DEFAULT_JOYSTICK_LANGUAGE_RIGHT
        )

    @property
    def available_joystick_devices(self) -> List[JoystickDevice]:
        """Get the list of available joystick devices detected at runtime."""
        return self._available_joystick_devices

    def set_available_joystick_devices(self, devices: List[JoystickDevice]) -> None:
        """Set the list of available joystick devices."""
        self._available_joystick_devices = devices
        self._logger.info(f"Updated available joystick devices: {len(devices)} found.")

    def shutdown(self):
        pass
