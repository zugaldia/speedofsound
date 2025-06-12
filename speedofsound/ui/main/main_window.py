import logging

from gi.repository import Adw, Gdk, GObject, Gtk  # type: ignore

from speedofsound.constants import APPLICATION_NAME
from speedofsound.ui.main.main_view_model import MainViewModel
from speedofsound.ui.main.main_view_state import MainViewState


class MainWindow(Adw.ApplicationWindow):
    def __init__(
        self,
        application: Adw.Application,
        view_model: MainViewModel,
    ) -> None:
        super().__init__(application=application)
        self._logger = logging.getLogger(__name__)
        self.set_title(APPLICATION_NAME)
        self.set_default_size(500, 200)
        self._load_css()

        self._view_model = view_model
        self._view_model.view_state.connect(
            "notify::status-text", self._on_status_text_changed
        )

        toolbar_view = Adw.ToolbarView()
        self.set_content(toolbar_view)

        header_bar = Adw.HeaderBar()
        header_bar.set_title_widget(Gtk.Label(label=APPLICATION_NAME))
        toolbar_view.add_top_bar(header_bar)

        main_label = Gtk.Label(label="Ready.")
        toolbar_view.set_content(main_label)

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

    def _on_status_text_changed(
        self,
        view_state: MainViewState,
        param_spec: GObject.ParamSpec,
    ):
        self._logger.info(f"Status text changed: {view_state.status_text}")
