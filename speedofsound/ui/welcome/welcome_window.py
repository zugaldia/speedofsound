import logging

from gi.repository import Adw, Gio, Gtk  # type: ignore

from speedofsound.constants import (
    APPLICATION_NAME,
    DEFAULT_MARGIN,
    DEFAULT_SPACING,
    SETTING_SHOW_WELCOME,
)


class WelcomeWindow(Adw.Window):
    def __init__(self, application: Adw.Application, settings: Gio.Settings) -> None:
        super().__init__(application=application)
        self._settings = settings
        self._logger = logging.getLogger(__name__)
        self.set_title(APPLICATION_NAME)
        self.set_size_request(400, 300)

        welcome_text = (
            f"Welcome to {APPLICATION_NAME}.\n\n"
            "The main window stays hidden by default,\n"
            "you'll need to trigger it to start voice typing.\n"
            "For setup information, please read "
            '<a href="https://github.com/zugaldia/speedofsound">the documentation</a>.\n\n'
            "Happy voice typing!"
        )

        toolbar_view = Adw.ToolbarView()
        self.set_content(toolbar_view)

        header_bar = Adw.HeaderBar()
        header_bar.set_title_widget(Gtk.Label(label=APPLICATION_NAME))
        header_bar.set_show_end_title_buttons(True)
        toolbar_view.add_top_bar(header_bar)

        vbox = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=DEFAULT_SPACING)
        vbox.set_margin_top(DEFAULT_MARGIN)
        vbox.set_margin_bottom(DEFAULT_MARGIN)
        vbox.set_margin_start(DEFAULT_MARGIN)
        vbox.set_margin_end(DEFAULT_MARGIN)

        welcome_label = Gtk.Label()
        welcome_label.set_markup(welcome_text)
        welcome_label.set_wrap(False)
        welcome_label.set_margin_bottom(DEFAULT_MARGIN)
        vbox.append(welcome_label)

        dont_show_checkbox = Gtk.CheckButton(label="Don't show this again")
        dont_show_checkbox.connect("toggled", self._on_checkbox_toggled)
        vbox.append(dont_show_checkbox)

        accept_button = Gtk.Button(label="Got it")
        accept_button.connect("clicked", self._on_accept_clicked)
        vbox.append(accept_button)

        toolbar_view.set_content(vbox)

    def _on_checkbox_toggled(self, checkbox: Gtk.CheckButton) -> None:
        is_checked = checkbox.get_active()
        if self._settings:
            self._settings.set_boolean(SETTING_SHOW_WELCOME, not is_checked)

    def _on_accept_clicked(self, button: Gtk.Button) -> None:
        self.close()
