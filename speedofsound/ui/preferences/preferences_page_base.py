import logging

from gi.repository import Adw, Gio  # type: ignore

from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageBase(Adw.PreferencesPage):
    def __init__(self, view_model: PreferencesViewModel):
        super().__init__()
        self._view_model = view_model
        self._logger = logging.getLogger(self.__class__.__name__)

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

    def bind_string_setting(self, setting_key: str, widget: Adw.ComboRow) -> None:
        """Bind a string GSettings key to a combo widget with fallback handling."""
        configuration = self._view_model.configuration
        settings = configuration.settings
        if settings is not None and configuration.has_key(setting_key):
            settings.bind(
                setting_key,
                widget,
                "selected-id",
                Gio.SettingsBindFlags.DEFAULT,
            )
        else:
            widget.set_sensitive(False)
            self._logger.warning(
                f"GSettings key '{setting_key}' not available, widget will not be functional"
            )
