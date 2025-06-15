import logging
from abc import abstractmethod


class BaseProvider:
    def __init__(self, provider_name: str):
        super().__init__()
        self._provider_name = provider_name
        self._logger = logging.getLogger(provider_name)

    @property
    def provider_name(self) -> str:
        """Get the name of the provider."""
        return self._provider_name

    @abstractmethod
    def shutdown(self):
        pass
