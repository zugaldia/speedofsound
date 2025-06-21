import os
from concurrent.futures import Future, ThreadPoolExecutor

from gi.repository import GObject  # type: ignore

from speedofsound.constants import TYPIST_RESPONSE_SIGNAL
from speedofsound.models import TypistBackend, TypistRequest, TypistResponse
from speedofsound.services.base_service import BaseService
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.typist.atspi_typist import AtSpiTypist
from speedofsound.services.typist.base_typist import BaseTypist
from speedofsound.services.typist.xdotool_typist import XdotoolTypist
from speedofsound.services.typist.ydotool_typist import YdotoolTypist


class TypistService(BaseService):
    SERVICE_NAME = "typist"

    __gsignals__ = {
        TYPIST_RESPONSE_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
    }

    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(service_name=self.SERVICE_NAME)
        self._configuration_service = configuration_service
        self._executor = ThreadPoolExecutor(max_workers=1)
        self._typist: BaseTypist = self._get_typist()
        self._logger.info("Initialized.")

    def shutdown(self):
        self._logger.info("Shutting down.")
        self._executor.shutdown(wait=True)
        self._typist.shutdown()

    def _get_display_server(self) -> str:
        """Detect the display server type (x11, wayland, or unknown)."""
        session_type = os.environ.get("XDG_SESSION_TYPE", "").lower()
        self._logger.info(f"Detected session type: {session_type}")
        if session_type in ["x11", "wayland"]:
            return session_type
        return "unknown"

    def _get_default_backend(self) -> TypistBackend:
        """Get the default typist backend based on the display server."""
        display_server = self._get_display_server()
        if display_server == "x11":
            return TypistBackend.XDOTOOL
        elif display_server == "wayland":
            return TypistBackend.YDOTOOL
        else:
            self._logger.warning("Unknown display server, defaulting to AT-SPI.")
            return TypistBackend.ATSPI

    def _get_typist(self) -> BaseTypist:
        config = self._configuration_service.config
        backend = (
            TypistBackend(config.typist_backend)
            if config.typist_backend
            else self._get_default_backend()
        )

        if backend == TypistBackend.XDOTOOL:
            self._logger.info("Using xdotool backend.")
            return XdotoolTypist()
        elif backend == TypistBackend.YDOTOOL:
            self._logger.info("Using ydotool backend.")
            return YdotoolTypist()
        elif backend == TypistBackend.ATSPI:
            self._logger.info("Using AT-SPI backend.")
            return AtSpiTypist()
        else:
            self._logger.error(f"Unsupported backend: {backend}")
            raise ValueError(f"Unsupported backend: {backend}")

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
