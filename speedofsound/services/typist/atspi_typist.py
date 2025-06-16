"""

Docs:

https://gnome.pages.gitlab.gnome.org/at-spi2-core/libatspi/func.generate_keyboard_event.html

"""

from gi.repository import Atspi  # type: ignore

from speedofsound.models import TypistRequest, TypistResponse
from speedofsound.services.typist.base_typist import BaseTypist


class AtSpiTypist(BaseTypist):
    def __init__(self):
        super().__init__(provider_name="atspi")

        # init/exit doesn’t seem to have any impact fixing the warning we're seeing:
        # (python3:251463): dbind-WARNING **: 09:10:38.357:
        # AT-SPI: Unable to open bus connection: Failed to connect to socket
        # /run/user/1000/at-spi2-0P6U72/socket: No such file or directory

        # result = Atspi.init()
        # if result != 0 and result != 1:  # 0 = success, 1 = already initialized
        #     raise RuntimeError(f"AT-SPI initialization failed with code: {result}")

        self._logger.info("Initialized.")

    def shutdown(self):
        self._logger.info("Shutting down.")
        # result = Atspi.exit()
        # if result != 0:
        #     self._logger.error(f"AT-SPI exit failed with code: {result}")

    def type(self, request: TypistRequest) -> TypistResponse:
        text = request.transcriber_response.get_text()
        self._logger.debug(f"Typing text: {text}")

        try:
            for char in text:
                success = Atspi.generate_keyboard_event(
                    ord(char), None, Atspi.KeySynthType.SYM
                )
                if not success:
                    message = f"Failed to type character: {char}"
                    self._logger.error(message)
                    return TypistResponse(success=False, message=message)
            return TypistResponse(success=True)
        except Exception as e:
            message = f"Error during typing: {str(e)}"
            self._logger.error(message)
            return TypistResponse(success=False, message=message)
