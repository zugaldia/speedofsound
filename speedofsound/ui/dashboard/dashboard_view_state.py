from gi.repository import GObject  # type: ignore

from speedofsound.ui.base_view_state import BaseViewState


class DashboardViewState(BaseViewState):
    status_text = GObject.Property(type=str, default="Ready")
