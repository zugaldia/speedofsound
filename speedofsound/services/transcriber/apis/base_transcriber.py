from abc import abstractmethod

from speedofsound.models import (
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.base_provider import BaseProvider
from speedofsound.services.configuration import ConfigurationService


class BaseTranscriber(BaseProvider):
    """Base class for all transcriber implementations."""

    def __init__(
        self,
        provider_type: TranscriberType,
        configuration: ConfigurationService,
    ):
        super().__init__(provider_name=provider_type.value)
        self._provider_type = provider_type
        self._configuration_service = configuration

    @abstractmethod
    def get_name(self) -> str:
        """Get the name of the transcriber."""
        pass

    @abstractmethod
    def get_available_models(self) -> list[TranscriberModel]:
        """Get the list of available models for the provider."""
        pass

    @abstractmethod
    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        """Transcribe audio content from the request."""
        pass
