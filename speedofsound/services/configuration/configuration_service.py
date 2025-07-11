import tomllib
from pathlib import Path

from pydantic import ValidationError

from speedofsound.constants import CONFIG_FILE
from speedofsound.models import AppConfig
from speedofsound.services.base_service import BaseService
from speedofsound.utils import get_config_path

DEFAULT_CONFIG = """
transcriber = "faster_whisper"

[faster_whisper]
enabled = true
model = "small"
"""


class ConfigurationService(BaseService):
    SERVICE_NAME = "configuration"

    def __init__(self):
        super().__init__(service_name=self.SERVICE_NAME)
        self._config: AppConfig = self._load_configuration()
        self._logger.info("Initialized.")

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

    def shutdown(self):
        pass
