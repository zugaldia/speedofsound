from gi.repository import GLib, GObject, Gtk  # type: ignore

from speedofsound.constants import DEFAULT_MARGIN, DEFAULT_SPACING


class InputWidget(Gtk.Box):

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

        self._pulse_timeout_id = None
        self._progress_bar = Gtk.ProgressBar()
        self._progress_bar.set_size_request(250, -1)
        self._progress_bar.set_halign(Gtk.Align.CENTER)
        self.append(self._progress_bar)

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

    def _pulse_progress_bar(self) -> bool:
        self._progress_bar.pulse()
        return True
