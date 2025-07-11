"""

In order to maximize support across X11 and Wayland we need to use the kernel
uinput module under the hood. However, because this module is quite low level,
we are interfacing with it at a higher layer of abstraction.

This class relies on pynput, which in turn it uses evdev (event device library),
which in turn uses the kernel uinput interface. If eventually we need more
control over the input interface, we could also use the Python libraries for
evdev directly. There are two main ones to choose from:

https://python-libevdev.readthedocs.io/en/latest/python-evdev.html

pynput uses evdev, while libevdev seems to be the preferred library by the
Free Desktop project and originally developed by Red Hat.

"""

import os
from time import sleep

from pynput.keyboard import Controller

from speedofsound.models import PynputConfig, TypistRequest, TypistResponse
from speedofsound.services.typist.base_typist import BaseTypist


class PynputTypist(BaseTypist):
    def __init__(self, config: PynputConfig):
        super().__init__(provider_name="pynput")
        self._config = config

        if self._config.backend:
            # Set backend if specified otherwise let pynput decide
            os.environ["PYNPUT_BACKEND"] = self._config.backend

        self._keyboard = Controller()
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def type(self, request: TypistRequest) -> TypistResponse:
        text = request.transcriber_response.get_text()
        self._logger.debug(f"Typing text: {text}")
        try:
            # Other typists don't need this delay to function, but for some
            # reason pynput needs it. A default of 200 ms seems to work, but
            # it's configurable in case different setups need different values.
            sleep(self._config.delay)
            self._keyboard.type(text)
            return TypistResponse(success=True)
        except Exception as e:
            message = f"Error typing text: {e}"
            self._logger.error(message)
            return TypistResponse(success=False, message=message)
