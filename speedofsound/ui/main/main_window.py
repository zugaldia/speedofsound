import logging

from gi.repository import Adw, Gdk, Gtk  # type: ignore

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
        self._load_css()

        # For consistency with the behavior where we hide the window when we
        # start typing or cancel transcription.
        self.set_hide_on_close(True)

        toolbar_view = Adw.ToolbarView()
        self.set_content(toolbar_view)

        header_bar = Adw.HeaderBar()
        header_bar.set_title_widget(Gtk.Label(label=APPLICATION_NAME))
        header_bar.set_show_end_title_buttons(False)
        toolbar_view.add_top_bar(header_bar)

        self._content_widget = ContentWidget()
        toolbar_view.set_content(self._content_widget)

        self._status_bar = StatusBar()
        toolbar_view.add_bottom_bar(self._status_bar)

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
            "notify::microphone-name", self._on_microphone_name_changed
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

    def _on_status_text_changed(self, view_state, param) -> None:
        status_text = self._view_model.view_state.status_text
        self._content_widget.set_status(status_text)

    def _on_orchestrator_state_changed(self, view_state, param) -> None:
        orchestrator_state = self._view_model.view_state.orchestrator_state
        if orchestrator_state == OrchestratorStage.READY:
            self._content_widget.set_volume(0.0)
            if self._was_recording:
                self.hide()
                self._was_recording = False
        elif orchestrator_state == OrchestratorStage.RECORDING:
            self._was_recording = True
            self.present()
        elif orchestrator_state == OrchestratorStage.TRANSCRIBING:
            self._content_widget.set_pulsating(True)
        elif orchestrator_state == OrchestratorStage.TYPING:
            self.hide()
            self._content_widget.set_pulsating(False)
            self._was_recording = False
            self._view_model.action_type()

    def _on_volume_level_changed(self, view_state, param) -> None:
        volume_level = self._view_model.view_state.volume_level
        self._content_widget.set_volume(volume_level)

    def _on_language_name_changed(self, view_state, param) -> None:
        language_name = self._view_model.view_state.language_name
        self._status_bar.set_language_name(language_name)

    def _on_microphone_name_changed(self, view_state, param) -> None:
        microphone_name = self._view_model.view_state.microphone_name
        self._status_bar.set_microphone_name(microphone_name)

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
