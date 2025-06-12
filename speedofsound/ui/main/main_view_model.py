from speedofsound.ui.base_view_model import BaseViewModel
from speedofsound.ui.main.main_view_state import MainViewState


class MainViewModel(BaseViewModel):
    def __init__(self):
        super().__init__()
        self.view_state = MainViewState()

    def shutdown(self):
        pass
