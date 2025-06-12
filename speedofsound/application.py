import logging

import gi

gi.require_version("Gtk", "4.0")
gi.require_version("Adw", "1")

from gi.repository import Adw, Gio  # type: ignore  # noqa: E402

from speedofsound.constants import APPLICATION_ID  # noqa: E402
from speedofsound.ui.main.main_view_model import MainViewModel  # noqa: E402
from speedofsound.ui.main.main_window import MainWindow  # noqa: E402


class SosApplication(Adw.Application):
    def __init__(self):
        super().__init__(
            application_id=APPLICATION_ID,
            flags=Gio.ApplicationFlags.DEFAULT_FLAGS,
        )

        logging.basicConfig(level=logging.INFO)
        self._logger = logging.getLogger(__name__)
        self._logger.info("Initialized.")

    def do_startup(self):
        Adw.Application.do_startup(self)
        self._logger.info("Starting up.")
        self._create_action("quit", self.quit, ["<primary>q"])

        # View models
        self._main_view_model = MainViewModel()

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
        Adw.Application.do_shutdown(self)

    def _create_action(self, name, callback, shortcuts=None):
        action = Gio.SimpleAction.new(name, None)
        action.connect("activate", callback)
        self.add_action(action)
        if shortcuts:
            self.set_accels_for_action(f"app.{name}", shortcuts)
