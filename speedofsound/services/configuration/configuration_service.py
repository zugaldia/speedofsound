import tomllib
from pathlib import Path

from pydantic import ValidationError

from speedofsound.constants import CONFIG_FILE
from speedofsound.models import AppConfig
from speedofsound.services.base_service import BaseService


class ConfigurationService(BaseService):
    SERVICE_NAME = "configuration"

    def __init__(self):
        super().__init__(service_name=self.SERVICE_NAME)
        self._config: AppConfig = self._load_configuration()
        self._logger.info("Initialized.")

    def _load_configuration(self) -> AppConfig:
        """Load configuration from config.toml file."""
        config_path = Path(CONFIG_FILE)
        if not config_path.exists():
            raise FileNotFoundError(
                f"Configuration file {CONFIG_FILE} not found. "
                f"Copy config.example.toml to {CONFIG_FILE} to get started."
            )

        try:
            with open(config_path, "rb") as f:
                config_data = tomllib.load(f)
            config = AppConfig(**config_data)
            self._logger.info(f"Configuration loaded from {CONFIG_FILE}")
            return config
        except ValidationError as e:
            raise ValueError(f"Configuration validation error: {e}")
        except Exception as e:
            raise RuntimeError(f"Error loading configuration: {e}")

    @property
    def config(self) -> AppConfig:
        """Get the current configuration."""
        return self._config

    def shutdown(self):
        pass
