from concurrent.futures import Future, ThreadPoolExecutor

from gi.repository import GObject  # type: ignore

from speedofsound.constants import TYPIST_RESPONSE_SIGNAL
from speedofsound.models import TypistRequest, TypistResponse
from speedofsound.services.base_service import BaseService
from speedofsound.services.typist.xdotool_typist import XdotoolTypist


class TypistService(BaseService):
    SERVICE_NAME = "typist"

    __gsignals__ = {
        TYPIST_RESPONSE_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
    }

    def __init__(self):
        super().__init__(service_name=self.SERVICE_NAME)
        self._executor = ThreadPoolExecutor(max_workers=1)

        # TODO: Atspi alternative?
        # https://docs.gtk.org/atspi2/func.generate_keyboard_event.html
        self._typist = XdotoolTypist()
        self._logger.info("Initialized.")

    def shutdown(self):
        self._logger.info("Shutting down.")
        self._executor.shutdown(wait=True)
        self._typist.shutdown()

    def type_async(self, request: TypistRequest):
        self._logger.info("Typing.")
        future = self._executor.submit(self._typist.type, request)
        future.add_done_callback(self._handle_typing_result)

    def _handle_typing_result(self, future: Future):
        try:
            result: TypistResponse = future.result()
            self.safe_emit(TYPIST_RESPONSE_SIGNAL, result.model_dump_json())
        except Exception as e:
            self._logger.error(f"Error during typing: {e}")
            response = TypistResponse(
                success=False, message=f"Error during typing: {str(e)}"
            )
            self.safe_emit(TYPIST_RESPONSE_SIGNAL, response.model_dump_json())
