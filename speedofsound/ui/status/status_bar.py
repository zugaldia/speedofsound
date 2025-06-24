from gi.repository import Gtk  # type: ignore

from speedofsound.constants import DEFAULT_MARGIN, DEFAULT_SPACING
from speedofsound.ui.status.status_column import StatusColumn

DEFAULT_TEXT = "Loading..."
DEFAULT_WPM = "— WPM"


class StatusBar(Gtk.Box):
    def __init__(self) -> None:
        super().__init__(orientation=Gtk.Orientation.HORIZONTAL)
        # Increase spacing between components for visibility
        self.set_spacing(DEFAULT_SPACING * 2)

        self.set_margin_top(DEFAULT_MARGIN)
        self.set_margin_bottom(DEFAULT_MARGIN)
        self.set_margin_start(DEFAULT_MARGIN)
        self.set_margin_end(DEFAULT_MARGIN)
        self.set_halign(Gtk.Align.CENTER)

        self._language_column = StatusColumn(
            "language-symbolic",
            DEFAULT_TEXT,
        )

        self._microphone_column = StatusColumn(
            "audio-input-microphone-symbolic",
            DEFAULT_TEXT,
        )

        self._model_column = StatusColumn(
            "sound-wave-symbolic",
            DEFAULT_TEXT,
        )

        self._words_per_minute_column = StatusColumn(
            "speedometer-symbolic",
            DEFAULT_WPM,
        )

        self.append(self._language_column)
        self.append(self._microphone_column)
        self.append(self._model_column)
        self.append(self._words_per_minute_column)

    def set_language_name(self, language_name: str) -> None:
        """Update the language name display."""
        self._language_column.set_text(language_name or DEFAULT_TEXT)

    def set_microphone_name(self, microphone_name: str) -> None:
        """Update the microphone name display."""
        self._microphone_column.set_text(microphone_name or DEFAULT_TEXT)

    def set_model_name(self, model_name: str) -> None:
        """Update the model name display."""
        self._model_column.set_text(model_name or DEFAULT_TEXT)

    def set_words_per_minute(self, words_per_minute: float) -> None:
        """Update the words per minute display."""
        text = f"{words_per_minute:.1f} WPM" if words_per_minute > 0 else DEFAULT_WPM
        self._words_per_minute_column.set_text(text)
