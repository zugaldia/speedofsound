import logging
from abc import abstractmethod
from gi.repository import GObject


class BaseViewModel(GObject.Object):
    def __init__(self):
        super().__init__()
        self.logger = logging.getLogger(self.__class__.__name__)

    @abstractmethod
    def shutdown(self):
        pass
