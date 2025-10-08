import logging

from gi.repository import Adw, Gio, Gtk, Pango  # type: ignore

from speedofsound.constants import (
    ACTION_ABOUT,
    ACTION_DOCUMENTATION,
    ACTION_PREFERENCES,
    ACTION_QUIT,
    ACTION_TRIGGER,
    APPLICATION_DEVELOPER,
    APPLICATION_DOCUMENTATION_URL,
    APPLICATION_ID,
    APPLICATION_ISSUE_URL,
    APPLICATION_NAME,
    APPLICATION_VERSION,
    APPLICATION_WEBSITE,
    DASHBOARD_WINDOW_DEFAULT_HEIGHT,
    DASHBOARD_WINDOW_DEFAULT_WIDTH,
    DEFAULT_MARGIN,
    DEFAULT_SPACING,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.ui.dashboard.dashboard_view_model import DashboardViewModel
from speedofsound.ui.preferences import PreferencesViewModel, PreferencesWindow

USER_INSTRUCTIONS = """
<b>Test your voice typing setup</b>\n
Click the button below and speak into your microphone.
Use your keyboard (<tt>Super+Z</tt>) or joystick to finish recording.\n
If your words appear above, you can minimize this window and start typing into any app. Not working? Check your preferences.
""".strip()

TEXT_VIEW_WIDTH = 400
TEXT_VIEW_HEIGHT = 120
TEST_BUTTON_WIDTH = 200
TEST_BUTTON_HEIGHT = 50
INSTRUCTIONS_MAX_WIDTH_CHARS = 50


class DashboardWindow(Adw.ApplicationWindow):
    def __init__(
        self,
        application: Adw.Application,
        view_model: DashboardViewModel,
        configuration: ConfigurationService,
    ) -> None:
        super().__init__(application=application)
        self._logger = logging.getLogger(__name__)
        self.set_title(APPLICATION_NAME)
        self.connect("close-request", self._on_close_request)
        self.set_default_size(
            DASHBOARD_WINDOW_DEFAULT_WIDTH,
            DASHBOARD_WINDOW_DEFAULT_HEIGHT,
        )

        self._view_model = view_model
        self._application = application
        self._configuration = configuration
        self._setup_actions()

        toolbar_view = Adw.ToolbarView()
        self.set_content(toolbar_view)

        header_bar = Adw.HeaderBar()
        header_bar.set_title_widget(Gtk.Label(label=APPLICATION_NAME))
        header_bar.set_show_end_title_buttons(True)

        menu = Gio.Menu()
        menu.append("Preferences", f"app.{ACTION_PREFERENCES}")
        menu.append("Documentation", f"app.{ACTION_DOCUMENTATION}")
        menu.append("About", f"app.{ACTION_ABOUT}")

        menu_button = Gtk.MenuButton()
        menu_button.set_icon_name("open-menu-symbolic")
        menu_button.set_menu_model(menu)
        header_bar.pack_end(menu_button)

        toolbar_view.add_top_bar(header_bar)

        content_box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL)
        content_box.set_hexpand(True)
        content_box.set_vexpand(True)
        content_box.set_halign(Gtk.Align.CENTER)
        content_box.set_valign(Gtk.Align.CENTER)
        content_box.set_spacing(DEFAULT_SPACING)

        self._text_view = Gtk.TextView()
        self._text_view.set_wrap_mode(Gtk.WrapMode.WORD)
        self._text_view.set_size_request(TEXT_VIEW_WIDTH, TEXT_VIEW_HEIGHT)
        self._text_view.set_top_margin(DEFAULT_MARGIN)
        self._text_view.set_bottom_margin(DEFAULT_MARGIN)
        self._text_view.set_left_margin(DEFAULT_MARGIN)
        self._text_view.set_right_margin(DEFAULT_MARGIN)

        scrolled_window = Gtk.ScrolledWindow()
        scrolled_window.set_child(self._text_view)
        scrolled_window.set_size_request(TEXT_VIEW_WIDTH, TEXT_VIEW_HEIGHT)
        scrolled_window.add_css_class("card")

        content_box.append(scrolled_window)

        instructions_label = Gtk.Label()
        instructions_label.set_markup(USER_INSTRUCTIONS)
        instructions_label.set_wrap(True)
        instructions_label.set_wrap_mode(Pango.WrapMode.WORD)
        instructions_label.set_justify(Gtk.Justification.CENTER)
        instructions_label.set_max_width_chars(INSTRUCTIONS_MAX_WIDTH_CHARS)
        instructions_label.set_margin_top(DEFAULT_MARGIN)
        instructions_label.set_margin_bottom(DEFAULT_MARGIN)
        instructions_label.add_css_class("dim-label")
        content_box.append(instructions_label)

        button_content = Adw.ButtonContent(
            label="Test Voice Typing",
            icon_name="audio-input-microphone-symbolic",
        )

        start_button = Gtk.Button()
        start_button.set_child(button_content)
        start_button.connect("clicked", self._on_test_button_clicked)
        start_button.add_css_class("pill")
        start_button.add_css_class("suggested-action")
        start_button.set_size_request(TEST_BUTTON_WIDTH, TEST_BUTTON_HEIGHT)
        content_box.append(start_button)

        toolbar_view.set_content(content_box)

    def _setup_actions(self) -> None:
        preferences_action = Gio.SimpleAction.new(ACTION_PREFERENCES, None)
        preferences_action.connect("activate", self._on_preferences_activated)
        self._application.add_action(preferences_action)

        documentation_action = Gio.SimpleAction.new(ACTION_DOCUMENTATION, None)
        documentation_action.connect("activate", self._on_documentation_activated)
        self._application.add_action(documentation_action)

        about_action = Gio.SimpleAction.new(ACTION_ABOUT, None)
        about_action.connect("activate", self._on_about_activated)
        self._application.add_action(about_action)

    def _on_preferences_activated(
        self, action: Gio.SimpleAction, parameter: None
    ) -> None:
        self._logger.info("Preferences menu item clicked")
        preferences_view_model = PreferencesViewModel(configuration=self._configuration)
        preferences_window = PreferencesWindow(view_model=preferences_view_model)
        preferences_window.present(self)

    def _on_documentation_activated(
        self, action: Gio.SimpleAction, parameter: None
    ) -> None:
        Gtk.show_uri(self, APPLICATION_DOCUMENTATION_URL, 0)

    def _on_about_activated(self, action: Gio.SimpleAction, parameter: None) -> None:
        about_dialog = Adw.AboutDialog()
        about_dialog.set_application_name(APPLICATION_NAME)
        about_dialog.set_application_icon(APPLICATION_ID)
        about_dialog.set_version(APPLICATION_VERSION)
        about_dialog.set_developer_name(APPLICATION_DEVELOPER)
        about_dialog.set_license_type(Gtk.License.MIT_X11)
        about_dialog.set_website(APPLICATION_WEBSITE)
        about_dialog.set_issue_url(APPLICATION_ISSUE_URL)
        about_dialog.present(self)

    def _on_test_button_clicked(self, button: Gtk.Button) -> None:
        self._text_view.grab_focus()
        self._application.activate_action(ACTION_TRIGGER)

    def _on_close_request(self, window: Adw.ApplicationWindow) -> bool:
        self._application.activate_action(ACTION_QUIT)
        return False
