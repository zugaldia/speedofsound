from abc import abstractmethod

from speedofsound.models import TypistRequest, TypistResponse
from speedofsound.services.base_provider import BaseProvider


class BaseTypist(BaseProvider):
    @abstractmethod
    def type(self, request: TypistRequest) -> TypistResponse:
        pass
