from concurrent.futures import Future, ThreadPoolExecutor

from gi.repository import Gdk, GObject  # type: ignore

from speedofsound.constants import TYPIST_RESPONSE_SIGNAL
from speedofsound.models import (
    DisplayServer,
    TypistBackend,
    TypistRequest,
    TypistResponse,
)
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

    def __init__(self, configuration: ConfigurationService):
        super().__init__(service_name=self.SERVICE_NAME)
        self._configuration = configuration
        self._executor = ThreadPoolExecutor(max_workers=1)
        self._typist: BaseTypist = self._get_typist()
        self._logger.info("Initialized.")

    def shutdown(self):
        self._logger.info("Shutting down.")
        self._executor.shutdown(wait=True)
        self._typist.shutdown()

    def _detect_display_server(self) -> DisplayServer:
        """Detect if running on X11 or Wayland using GTK/GDK."""
        try:
            display = Gdk.Display.get_default()
            if display is None:
                self._logger.warning("No display found.")
                return DisplayServer.UNKNOWN

            # Replace with a type check, but GdkWayland isn't found
            display_type = str(type(display))
            if "Wayland" in display_type:
                return DisplayServer.WAYLAND
            elif "X11" in display_type:
                return DisplayServer.X11
            else:
                self._logger.warning(f"Unknown display type: {display_type}")
                return DisplayServer.UNKNOWN
        except Exception as e:
            self._logger.warning(f"Failed to detect display server: {e}")
            return DisplayServer.UNKNOWN

    def _get_typist(self) -> BaseTypist:
        typist_backend = self._configuration.typist_backend

        if typist_backend == "auto":
            # Auto-detect based on display server
            display_server = self._detect_display_server()
            if display_server == DisplayServer.WAYLAND:
                backend = TypistBackend.YDOTOOL
                self._logger.info("Detected Wayland, using ydotool backend.")
            elif display_server == DisplayServer.X11:
                backend = TypistBackend.XDOTOOL
                self._logger.info("Detected X11, using xdotool backend.")
            else:
                # Default to xdotool for unknown
                backend = TypistBackend.XDOTOOL
                self._logger.info(
                    "Unknown display server, defaulting to xdotool backend."
                )
        else:
            # User override - respect their choice
            backend = TypistBackend(typist_backend)
            self._logger.info(f"Using user-configured backend: {backend}")

        if backend == TypistBackend.ATSPI:
            return AtSpiTypist()
        elif backend == TypistBackend.XDOTOOL:
            return XdotoolTypist()
        elif backend == TypistBackend.YDOTOOL:
            return YdotoolTypist()
        else:
            self._logger.warning(f"Unknown backend {backend}, defaulting to xdotool.")
            return XdotoolTypist()

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
