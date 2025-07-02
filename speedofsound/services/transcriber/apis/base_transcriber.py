from abc import abstractmethod

from speedofsound.models import (
    DEFAULT_LANGUAGE,
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.base_provider import BaseProvider
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.transcriber_prompts import PROMPTS
from speedofsound.utils import get_config_path, is_empty


class BaseTranscriber(BaseProvider):
    """Base class for all transcriber implementations."""

    def __init__(
        self,
        provider_type: TranscriberType,
        configuration_service: ConfigurationService,
    ):
        super().__init__(provider_name=provider_type.value)
        self._provider_type = provider_type
        self._configuration_service = configuration_service

    def get_prompt(self, language_id: str) -> str:
        """Get the prompt for transcription, optionally including custom content."""
        custom_prompt_text = ""
        language_id = DEFAULT_LANGUAGE.id if is_empty(language_id) else language_id
        base_prompt = PROMPTS[language_id]

        custom_prompt_path = get_config_path() / f"prompt_{language_id}.md"
        if custom_prompt_path.exists():
            try:
                custom_prompt_text = custom_prompt_path.read_text().strip()
                self._logger.debug(f"Loaded custom prompt from {custom_prompt_path}")
            except Exception as e:
                self._logger.warning(f"Failed to load custom prompt: {e}")

        return base_prompt.format(CUSTOM_PROMPT=custom_prompt_text)

    @abstractmethod
    def get_name(self) -> str:
        """Get the name of the transcriber."""
        pass

    @abstractmethod
    def get_available_models(self) -> list[TranscriberModel]:
        """Get the list of available models for the provider."""
        pass

    @abstractmethod
    def is_ready(self) -> bool:
        """Check if the transcriber is ready to process requests."""
        pass

    @abstractmethod
    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        """Transcribe audio content from the request."""
        pass
