import logging

import gi

from speedofsound.services.configuration.configuration_service import (
    ConfigurationService,
)
from speedofsound.services.control.control_service import ControlService
from speedofsound.services.control.joystick_control import JoystickControl
from speedofsound.services.orchestrator.orchestrator_service import OrchestratorService
from speedofsound.services.recorder.pyaudio_recorder import PyAudioRecorder
from speedofsound.services.recorder.recorder_service import RecorderService
from speedofsound.services.transcriber.transcriber_service import TranscriberService
from speedofsound.services.typist.typist_service import TypistService

gi.require_version("Gtk", "4.0")
gi.require_version("Adw", "1")

from gi.repository import Adw, Gio  # type: ignore  # noqa: E402

from speedofsound.constants import APPLICATION_ID, LOG_FILE  # noqa: E402
from speedofsound.ui.main.main_view_model import MainViewModel  # noqa: E402
from speedofsound.ui.main.main_window import MainWindow  # noqa: E402


class SosApplication(Adw.Application):
    def __init__(self):
        super().__init__(
            application_id=APPLICATION_ID,
            flags=Gio.ApplicationFlags.DEFAULT_FLAGS,
        )

        self._setup_logging()
        self._logger = logging.getLogger(__name__)
        self._logger.info("Initialized.")

    def _setup_logging(self):
        """Setup logging to both console and file."""
        root_logger = logging.getLogger()
        root_logger.setLevel(logging.DEBUG)
        root_logger.handlers.clear()

        # Console formatter - simpler for interactive use
        console_formatter = logging.Formatter(fmt="%(levelname)s %(name)s: %(message)s")

        # File formatter - more detailed for LLM debugging/troubleshooting
        file_formatter = logging.Formatter(
            fmt="%(asctime)s - %(name)s - %(levelname)s - %(funcName)s:%(lineno)d - %(message)s",
            datefmt="%Y-%m-%d %H:%M:%S",
        )

        # Console handler
        console_handler = logging.StreamHandler()
        console_handler.setLevel(logging.INFO)
        console_handler.setFormatter(console_formatter)
        root_logger.addHandler(console_handler)

        # File handler - Not only we log more content to this handler, we have
        # it on a file so that we can feed it to the LLM for troubleshooting
        # if anything goes wrong.
        file_handler = logging.FileHandler(LOG_FILE, mode="w")
        file_handler.setLevel(logging.DEBUG)
        file_handler.setFormatter(file_formatter)
        root_logger.addHandler(file_handler)

    def _setup_services_di(self):
        self._configuration_service = ConfigurationService()

        self._joystick_control = JoystickControl(
            configuration_service=self._configuration_service
        )
        self._control_service = ControlService(joystick_control=self._joystick_control)

        self._pyaudio_recorder = PyAudioRecorder()
        self._recorder_service = RecorderService(
            configuration_service=self._configuration_service,
            pyaudio_recorder=self._pyaudio_recorder,
        )

        self._transcriber = TranscriberService(
            configuration_service=self._configuration_service,
        )

        self._typist_service = TypistService()

        self._orchestrator = OrchestratorService(
            configuration_service=self._configuration_service,
            control_service=self._control_service,
            recorder_service=self._recorder_service,
            transcriber_service=self._transcriber,
            typist_service=self._typist_service,
        )

    def do_startup(self):
        Adw.Application.do_startup(self)
        self._logger.info("Starting up.")
        self._create_action("quit", self.quit, ["<primary>q"])
        self._create_action("trigger", self._on_trigger_action)

        try:
            # Poor man DI
            self._setup_services_di()
        except Exception as e:
            self._logger.error(f"Startup failed: {e}")
            self.quit()

        # View models
        self._main_view_model = MainViewModel(orchestrator=self._orchestrator)

        # Main window
        self._main_window = MainWindow(
            application=self,
            view_model=self._main_view_model,
        )

    def do_activate(self):
        self._logger.info("Activating.")
        self._main_window.present()

    def do_shutdown(self):
        self._logger.info("Shutting down.")
        self._main_view_model.shutdown()
        self._orchestrator.shutdown()
        self._typist_service.shutdown()
        self._transcriber.shutdown()
        self._recorder_service.shutdown()
        self._pyaudio_recorder.shutdown()
        self._control_service.shutdown()
        self._joystick_control.shutdown()
        self._configuration_service.shutdown()
        Adw.Application.do_shutdown(self)

    def _on_trigger_action(self, action, param):
        self._orchestrator.triggered()

    def _create_action(self, name, callback, shortcuts=None):
        action = Gio.SimpleAction.new(name, None)
        action.connect("activate", callback)
        self.add_action(action)
        if shortcuts:
            self.set_accels_for_action(f"app.{name}", shortcuts)
