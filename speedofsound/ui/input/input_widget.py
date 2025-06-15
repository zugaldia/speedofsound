from gi.repository import GLib, GObject, Gtk  # type: ignore

from speedofsound.constants import (
    DEFAULT_MARGIN,
    DEFAULT_SPACING,
    INPUT_BUTTON_CLICKED_SIGNAL,
)


class InputWidget(Gtk.Box):
    __gsignals__ = {
        INPUT_BUTTON_CLICKED_SIGNAL: (GObject.SIGNAL_RUN_FIRST, None, ()),
    }

    def __init__(self) -> None:
        super().__init__(
            orientation=Gtk.Orientation.VERTICAL,
            spacing=DEFAULT_SPACING,
        )
        self.set_margin_top(DEFAULT_MARGIN)
        self.set_margin_bottom(DEFAULT_MARGIN)
        self.set_margin_start(DEFAULT_MARGIN)
        self.set_margin_end(DEFAULT_MARGIN)
        self.set_halign(Gtk.Align.CENTER)
        self.set_valign(Gtk.Align.CENTER)

        horizontal_wrapper = Gtk.Box(
            orientation=Gtk.Orientation.HORIZONTAL,
            spacing=DEFAULT_SPACING,
        )
        horizontal_wrapper.set_homogeneous(True)
        horizontal_wrapper.append(Gtk.Box())

        vertical_wrapper = Gtk.Box(
            orientation=Gtk.Orientation.VERTICAL,
            spacing=DEFAULT_SPACING,
        )

        self._pulse_timeout_id = None
        self._progress_bar = Gtk.ProgressBar()
        vertical_wrapper.append(self._progress_bar)

        self._main_button = Gtk.Button(label="Start")
        self._main_button.connect("clicked", self._on_button_clicked)
        vertical_wrapper.append(self._main_button)

        horizontal_wrapper.append(vertical_wrapper)
        horizontal_wrapper.append(Gtk.Box())
        self.append(horizontal_wrapper)

        self._status_label = Gtk.Label(label="Ready.")
        self._status_label.set_halign(Gtk.Align.CENTER)
        self._status_label.set_wrap(True)
        self.append(self._status_label)

    def set_status(self, status: str) -> None:
        self._status_label.set_text(status)

    def set_volume(self, volume: float) -> None:
        if 0.0 <= volume <= 1.0:
            self._progress_bar.set_fraction(volume)

    def set_pulsating(self, active: bool) -> None:
        if active:
            if self._pulse_timeout_id is None:
                self._pulse_timeout_id = GLib.timeout_add(150, self._pulse_progress_bar)
        else:
            if self._pulse_timeout_id is not None:
                GLib.source_remove(self._pulse_timeout_id)
                self._pulse_timeout_id = None
            self._progress_bar.set_fraction(0.0)

    def set_button_label(self, label: str) -> None:
        self._main_button.set_label(label)

    def set_button_enabled(self, enabled: bool) -> None:
        self._main_button.set_sensitive(enabled)

    def _pulse_progress_bar(self) -> bool:
        self._progress_bar.pulse()
        return True

    def _on_button_clicked(self, button: Gtk.Button) -> None:
        self.emit(INPUT_BUTTON_CLICKED_SIGNAL)
