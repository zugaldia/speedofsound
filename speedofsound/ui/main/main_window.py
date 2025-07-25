import logging

from gi.repository import Adw, Gdk, GLib, Gtk  # type: ignore

from speedofsound.constants import APPLICATION_NAME
from speedofsound.models import OrchestratorStage
from speedofsound.ui.content.content_widget import ContentWidget
from speedofsound.ui.main.main_view_model import MainViewModel
from speedofsound.ui.status.status_bar import StatusBar


class MainWindow(Adw.ApplicationWindow):
    def __init__(
        self,
        application: Adw.Application,
        view_model: MainViewModel,
    ) -> None:
        super().__init__(application=application)
        self._logger = logging.getLogger(__name__)
        self.set_title(APPLICATION_NAME)
        self.set_resizable(False)

        toolbar_view = Adw.ToolbarView()
        self.set_content(toolbar_view)

        header_bar = Adw.HeaderBar()
        header_bar.set_title_widget(Gtk.Label(label=APPLICATION_NAME))
        toolbar_view.add_top_bar(header_bar)

        # This would make the experience cleaner, but it also makes it harder
        # for the user to find a way to quit the application.
        # self.set_hide_on_close(True)
        # header_bar.set_show_end_title_buttons(False)

        self._content_widget = ContentWidget()
        toolbar_view.set_content(self._content_widget)

        self._status_bar = StatusBar()
        toolbar_view.add_bottom_bar(self._status_bar)

        # We trigger typing when the window confirms the hiding signal.
        self.connect("hide", self._on_dismissed)

        self._view_model = view_model
        self._was_recording = False
        self._view_model.view_state.connect(
            "notify::status-text", self._on_status_text_changed
        )
        self._view_model.view_state.connect(
            "notify::orchestrator-state", self._on_orchestrator_state_changed
        )
        self._view_model.view_state.connect(
            "notify::volume-level", self._on_volume_level_changed
        )
        self._view_model.view_state.connect(
            "notify::language-name", self._on_language_name_changed
        )
        self._view_model.view_state.connect(
            "notify::model-name", self._on_model_name_changed
        )
        self._view_model.view_state.connect(
            "notify::words-per-minute", self._on_words_per_minute_changed
        )

        # Add keyboard event controller for escape key
        key_controller = Gtk.EventControllerKey()
        key_controller.connect("key-pressed", self._on_key_pressed)
        self.add_controller(key_controller)

        # Track window state changes
        # self._state_subscription_id: Optional[int] = None
        # self.connect("realize", self._on_window_realized)

    def dismiss(self) -> None:
        """Dismiss the main window."""
        self._logger.info("Dismissing the main window.")
        # Minimizing would be another option so that the app is still present
        # in the GNOME dock, but the minimize event wasn't as reliable as the
        # hide signal, not sure why (logic commented at the end of this file).
        # self.minimize()
        self.hide()

    def _on_dismissed(self, window) -> None:
        GLib.idle_add(self._view_model.action_type)

    def _on_status_text_changed(self, view_state, param) -> None:
        status_text = self._view_model.view_state.status_text
        self._content_widget.set_status(status_text)

    def _on_orchestrator_state_changed(self, view_state, param) -> None:
        # Should we run as an application service? (--gapplication-service)
        # I can't find good documentation on this, except that it seems to
        # be supported explicitly by Flatpak:
        # https://docs.flatpak.org/en/latest/conventions.html#d-bus-service-files
        orchestrator_state = self._view_model.view_state.orchestrator_state
        if orchestrator_state == OrchestratorStage.READY:
            self._content_widget.set_volume(0.0)
            if self._was_recording:
                self.dismiss()
                self._was_recording = False
        elif orchestrator_state == OrchestratorStage.RECORDING:
            self._was_recording = True
            self.present()
        elif orchestrator_state == OrchestratorStage.TRANSCRIBING:
            self._content_widget.set_pulsating(True)
        elif orchestrator_state == OrchestratorStage.TYPING:
            # We'll trigger the actual typing in _on_state_changed()
            self.dismiss()
            self._content_widget.set_pulsating(False)
            self._was_recording = False

    def _on_volume_level_changed(self, view_state, param) -> None:
        volume_level = self._view_model.view_state.volume_level
        self._content_widget.set_volume(volume_level)

    def _on_language_name_changed(self, view_state, param) -> None:
        language_name = self._view_model.view_state.language_name
        self._status_bar.set_language_name(language_name)

    def _on_model_name_changed(self, view_state, param) -> None:
        model_name = self._view_model.view_state.model_name
        self._status_bar.set_model_name(model_name)

    def _on_words_per_minute_changed(self, view_state, param) -> None:
        words_per_minute = self._view_model.view_state.words_per_minute
        self._status_bar.set_words_per_minute(words_per_minute)

    def _on_key_pressed(self, controller, keyval, keycode, state) -> bool:
        if keyval == Gdk.KEY_Escape:
            orchestrator_state = self._view_model.view_state.orchestrator_state
            if orchestrator_state == OrchestratorStage.RECORDING:
                self._view_model.cancel_recording()
                return True
        return False

    # def _on_window_realized(self, window) -> None:
    #     surface: Optional[Gdk.Surface] = self.get_surface()
    #     if surface and not self._state_subscription_id:
    #         self._state_subscription_id = surface.connect(
    #             "notify::state", self._on_surface_state_changed
    #         )

    # def _on_surface_state_changed(self, surface: Gdk.Toplevel, param) -> None:
    #     state = surface.get_state()
    #     self._logger.info(f"Window state changed: {state}")

    #     state_names = []
    #     if state & Gdk.ToplevelState.MINIMIZED:
    #         state_names.append("MINIMIZED")
    #     if "MINIMIZED" in state_names:
    #         # This event might get triggered multiple times, so the function
    #         # below needs to be ready to be idempotent.
    #         GLib.idle_add(self._view_model.action_type)
