import logging

from gi.repository import Adw, Gdk, Gtk  # type: ignore

from speedofsound.constants import (
    APPLICATION_NAME,
    DEFAULT_HEIGHT,
    DEFAULT_WIDTH,
    INPUT_BUTTON_CLICKED_SIGNAL,
)
from speedofsound.models import OrchestratorStage
from speedofsound.ui.input.input_widget import InputWidget
from speedofsound.ui.main.main_view_model import MainViewModel


class MainWindow(Adw.ApplicationWindow):
    def __init__(
        self,
        application: Adw.Application,
        view_model: MainViewModel,
    ) -> None:
        super().__init__(application=application)
        self._logger = logging.getLogger(__name__)
        self.set_title(APPLICATION_NAME)
        self.set_default_size(DEFAULT_WIDTH, DEFAULT_HEIGHT)
        self.set_resizable(False)
        self._load_css()

        # Same behavior as when we start typing
        self.set_hide_on_close(True)

        toolbar_view = Adw.ToolbarView()
        self.set_content(toolbar_view)

        header_bar = Adw.HeaderBar()
        header_bar.set_title_widget(Gtk.Label(label=APPLICATION_NAME))
        toolbar_view.add_top_bar(header_bar)

        self._input_widget = InputWidget()
        self._input_widget.connect(
            INPUT_BUTTON_CLICKED_SIGNAL, self._on_input_button_clicked
        )
        toolbar_view.set_content(self._input_widget)

        self._view_model = view_model
        self._view_model.view_state.connect(
            "notify::status-text", self._on_status_text_changed
        )
        self._view_model.view_state.connect(
            "notify::orchestrator-state", self._on_orchestrator_state_changed
        )
        self._view_model.view_state.connect(
            "notify::volume-level", self._on_volume_level_changed
        )

    def _load_css(self):
        default_display = Gdk.Display.get_default()
        if default_display:
            css_provider = Gtk.CssProvider()
            css_provider.load_from_path("speedofsound/data/style.css")
            Gtk.StyleContext().add_provider_for_display(
                default_display,
                css_provider,
                Gtk.STYLE_PROVIDER_PRIORITY_APPLICATION,
            )

    def _on_input_button_clicked(self, input_widget: InputWidget) -> None:
        self._view_model.input_button_clicked()

    def _on_status_text_changed(self, view_state, param) -> None:
        status_text = self._view_model.view_state.status_text
        self._input_widget.set_status(status_text)

    def _on_orchestrator_state_changed(self, view_state, param) -> None:
        orchestrator_state = self._view_model.view_state.orchestrator_state
        if orchestrator_state == OrchestratorStage.INITIALIZING:
            self._input_widget.set_button_enabled(False)
        elif orchestrator_state == OrchestratorStage.READY:
            self._input_widget.set_button_label("Start")
            self._input_widget.set_button_enabled(True)
            self._input_widget.set_volume(0.0)
        elif orchestrator_state == OrchestratorStage.RECORDING:
            self._input_widget.set_button_label("Stop")
            self.present()
        elif orchestrator_state == OrchestratorStage.TRANSCRIBING:
            self._input_widget.set_button_enabled(False)
            self._input_widget.set_pulsating(True)
        elif orchestrator_state == OrchestratorStage.TYPING:
            self.hide()
            self._input_widget.set_pulsating(False)
            self._view_model.action_type()

    def _on_volume_level_changed(self, view_state, param) -> None:
        volume_level = self._view_model.view_state.volume_level
        self._input_widget.set_volume(volume_level)
