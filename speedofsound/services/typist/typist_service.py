from concurrent.futures import Future, ThreadPoolExecutor

from gi.repository import GObject  # type: ignore

from speedofsound.constants import TYPIST_RESPONSE_SIGNAL
from speedofsound.models import TypistBackend, TypistRequest, TypistResponse
from speedofsound.services.base_service import BaseService
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.typist.atspi_typist import AtSpiTypist
from speedofsound.services.typist.base_typist import BaseTypist
from speedofsound.services.typist.pynput_typist import PynputTypist
from speedofsound.services.typist.xdotool_typist import XdotoolTypist
from speedofsound.services.typist.ydotool_typist import YdotoolTypist


class TypistService(BaseService):
    SERVICE_NAME = "typist"

    __gsignals__ = {
        TYPIST_RESPONSE_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
    }

    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(service_name=self.SERVICE_NAME)
        self._configuration = configuration_service
        self._executor = ThreadPoolExecutor(max_workers=1)
        self._typist: BaseTypist = self._get_typist()
        self._logger.info("Initialized.")

    def shutdown(self):
        self._logger.info("Shutting down.")
        self._executor.shutdown(wait=True)
        self._typist.shutdown()

    def _get_typist(self) -> BaseTypist:
        config = self._configuration.config
        backend = (
            TypistBackend(config.typist_backend)
            if config.typist_backend
            else TypistBackend.PYNPUT
        )

        if backend == TypistBackend.ATSPI:
            self._logger.info("Using AT-SPI backend.")
            return AtSpiTypist()
        elif backend == TypistBackend.XDOTOOL:
            self._logger.info("Using xdotool backend.")
            return XdotoolTypist()
        elif backend == TypistBackend.YDOTOOL:
            self._logger.info("Using ydotool backend.")
            return YdotoolTypist()
        else:
            self._logger.info("Defaulting to pynput backend.")
            return PynputTypist(config.pynput)

    def type_async(self, request: TypistRequest):
        self._logger.info("Typing.")
        future = self._executor.submit(self._typist.type, request)
        future.add_done_callback(self._handle_typing_result)

    def _handle_typing_result(self, future: Future):
        try:
            result: TypistResponse = future.result()
            self.safe_emit(TYPIST_RESPONSE_SIGNAL, result.model_dump_json())
        except Exception as e:
            message = f"Error during typing: {str(e)}"
            self._logger.error(message)
            response = TypistResponse(success=False, message=message)
            self.safe_emit(TYPIST_RESPONSE_SIGNAL, response.model_dump_json())
