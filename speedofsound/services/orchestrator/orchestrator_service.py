from typing import Optional

from gi.repository import Gdk, GObject  # type: ignore

from speedofsound.constants import (
    CONTROL_EVENT_SIGNAL,
    LANGUAGE_NAME_SIGNAL,
    MODEL_NAME_SIGNAL,
    ORCHESTRATOR_EVENT_SIGNAL,
    RECORDER_RESPONSE_SIGNAL,
    TRANSCRIBER_RESPONSE_SIGNAL,
    TYPIST_RESPONSE_SIGNAL,
    VOLUME_LEVEL_SIGNAL,
    WORDS_PER_MINUTE_SIGNAL,
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
from speedofsound.services.benchmark import BenchmarkService
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.context import ContextService
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
        LANGUAGE_NAME_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
        MODEL_NAME_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
        WORDS_PER_MINUTE_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (float,)),
    }

    def __init__(
        self,
        configuration: ConfigurationService,
        context: ContextService,
        control: ControlService,
        recorder: RecorderService,
        transcriber: TranscriberService,
        typist: TypistService,
        extension: ExtensionService,
        benchmark: BenchmarkService,
    ):
        super().__init__(service_name=self.SERVICE_NAME)
        self._stage = OrchestratorStage.INITIALIZING
        self._queued_typing: Optional[TypistRequest] = None
        self._recording_cancelled = False
        self._last_transcriber_request: Optional[TranscriberRequest] = None

        self._configuration_service = configuration
        self._context = context

        self._control = control
        self._control.connect(CONTROL_EVENT_SIGNAL, self._on_control_event)

        self._recorder = recorder
        self._recorder.connect(RECORDER_RESPONSE_SIGNAL, self._on_recorder_response)
        self._recorder.connect(VOLUME_LEVEL_SIGNAL, self._on_volume_level)

        self._transcriber = transcriber
        self._transcriber.connect(
            TRANSCRIBER_RESPONSE_SIGNAL, self._on_transcriber_response
        )

        self._typist = typist
        self._typist.connect(TYPIST_RESPONSE_SIGNAL, self._on_typist_response)

        self._extension = extension
        self._benchmark = benchmark

        self._total_seconds = 0
        self._total_words = 0

        self._clipboard: Optional[Gdk.Clipboard] = None
        self._setup_clipboard()

        self._update_status_bar()
        self._send_event(OrchestratorStage.READY, "Ready.")
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def _setup_clipboard(self):
        if not self._configuration_service.copy_to_clipboard:
            return

        display = Gdk.Display.get_default()
        if display is None:
            self._logger.warning(
                "Failed to get default GDK display, copy to clipboard will not work."
            )
            return

        self._clipboard = display.get_clipboard()
        if self._clipboard is None:
            self._logger.warning(
                "Failed to get GNOME clipboard, copy to clipboard will not work."
            )

    def _update_status_bar(self):
        self.safe_emit(
            LANGUAGE_NAME_SIGNAL,
            self._configuration_service.language,
        )
        self.safe_emit(
            MODEL_NAME_SIGNAL,
            self._configuration_service.config.transcriber.replace("_", " "),
        )

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

        # Update the active app before we present our own
        self._context.update_active_app()

        self._send_event(OrchestratorStage.RECORDING, "Listening...")
        self._total_seconds = 0
        self._total_words = 0
        self._queued_typing = None
        self._recording_cancelled = False
        self._recorder.start_recording()

    def _action_stop(self) -> None:
        if not self._recorder.is_recording():
            self._logger.warning("Not recording.")
            return
        self._recorder.stop_recording()

    def action_type(self) -> None:
        if self._queued_typing:
            self._typist.type_async(self._queued_typing)

    def cancel_recording(self) -> None:
        if self._stage == OrchestratorStage.RECORDING:
            self._logger.info("Canceling recording...")
            self._recording_cancelled = True
            self._recorder.stop_recording()

    def copy_to_clipboard(self, text: str) -> None:
        try:
            if self._clipboard is None:
                return
            self._clipboard.set(text)
            self._logger.info(f"Copied {len(text)} characters to clipboard")
        except Exception as e:
            self._logger.error(f"Error copying to clipboard: {e}")

    #
    # Private API
    #

    def _on_control_event(self, _service, encoded: str):
        try:
            control_event = ControlEvent.model_validate_json(encoded)
            if control_event.button == JoystickButton.Left:
                language_id = self._configuration_service.joystick_language_left
                if not is_empty(language_id):
                    self._configuration_service.language = language_id
                    self.safe_emit(LANGUAGE_NAME_SIGNAL, language_id)
            elif control_event.button == JoystickButton.Right:
                language_id = self._configuration_service.joystick_language_right
                if not is_empty(language_id):
                    self._configuration_service.language = language_id
                    self.safe_emit(LANGUAGE_NAME_SIGNAL, language_id)
            elif control_event.button == JoystickButton.B:
                self.triggered()
        except Exception as e:
            self._logger.error(f"Error handling control event: {e}")

    def _on_recorder_response(self, _service, encoded: str):
        try:
            recorder_response = RecorderResponse.model_validate_json(encoded)
            if not recorder_response.success:
                return self._send_event(
                    stage=OrchestratorStage.READY,
                    message=recorder_response.message,
                    success=recorder_response.success,
                )

            # If recording was cancelled, skip transcription and go directly to ready
            if self._recording_cancelled:
                self._recording_cancelled = False
                return self._send_event(OrchestratorStage.READY)

            self._total_seconds = recorder_response.get_duration_seconds()
            self._send_event(OrchestratorStage.TRANSCRIBING, "Transcribing...")
            self._last_transcriber_request = TranscriberRequest(
                recorder_response=recorder_response,
                simple_prompt=self._context.get_simple_prompt(
                    self._configuration_service.language
                ),
                prompt=self._context.get_prompt(self._configuration_service.language),
            )
            self._transcriber.transcribe_async(request=self._last_transcriber_request)
        except Exception as e:
            self._send_event(
                stage=OrchestratorStage.READY,
                message=f"Error handling recorder response: {e}",
                success=False,
            )

    def _on_volume_level(self, _service, volume: float):
        """Handle volume level updates from the recorder."""
        self.safe_emit(VOLUME_LEVEL_SIGNAL, volume)

    def _calculate_and_emit_wpm(self) -> None:
        """Calculate and emit words per minute (WPM) with upper bound check."""
        if self._total_seconds > 0 and self._total_words > 0:
            wpm = 1.0 * self._total_words / (self._total_seconds / 60)
            self.safe_emit(WORDS_PER_MINUTE_SIGNAL, min(wpm, 999.0))

    def _on_transcriber_response(self, _service, encoded: str):
        try:
            transcriber_response = TranscriberResponse.model_validate_json(encoded)

            if self._last_transcriber_request:
                self._benchmark.save_transcription(
                    self._last_transcriber_request, transcriber_response
                )

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

            self._total_words = transcriber_response.get_total_words()
            self._calculate_and_emit_wpm()

            if self._configuration_service.copy_to_clipboard:
                self.copy_to_clipboard(transcriber_response.get_text())

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

    def _on_typist_response(self, _service, encoded: str):
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
