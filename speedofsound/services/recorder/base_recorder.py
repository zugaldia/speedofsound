import audioop
from abc import ABC, abstractmethod

from speedofsound.models import RecorderRequest
from speedofsound.services.base_provider import BaseProvider


class BaseRecorder(BaseProvider, ABC):
    def __init__(self, provider_name: str):
        super().__init__(provider_name=provider_name)
        self._volume_callback = None
        self._max_rms = 0.0
        self._sample_width = None

    @abstractmethod
    def shutdown(self) -> None:
        pass

    @abstractmethod
    def is_recording(self) -> bool:
        pass

    @abstractmethod
    def start_recording(self, recorder_request: RecorderRequest) -> None:
        pass

    @abstractmethod
    def stop_recording(self) -> bytes:
        pass

    def set_volume_callback(self, callback) -> None:
        """Set callback function to receive volume level updates."""
        self._volume_callback = callback

    def _calculate_rms_volume(self, audio_data: bytes) -> None:
        """Calculate RMS volume from audio data, normalized to 0.0-1.0."""
        if not self._volume_callback or not self._sample_width:
            return
        try:
            rms = audioop.rms(audio_data, self._sample_width)
            self._max_rms = max(self._max_rms, rms)
            rms_normalized = rms / self._max_rms if self._max_rms > 0 else 0.0
            self._volume_callback(rms_normalized)
        except Exception as e:
            self._logger.error(f"Error calculating RMS volume: {e}")
