import logging
from typing import Optional

import gi

gi.require_version("Adw", "1")
gi.require_version("Atspi", "2.0")
gi.require_version("Gdk", "4.0")
gi.require_version("Gst", "1.0")
gi.require_version("Gtk", "4.0")
from gi.repository import Adw, Gio  # type: ignore  # noqa: E402

from speedofsound.constants import (  # noqa: E402
    ACTION_QUIT,
    ACTION_SHOW,
    ACTION_TRIGGER,
    APPLICATION_ID,
    APPLICATION_NAME,
    LOG_FILE,
)
from speedofsound.services.benchmark import BenchmarkService  # noqa: E402
from speedofsound.services.configuration import ConfigurationService  # noqa: E402
from speedofsound.services.context import ContextService  # noqa: E402
from speedofsound.services.control import ControlService  # noqa: E402
from speedofsound.services.control.joystick_control import JoystickControl  # noqa: E402
from speedofsound.services.extension import ExtensionService  # noqa: E402
from speedofsound.services.orchestrator import OrchestratorService  # noqa: E402
from speedofsound.services.recorder import RecorderService  # noqa: E402
from speedofsound.services.transcriber import TranscriberService  # noqa: E402
from speedofsound.services.typist import TypistService  # noqa: E402
from speedofsound.ui.dashboard.dashboard_view_model import DashboardViewModel  # noqa: E402
from speedofsound.ui.dashboard.dashboard_window import DashboardWindow  # noqa: E402
from speedofsound.ui.main.main_view_model import MainViewModel  # noqa: E402
from speedofsound.ui.main.main_window import MainWindow  # noqa: E402


class SosApplication(Adw.Application):
    def __init__(self, version: str):
        super().__init__(
            application_id=APPLICATION_ID,
            flags=Gio.ApplicationFlags.DEFAULT_FLAGS,
        )

        self._setup_logging()
        self._logger = logging.getLogger(__name__)
        self._logger.info(f"Initialized version {version} of {APPLICATION_NAME}.")
        self._logger.info(f"Adwaita version: {Adw.VERSION_S}")

        self._settings: Optional[Gio.Settings] = None

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

    def _get_settings(self) -> Optional[Gio.Settings]:
        try:
            source = Gio.SettingsSchemaSource.get_default()
            if source is None:
                self._logger.error("System source schema not found.")
                return None
            result = source.lookup(schema_id=APPLICATION_ID, recursive=True)
            if result is None:
                self._logger.error("Application schema not found.")
                return None
            return Gio.Settings.new(schema_id=APPLICATION_ID)
        except Exception as e:
            self._logger.error(f"Failed to initialize settings: {e}")
            return None

    def _do_manual_di(self):
        self._settings = self._get_settings()
        self._configuration = ConfigurationService()
        self._context = ContextService(configuration=self._configuration)
        self._joystick_control = JoystickControl(configuration=self._configuration)
        self._control = ControlService(joystick_control=self._joystick_control)
        self._recorder = RecorderService(configuration=self._configuration)
        self._transcriber = TranscriberService(configuration=self._configuration)
        self._typist = TypistService(configuration=self._configuration)
        self._extension = ExtensionService(settings=self._settings)
        self._benchmark = BenchmarkService(configuration=self._configuration)
        self._orchestrator = OrchestratorService(
            configuration=self._configuration,
            context=self._context,
            control=self._control,
            recorder=self._recorder,
            transcriber=self._transcriber,
            typist=self._typist,
            extension=self._extension,
            benchmark=self._benchmark,
        )

    def do_startup(self):
        Adw.Application.do_startup(self)
        self._logger.info("Starting up.")
        self._create_action(ACTION_TRIGGER, self._on_trigger_action)
        self._create_action(ACTION_SHOW, self._on_show_action)
        self._create_action(ACTION_QUIT, self._on_quit_action, ["<primary>q"])

        try:
            self._do_manual_di()  # Poor man DI
        except Exception as e:
            self._logger.error(f"Startup failed: {e}")
            self.quit()

        # Main window
        self._main_view_model = MainViewModel(orchestrator=self._orchestrator)
        self._main_window = MainWindow(
            application=self,
            view_model=self._main_view_model,
        )

        # Dashboard window
        self._dashboard_view_model = DashboardViewModel()
        self._dashboard_window = DashboardWindow(
            application=self,
            view_model=self._dashboard_view_model,
        )

    def do_activate(self):
        self._main_window.dismiss()
        self._dashboard_window.present()

    def do_shutdown(self):
        self._logger.info("Shutting down.")
        self._main_view_model.shutdown()
        self._dashboard_view_model.shutdown()
        self._orchestrator.shutdown()
        self._typist.shutdown()
        self._transcriber.shutdown()
        self._recorder.shutdown()
        self._control.shutdown()
        self._joystick_control.shutdown()
        self._extension.shutdown()
        self._benchmark.shutdown()
        self._configuration.shutdown()
        Adw.Application.do_shutdown(self)

    def _on_trigger_action(self, action, param):
        self._orchestrator.triggered()

    def _on_show_action(self, action, param):
        if self._main_window:
            self._main_window.present()

    def _on_quit_action(self, action, param):
        self.quit()

    def _create_action(self, name, callback, shortcuts=None):
        action = Gio.SimpleAction.new(name, None)
        action.connect("activate", callback)
        self.add_action(action)
        if shortcuts:
            self.set_accels_for_action(f"app.{name}", shortcuts)
