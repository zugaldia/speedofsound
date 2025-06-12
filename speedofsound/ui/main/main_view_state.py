from gi.repository import GObject
from speedofsound.ui.base_view_state import BaseViewState


class MainViewState(BaseViewState):
    status_text = GObject.Property(type=str, default="")
