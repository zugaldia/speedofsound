import logging
from typing import Any, Callable, Optional

from gi.repository import Adw, Gio, GObject, Gtk  # type: ignore

from speedofsound.languages import LANGUAGES
from speedofsound.models import TypistBackend
from speedofsound.ui.preferences import PreferencesViewModel


class PreferencesPageBase(Adw.PreferencesPage):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__()
        self._view_model: PreferencesViewModel = view_model
        self._logger: logging.Logger = logging.getLogger(self.__class__.__name__)

    def _setting_key_to_attr(self, setting_key: str) -> str:
        """Convert GSettings key format (kebab-case) to Python attribute name (snake_case)."""
        return setting_key.replace("-", "_")

    def bind_boolean_setting(self, setting_key: str, widget: Adw.SwitchRow) -> None:
        """Bind a boolean GSettings key to a switch widget with fallback handling."""
        configuration = self._view_model.configuration
        settings = configuration.settings
        if settings is not None and configuration.has_key(setting_key):
            settings.bind(
                setting_key,
                widget,
                "active",
                Gio.SettingsBindFlags.DEFAULT,
            )
        else:
            widget.set_sensitive(False)
            self._logger.warning(
                f"GSettings key '{setting_key}' not available, widget will not be functional"
            )

    def bind_int_setting(self, setting_key: str, widget: Adw.SpinRow) -> None:
        """Bind an integer GSettings key to a spin widget with fallback handling."""
        configuration = self._view_model.configuration
        settings = configuration.settings
        if settings is not None and configuration.has_key(setting_key):
            settings.bind(
                setting_key,
                widget,
                "value",
                Gio.SettingsBindFlags.DEFAULT,
            )
        else:
            widget.set_sensitive(False)
            self._logger.warning(
                f"GSettings key '{setting_key}' not available, widget will not be functional"
            )

    def bind_double_setting(self, setting_key: str, widget: Adw.SpinRow) -> None:
        """Bind a double GSettings key to a spin widget with fallback handling."""
        configuration = self._view_model.configuration
        settings = configuration.settings
        if settings is not None and configuration.has_key(setting_key):
            settings.bind(
                setting_key,
                widget,
                "value",
                Gio.SettingsBindFlags.DEFAULT,
            )
        else:
            widget.set_sensitive(False)
            self._logger.warning(
                f"GSettings key '{setting_key}' not available, widget will not be functional"
            )

    def bind_entry_setting(
        self, setting_key: str, widget: Adw.EntryRow | Adw.PasswordEntryRow
    ) -> None:
        """Bind a string GSettings key to an entry widget with fallback handling."""
        configuration = self._view_model.configuration
        settings = configuration.settings
        if settings is not None and configuration.has_key(setting_key):
            settings.bind(
                setting_key,
                widget,
                "text",
                Gio.SettingsBindFlags.DEFAULT,
            )
        else:
            widget.set_sensitive(False)
            self._logger.warning(
                f"GSettings key '{setting_key}' not available, widget will not be functional"
            )

    def create_combo_row(
        self,
        title: str,
        setting_key: str,
        options: list[tuple[str, Any]],
        get_current_value: Callable[[], Any],
        set_value: Callable[[Any], None],
        format_display: Optional[Callable[[str, Any], str]] = None,
        subtitle: Optional[str] = None,
    ) -> Adw.ComboRow:
        """
        Create a generic combo row for any setting type.

        Args:
            title: The title of the combo row
            setting_key: The GSettings key for this setting
            options: List of (display_name, value) tuples
            get_current_value: Function to get the current value from config
            set_value: Function to set the value in GSettings
            format_display: Optional function to format subtitle display
            subtitle: Optional static subtitle text

        Returns:
            Configured Adw.ComboRow
        """
        combo = Adw.ComboRow()
        combo.set_title(title)
        if subtitle:
            combo.set_subtitle(subtitle)

        string_list = Gtk.StringList()
        for display_name, _ in options:
            string_list.append(display_name)
        combo.set_model(string_list)

        def on_changed(widget: Adw.ComboRow, _pspec: GObject.ParamSpec) -> None:
            selected_index = widget.get_selected()
            if selected_index < 0 or selected_index >= len(options):
                return

            display_name, value = options[selected_index]

            if format_display:
                formatted = format_display(display_name, value)
                widget.set_subtitle(formatted)

            configuration = self._view_model.configuration
            settings = configuration.settings
            if settings is not None and configuration.has_key(setting_key):
                set_value(value)
                self._logger.info(f"{setting_key} changed to: {value}")

        combo.connect("notify::selected", on_changed)
        current_value = get_current_value()
        for i, (display_name, value) in enumerate(options):
            if value == current_value:
                combo.set_selected(i)
                if format_display and not subtitle:
                    combo.set_subtitle(format_display(display_name, value))
                break

        return combo

    def create_language_combo(self, title: str, setting_key: str) -> Adw.ComboRow:
        """Create a language combo row for the given setting key."""
        attr_name: str = self._setting_key_to_attr(setting_key)
        options: list[tuple[str, str]] = [
            (name, code) for name, code in LANGUAGES.items()
        ]

        return self.create_combo_row(
            title=title,
            setting_key=setting_key,
            options=options,
            get_current_value=lambda: getattr(
                self._view_model.configuration, attr_name
            ),
            set_value=lambda value: setattr(
                self._view_model.configuration, attr_name, value
            ),
            format_display=lambda name, code: f"{name} ({code})",
        )

    def create_microphone_combo(self, title: str, setting_key: str) -> Adw.ComboRow:
        """Create a microphone device combo row for the given setting key."""
        attr_name: str = self._setting_key_to_attr(setting_key)
        available_devices = self._view_model.configuration.available_microphone_devices
        options: list[tuple[str, str]] = [("Default microphone", "")]
        for device in available_devices:
            options.append((device.display_name, device.device_name))

        return self.create_combo_row(
            title=title,
            setting_key=setting_key,
            options=options,
            get_current_value=lambda: getattr(
                self._view_model.configuration, attr_name
            ),
            set_value=lambda value: setattr(
                self._view_model.configuration, attr_name, value
            ),
            subtitle="Select the microphone to use for recording",
        )

    def create_joystick_combo(self, title: str, setting_key: str) -> Adw.ComboRow:
        """Create a joystick device combo row for the given setting key."""
        attr_name: str = self._setting_key_to_attr(setting_key)
        available_devices = self._view_model.configuration.available_joystick_devices
        options: list[tuple[str, int]] = [("No joystick", -1)]
        for device in available_devices:
            options.append((device.name, device.id))

        return self.create_combo_row(
            title=title,
            setting_key=setting_key,
            options=options,
            get_current_value=lambda: getattr(
                self._view_model.configuration, attr_name
            ),
            set_value=lambda value: setattr(
                self._view_model.configuration, attr_name, value
            ),
            subtitle="Requires application restart to take effect",
        )

    def create_typist_backend_combo(self, title: str, setting_key: str) -> Adw.ComboRow:
        """Create a typist backend combo row for the given setting key."""
        attr_name: str = self._setting_key_to_attr(setting_key)
        options: list[tuple[str, str]] = [
            ("Auto", TypistBackend.AUTO.value),
            ("AT-SPI", TypistBackend.ATSPI.value),
            ("xdotool", TypistBackend.XDOTOOL.value),
            ("ydotool", TypistBackend.YDOTOOL.value),
        ]

        return self.create_combo_row(
            title=title,
            setting_key=setting_key,
            options=options,
            get_current_value=lambda: getattr(
                self._view_model.configuration, attr_name
            ),
            set_value=lambda value: setattr(
                self._view_model.configuration, attr_name, value
            ),
            subtitle="Mechanism to simulate typing into applications. Auto should normally be adequate. Requires application restart to take effect.",
        )
