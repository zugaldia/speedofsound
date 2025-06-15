import audioop
import typing
from concurrent.futures import ThreadPoolExecutor

import pyaudio

from speedofsound.models import MicrophoneDevice, RecorderRequest
from speedofsound.services.base_provider import BaseProvider


class PyAudioRecorder(BaseProvider):
    def __init__(self):
        super().__init__(provider_name="pyaudio")
        self._audio = pyaudio.PyAudio()
        self._stream = None
        self._frames = []
        self._volume_callback = None
        self._max_rms = 0.0
        self._buffer_count = 0
        self._executor = ThreadPoolExecutor()
        self._sample_width = None
        self._logger.info(f"PyAudio recorder v{pyaudio.__version__} initialized.")

    def shutdown(self):
        self._logger.info("Shutting down.")
        if self._stream:
            self._stream.stop_stream()
            self._stream.close()
        self._audio.terminate()

    def get_input_devices(self) -> typing.List[MicrophoneDevice]:
        devices = []
        for i in range(self._audio.get_device_count()):
            device = self._audio.get_device_info_by_index(i)
            if device["maxInputChannels"] > 0:
                devices.append(
                    MicrophoneDevice(
                        id=device["index"],
                        name=device["name"],
                    )
                )

        return devices

    def is_recording(self) -> bool:
        return self._stream is not None and self._stream.is_active()

    def set_volume_callback(self, callback):
        """Set callback function to receive volume level updates."""
        self._volume_callback = callback

    def start_recording(self, recorder_request: RecorderRequest) -> None:
        self._logger.info(f"Recorder request: {recorder_request}")
        format = self._audio.get_format_from_width(recorder_request.sample_width)
        input_device_index = (
            recorder_request.input_device
            if recorder_request.input_device is not None
            and recorder_request.input_device >= 0
            else None
        )

        self._frames = []
        self._buffer_count = 0
        self._sample_width = recorder_request.sample_width
        self._stream = self._audio.open(
            rate=recorder_request.rate,
            channels=recorder_request.channels,
            format=format,
            input=True,
            input_device_index=input_device_index,
            frames_per_buffer=recorder_request.frames_per_buffer,
            start=True,
            stream_callback=self._stream_callback,
        )

    def _stream_callback(self, in_data, frame_count, time_info, status):
        self._frames.append(in_data)
        self._executor.submit(self._calculate_rms_volume, in_data)
        return (None, pyaudio.paContinue)

    def _calculate_rms_volume(self, audio_data: bytes):
        """Calculate RMS volume from audio data, normalized to 0.0-1.0."""
        if not self._volume_callback or not self._sample_width:
            return 0.0
        try:
            rms = audioop.rms(audio_data, self._sample_width)
            self._max_rms = max(self._max_rms, rms)
            rms_normalized = rms / self._max_rms if self._max_rms > 0 else 0.0
            self._volume_callback(rms_normalized)
        except Exception as e:
            self._logger.error(f"Error calculating RMS volume: {e}")
            return 0.0

    def stop_recording(self) -> bytes:
        self._logger.info("Stopping.")
        if self._stream is not None:
            self._stream.stop_stream()
            self._stream.close()
        self._stream = None
        return b"".join(self._frames)
