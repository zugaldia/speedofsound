from speedofsound.constants import ORCHESTRATOR_EVENT_SIGNAL, VOLUME_LEVEL_SIGNAL
from speedofsound.models import OrchestratorEvent
from speedofsound.services.orchestrator import OrchestratorService
from speedofsound.ui.base_view_model import BaseViewModel
from speedofsound.ui.main.main_view_state import MainViewState


class MainViewModel(BaseViewModel):
    def __init__(self, orchestrator: OrchestratorService):
        super().__init__()
        self.view_state = MainViewState()
        self._orchestrator = orchestrator
        self._orchestrator.connect(
            ORCHESTRATOR_EVENT_SIGNAL, self._on_orchestrator_event
        )
        self._orchestrator.connect(VOLUME_LEVEL_SIGNAL, self._on_volume_level)

    def shutdown(self):
        pass

    def input_button_clicked(self):
        self._orchestrator.triggered()

    def action_type(self):
        self._orchestrator.action_type()

    def _on_orchestrator_event(
        self, service: OrchestratorService, encoded: str
    ) -> None:
        try:
            event = OrchestratorEvent.model_validate_json(encoded)
            self.view_state.orchestrator_state = event.stage
            if event.message:
                if event.success:
                    self.view_state.status_text = event.message
                else:
                    self.view_state.status_text = f"Error: {event.message}"
                    self.logger.warning(
                        f"Orchestrator error in stage {event.stage}: {event.message}"
                    )
        except Exception as e:
            self.logger.error(f"Error handling orchestrator event: {e}")

    def _on_volume_level(self, service: OrchestratorService, volume: float) -> None:
        """Handle volume level updates from the orchestrator."""
        self.view_state.volume_level = volume
