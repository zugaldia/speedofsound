from gi.repository import GObject  # type: ignore

from speedofsound.constants import CONTROL_EVENT_SIGNAL
from speedofsound.models import ControlEvent, JoystickButton
from speedofsound.services.base_service import BaseService
from speedofsound.services.control.joystick_control import JoystickControl


class ControlService(BaseService):
    SERVICE_NAME = "control"

    __gsignals__ = {
        CONTROL_EVENT_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
    }

    def __init__(self, joystick_control: JoystickControl):
        super().__init__(service_name=self.SERVICE_NAME)
        self._control = joystick_control
        self._control.set_callback(self._joystick_button_down_callback)
        self._logger.info("Initialized.")

    def _joystick_button_down_callback(self, button_id: int):
        button = JoystickButton(button_id)
        self._logger.info(f"Button {button.name} pressed.")
        event = ControlEvent(button=button)
        self.safe_emit(CONTROL_EVENT_SIGNAL, event.model_dump_json())

    def shutdown(self):
        pass
