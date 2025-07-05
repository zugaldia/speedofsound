import typing
from typing import Callable

import pygame
from gi.repository import GLib  # type: ignore

from speedofsound.models import JoystickDevice
from speedofsound.services.base_provider import BaseProvider
from speedofsound.services.configuration import ConfigurationService


class JoystickControl(BaseProvider):
    def __init__(self, configuration: ConfigurationService):
        super().__init__(provider_name="joystick")
        self._configuration = configuration
        self._callback = None
        self._joystick = None
        self._is_running = False

        joystick_id = self._configuration.config.joystick_id
        if joystick_id is None:
            self._logger.info("No joystick ID configured, skipping initialization.")
            return

        pygame.init()
        pygame.joystick.init()
        self._setup_joystick(joystick_id)
        self._logger.info("Initialized.")

    def set_callback(self, callback: Callable):
        self._callback = callback

    def _get_joystick_devices(self) -> typing.List[JoystickDevice]:
        devices = []
        for i in range(pygame.joystick.get_count()):
            device = pygame.joystick.Joystick(i)
            devices.append(
                JoystickDevice(
                    id=device.get_id(),
                    name=device.get_name(),
                )
            )

        return devices

    def _setup_joystick(self, joystick_id: int):
        devices = self._get_joystick_devices()
        names = [device.name for device in devices]
        self._logger.debug(f"Found {len(devices)} joystick device(s): {names}")
        if joystick_id < 0 or joystick_id >= len(devices):
            self._logger.error(
                f"Invalid joystick ID: {joystick_id}. "
                f"Must be between 0 and {len(devices) - 1}."
            )
            return

        self._joystick = pygame.joystick.Joystick(joystick_id)
        self._joystick.init()
        self._is_running = True

        GLib.timeout_add(100, self._poll_joystick)
        self._logger.info(f"Joystick initialized: {self._joystick.get_name()}")

    def _poll_joystick(self):
        if not self._callback:
            return False  # No callback set
        if not self._is_running:
            return False  # Shutdown requested
        for event in pygame.event.get():
            if event.type == pygame.JOYBUTTONDOWN:
                self._callback(event.button)
        return True  # Continue polling

    def shutdown(self):
        self._logger.info("Shutting down.")
        self._is_running = False
        if self._joystick:
            self._joystick.quit()
            pygame.joystick.quit()
            pygame.quit()
