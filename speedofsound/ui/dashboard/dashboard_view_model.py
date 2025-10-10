from speedofsound.ui.base_view_model import BaseViewModel
from speedofsound.ui.dashboard.dashboard_view_state import DashboardViewState


class DashboardViewModel(BaseViewModel):
    def __init__(self):
        super().__init__()
        self.view_state = DashboardViewState()

    def shutdown(self):
        self._logger.info("Shutting down.")
