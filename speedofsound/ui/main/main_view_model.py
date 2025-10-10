from speedofsound.constants import (
    LANGUAGE_NAME_SIGNAL,
    MODEL_NAME_SIGNAL,
    ORCHESTRATOR_EVENT_SIGNAL,
    VOLUME_LEVEL_SIGNAL,
    WORDS_PER_MINUTE_SIGNAL,
)
from speedofsound.models import OrchestratorEvent
from speedofsound.services.orchestrator import OrchestratorService
from speedofsound.ui.base_view_model import BaseViewModel
from speedofsound.ui.main.main_view_state import MainViewState


class MainViewModel(BaseViewModel):
    MAX_WPM_READINGS = 10

    def __init__(self, orchestrator: OrchestratorService):
        super().__init__()
        self.view_state = MainViewState()
        self._words_per_minute_readings: list[float] = []

        self._orchestrator = orchestrator
        self._orchestrator.connect(VOLUME_LEVEL_SIGNAL, self._on_volume_level)
        self._orchestrator.connect(LANGUAGE_NAME_SIGNAL, self._on_language_name)
        self._orchestrator.connect(MODEL_NAME_SIGNAL, self._on_model_name)
        self._orchestrator.connect(WORDS_PER_MINUTE_SIGNAL, self._on_words_per_minute)
        self._orchestrator.connect(
            ORCHESTRATOR_EVENT_SIGNAL, self._on_orchestrator_event
        )

    def shutdown(self):
        if self._orchestrator:
            self._logger.info("Shutting down.")
            self._orchestrator.disconnect_by_func(self._on_volume_level)
            self._orchestrator.disconnect_by_func(self._on_language_name)
            self._orchestrator.disconnect_by_func(self._on_model_name)
            self._orchestrator.disconnect_by_func(self._on_words_per_minute)
            self._orchestrator.disconnect_by_func(self._on_orchestrator_event)

    def action_type(self):
        self._orchestrator.action_type()

    def cancel_recording(self):
        self._orchestrator.cancel_recording()

    def _on_orchestrator_event(
        self, _service: OrchestratorService, encoded: str
    ) -> None:
        try:
            event = OrchestratorEvent.model_validate_json(encoded)
            self.view_state.orchestrator_state = event.stage
            if event.message:
                if event.success:
                    self.view_state.status_text = event.message
                else:
                    self.view_state.status_text = f"Error: {event.message}"
                    self._logger.warning(
                        f"Orchestrator error in stage {event.stage}: {event.message}"
                    )
        except Exception as e:
            self._logger.error(f"Error handling orchestrator event: {e}")

    def _on_volume_level(self, _service: OrchestratorService, volume: float) -> None:
        """Handle volume level updates from the orchestrator."""
        self.view_state.volume_level = volume

    def _on_language_name(
        self, _service: OrchestratorService, language_name: str
    ) -> None:
        """Handle language name updates from the orchestrator."""
        self.view_state.language_name = language_name

    def _on_model_name(self, _service: OrchestratorService, model_name: str) -> None:
        """Handle model name updates from the orchestrator."""
        self.view_state.model_name = model_name

    def _on_words_per_minute(
        self, _service: OrchestratorService, words_per_minute: float
    ) -> None:
        """Handle words per minute updates from the orchestrator."""
        if words_per_minute > 0:
            self._words_per_minute_readings.append(words_per_minute)
            if len(self._words_per_minute_readings) > self.MAX_WPM_READINGS:
                self._words_per_minute_readings.pop(0)  # Remove oldest
            self.view_state.words_per_minute = sum(
                self._words_per_minute_readings
            ) / len(self._words_per_minute_readings)
