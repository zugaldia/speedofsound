from gi.repository import GObject  # type: ignore

from speedofsound.constants import RECORDER_RESPONSE_SIGNAL, VOLUME_LEVEL_SIGNAL
from speedofsound.models import RecorderRequest, RecorderResponse
from speedofsound.services.base_service import BaseService
from speedofsound.services.configuration.configuration_service import (
    ConfigurationService,
)
from speedofsound.services.recorder.pyaudio_recorder import PyAudioRecorder


class RecorderService(BaseService):
    SERVICE_NAME = "recorder"

    __gsignals__ = {
        RECORDER_RESPONSE_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
        VOLUME_LEVEL_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (float,)),
    }

    def __init__(
        self,
        configuration_service: ConfigurationService,
        pyaudio_recorder: PyAudioRecorder,
    ):
        super().__init__(service_name=self.SERVICE_NAME)
        self._configuration = configuration_service
        self._recorder = pyaudio_recorder
        self._recorder.set_volume_callback(self._on_volume_level)
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def _on_volume_level(self, volume: float):
        """Handle volume level updates from the recorder."""
        self.safe_emit(VOLUME_LEVEL_SIGNAL, volume)

    def is_recording(self) -> bool:
        return self._recorder.is_recording()

    def start_recording(self):
        try:
            # TODO: Set a limit on the recording time
            recorder_request = RecorderRequest()
            recorder_request.input_device = self._configuration.config.microphone_id
            self._recorder.start_recording(recorder_request)
            self._logger.info("Started recording.")
        except Exception as e:
            self._logger.error(f"Error starting recording: {e}")
            response = RecorderResponse(
                success=False,
                message=f"Error starting recording: {str(e)}",
                recorder_request=RecorderRequest(),
            )
            self.safe_emit(RECORDER_RESPONSE_SIGNAL, response.model_dump_json())

    def stop_recording(self):
        try:
            data = self._recorder.stop_recording()
            encoded = RecorderResponse.data_encode(data)
            recorder_result = RecorderResponse(
                recorder_request=RecorderRequest(), data=encoded
            )
            serialized = recorder_result.model_dump_json()
            self._logger.info(f"Stopped recording: {len(serialized)} bytes")
            self.safe_emit(RECORDER_RESPONSE_SIGNAL, serialized)
        except Exception as e:
            self._logger.error(f"Error stopping recording: {e}")
            response = RecorderResponse(
                success=False,
                message=f"Error stopping recording: {str(e)}",
                recorder_request=RecorderRequest(),
            )
            self.safe_emit(RECORDER_RESPONSE_SIGNAL, response.model_dump_json())
