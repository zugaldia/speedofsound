"""

Context is king for the latest generation of AI models.

By providing the right information, the transcribers can produce substantially
better results, to a point where most transcriptions don't require any editing
to be accepted. This service is in charge of generating this information.

Currently, it does three things:
1. Provides a generic system prompt.
2. Manages an (optional) user-provided prompt.
3. Detects the active application to adapt the transcription to it.

Other things that could be added in the future:
- Capture a screenshot and use it as context.
- Memory of the conversation.

"""

from pathlib import Path
from typing import Optional

from speedofsound.services.base_service import BaseService
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.context.atspi_client import AtspiClient
from speedofsound.services.context.prompts import (
    APPLICATION_PROMPT,
    SIMPLE_PROMPT,
    SYSTEM_PROMPT,
)
from speedofsound.utils import get_config_path


class ContextService(BaseService):
    SERVICE_NAME = "context"

    def __init__(self, configuration: ConfigurationService):
        super().__init__(service_name=self.SERVICE_NAME)
        self._configuration = configuration
        self._atspi_client = None
        self._simple_prompt_cache = {}
        self._system_prompt_cache = {}
        if self._configuration.include_application_name:
            self._atspi_client = AtspiClient()
            self._logger.info("Initialized with ATSPI client.")

    def shutdown(self):
        pass

    def update_active_app(self):
        """Update the active application."""
        if self._atspi_client is None:
            return
        try:
            self._atspi_client.update_active_app()
        except Exception as e:
            self._logger.error(f"Error updating active app: {e}")

    def _get_custom_simple_prompt_path(self, language_id: str) -> Path:
        """Get the path to the custom prompt file for the given language."""
        return get_config_path() / f"prompt_simple_{language_id}.txt"

    def _get_custom_system_prompt_path(self, language_id: str) -> Path:
        """Get the path to the custom prompt file for the given language."""
        return get_config_path() / f"prompt_{language_id}.txt"

    def get_simple_prompt(self, language_id: str) -> str:
        if language_id in self._simple_prompt_cache:
            return self._simple_prompt_cache[language_id]

        custom_simple_prompt_path = self._get_custom_simple_prompt_path(language_id)
        if custom_simple_prompt_path.exists():
            try:
                simple_prompt = custom_simple_prompt_path.read_text().strip()
                self._simple_prompt_cache[language_id] = simple_prompt
                self._logger.info(f"Loaded {custom_simple_prompt_path}.")
                return simple_prompt
            except Exception as e:
                self._logger.warning(f"Failed to load custom simple prompt: {e}")
                self._simple_prompt_cache[language_id] = None

        simple_prompt = SIMPLE_PROMPT.get(language_id, SIMPLE_PROMPT["default"])
        return simple_prompt.strip()

    def _get_system_prompt(self, language_id: str) -> Optional[str]:
        if language_id in self._system_prompt_cache:
            return self._system_prompt_cache[language_id]

        system_prompt_path = self._get_custom_system_prompt_path(language_id)
        if system_prompt_path.exists():
            try:
                prompt = system_prompt_path.read_text().strip()
                self._system_prompt_cache[language_id] = prompt
                self._logger.info(f"Loaded {system_prompt_path}.")
                return prompt
            except Exception as e:
                self._logger.warning(f"Failed to load custom prompt: {e}")
                self._system_prompt_cache[language_id] = None

        prompt = SYSTEM_PROMPT.get(language_id, SYSTEM_PROMPT["default"])
        return prompt.strip()

    def _get_application_prompt(self, language_id: str) -> Optional[str]:
        if self._atspi_client is None or self._atspi_client.active_app is None:
            return None

        app_prompt = APPLICATION_PROMPT.get(language_id, APPLICATION_PROMPT["default"])
        return app_prompt.format(
            application_name=self._atspi_client.active_app.application_name,
            window_title=self._atspi_client.active_app.window_name,
        )

    def get_prompt(self, language_id: str) -> str:
        """Get the prompt for transcription, optionally including custom content."""
        system_prompt = self._get_system_prompt(language_id) or ""
        application_prompt = self._get_application_prompt(language_id) or ""
        return f"{system_prompt}\n{application_prompt}".strip()
