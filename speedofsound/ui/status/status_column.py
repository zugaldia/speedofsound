from gi.repository import Gtk, Pango  # type: ignore

from speedofsound.constants import DEFAULT_SPACING


class StatusColumn(Gtk.Box):
    def __init__(self, icon_name: str, default_text: str) -> None:
        super().__init__(
            orientation=Gtk.Orientation.HORIZONTAL,
            spacing=int(DEFAULT_SPACING / 2),
        )
        self.set_halign(Gtk.Align.CENTER)

        icon = Gtk.Image.new_from_icon_name(icon_name)
        icon.set_icon_size(Gtk.IconSize.NORMAL)
        icon.set_pixel_size(10)
        self.append(icon)

        self._label = Gtk.Label(label=default_text)
        self._label.set_halign(Gtk.Align.CENTER)
        self._label.set_ellipsize(Pango.EllipsizeMode.END)
        self._label.set_max_width_chars(15)
        self._label.add_css_class("status-column-text")
        self.append(self._label)

    def set_text(self, text: str) -> None:
        """Update the label text."""
        self._label.set_text(text)
