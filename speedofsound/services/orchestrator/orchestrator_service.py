from typing import Optional

from gi.repository import GObject  # type: ignore

from speedofsound.constants import (
    CONTROL_EVENT_SIGNAL,
    ORCHESTRATOR_EVENT_SIGNAL,
    RECORDER_RESPONSE_SIGNAL,
    TRANSCRIBER_RESPONSE_SIGNAL,
    TYPIST_RESPONSE_SIGNAL,
    VOLUME_LEVEL_SIGNAL,
)
from speedofsound.models import (
    ControlEvent,
    JoystickButton,
    OrchestratorEvent,
    OrchestratorRequest,
    OrchestratorStage,
    RecorderResponse,
    TranscriberRequest,
    TranscriberResponse,
    TypistRequest,
    TypistResponse,
)
from speedofsound.services.base_service import BaseService
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.control import ControlService
from speedofsound.services.extension import ExtensionService
from speedofsound.services.recorder import RecorderService
from speedofsound.services.transcriber import TranscriberService
from speedofsound.services.typist import TypistService
from speedofsound.utils import is_empty


class OrchestratorService(BaseService):
    SERVICE_NAME = "orchestrator"

    __gsignals__ = {
        ORCHESTRATOR_EVENT_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
        VOLUME_LEVEL_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (float,)),
    }

    def __init__(
        self,
        configuration_service: ConfigurationService,
        control_service: ControlService,
        recorder_service: RecorderService,
        transcriber_service: TranscriberService,
        typist_service: TypistService,
        extension_service: ExtensionService,
    ):
        super().__init__(service_name=self.SERVICE_NAME)
        self._stage = OrchestratorStage.INITIALIZING
        self._configuration_service = configuration_service
        self._queued_typing: Optional[TypistRequest] = None

        self._control = control_service
        self._control.connect(CONTROL_EVENT_SIGNAL, self._on_control_event)

        self._recorder = recorder_service
        self._recorder.connect(RECORDER_RESPONSE_SIGNAL, self._on_recorder_response)
        self._recorder.connect(VOLUME_LEVEL_SIGNAL, self._on_volume_level)

        self._transcriber = transcriber_service
        self._transcriber.connect(
            TRANSCRIBER_RESPONSE_SIGNAL, self._on_transcriber_response
        )

        self._typist = typist_service
        self._typist.connect(TYPIST_RESPONSE_SIGNAL, self._on_typist_response)

        self._extension = extension_service

        self._send_event(OrchestratorStage.READY, "Ready.")
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def _send_event(
        self,
        stage: OrchestratorStage,
        message: Optional[str] = None,
        success: bool = True,
    ):
        self._stage = stage
        self._extension.set_app_status(stage)
        if success is False and message is not None:
            self._extension.set_app_error(message)
        event = OrchestratorEvent(stage=stage, success=success, message=message)
        self.safe_emit(ORCHESTRATOR_EVENT_SIGNAL, event.model_dump_json())

    #
    # Public API
    #

    def triggered(self) -> None:
        if self._stage in [
            OrchestratorStage.INITIALIZING,
            OrchestratorStage.TRANSCRIBING,
            OrchestratorStage.TYPING,
        ]:
            self._logger.warning(f"Ignoring trigger in {self._stage.name} stage.")
            return

        if self._stage == OrchestratorStage.READY:
            self._logger.info("Starting...")
            self._action_start(OrchestratorRequest())
        elif self._stage == OrchestratorStage.RECORDING:
            self._logger.info("Stopping...")
            self._action_stop()

    def _action_start(self, request: OrchestratorRequest) -> None:
        if self._recorder.is_recording():
            self._logger.warning("Already recording.")
            return
        self._send_event(OrchestratorStage.RECORDING, "Listening...")
        self._queued_typing = None
        self._recorder.start_recording()

    def _action_stop(self) -> None:
        if not self._recorder.is_recording():
            self._logger.warning("Not recording.")
            return
        self._recorder.stop_recording()

    def action_type(self) -> None:
        if self._queued_typing:
            self._typist.type_async(self._queued_typing)

    #
    # Private API
    #

    def _on_control_event(self, service, encoded: str):
        try:
            control_event = ControlEvent.model_validate_json(encoded)
            if control_event.button == JoystickButton.Left:
                language_id = self._configuration_service.config.joystick_language_left
                if not is_empty(language_id):
                    self._logger.info(f"Language set to {language_id}.")
                    self._configuration_service.config.language = language_id
            elif control_event.button == JoystickButton.Right:
                language_id = self._configuration_service.config.joystick_language_right
                if not is_empty(language_id):
                    self._logger.info(f"Language set to {language_id}.")
                    self._configuration_service.config.language = language_id
            elif control_event.button == JoystickButton.B:
                self.triggered()
        except Exception as e:
            self._logger.error(f"Error handling control event: {e}")

    def _on_recorder_response(self, service, encoded: str):
        try:
            recorder_response = RecorderResponse.model_validate_json(encoded)
            if not recorder_response.success:
                return self._send_event(
                    stage=OrchestratorStage.READY,
                    message=recorder_response.message,
                    success=recorder_response.success,
                )

            self._send_event(OrchestratorStage.TRANSCRIBING, "Transcribing...")
            self._transcriber.transcribe_async(
                request=TranscriberRequest(recorder_response=recorder_response)
            )
        except Exception as e:
            self._send_event(
                stage=OrchestratorStage.READY,
                message=f"Error handling recorder response: {e}",
                success=False,
            )

    def _on_volume_level(self, service, volume: float):
        """Handle volume level updates from the recorder."""
        self.safe_emit(VOLUME_LEVEL_SIGNAL, volume)

    def _on_transcriber_response(self, service, encoded: str):
        try:
            transcriber_response = TranscriberResponse.model_validate_json(encoded)
            if not transcriber_response.success:
                return self._send_event(
                    stage=OrchestratorStage.READY,
                    message=transcriber_response.message,
                    success=transcriber_response.success,
                )

            if transcriber_response.is_empty():
                return self._send_event(
                    stage=OrchestratorStage.READY,
                    message="Nothing to type, transcription is empty.",
                    success=False,
                )

            self._queued_typing = TypistRequest(
                transcriber_response=transcriber_response
            )
            self._send_event(OrchestratorStage.TYPING, "Typing...")
        except Exception as e:
            self._send_event(
                stage=OrchestratorStage.READY,
                message=f"Error handling transcriber response: {e}",
                success=False,
            )

    def _on_typist_response(self, service, encoded: str):
        try:
            typist_response = TypistResponse.model_validate_json(encoded)
            if not typist_response.success:
                return self._send_event(
                    stage=OrchestratorStage.READY,
                    message=typist_response.message,
                    success=typist_response.success,
                )
            self._send_event(OrchestratorStage.READY, "Ready.")
        except Exception as e:
            self._send_event(
                stage=OrchestratorStage.READY,
                message=f"Error handling typist response: {e}",
                success=False,
            )
