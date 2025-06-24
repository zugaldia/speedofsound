import logging
from abc import abstractmethod

from gi.repository import GObject  # type: ignore


class BaseViewModel(GObject.Object):
    def __init__(self):
        super().__init__()
        self._logger = logging.getLogger(self.__class__.__name__)

    @abstractmethod
    def shutdown(self):
        pass
