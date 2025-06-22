from concurrent.futures import ThreadPoolExecutor
from typing import Any, List, Optional, Tuple

import pyaudio

from speedofsound.models import MicrophoneDevice, RecorderRequest
from speedofsound.services.recorder.base_recorder import BaseRecorder


class PyAudioRecorder(BaseRecorder):
    def __init__(self) -> None:
        super().__init__(provider_name="pyaudio")
        self._devices: List[MicrophoneDevice] = []
        self._audio = pyaudio.PyAudio()
        self._stream: Optional[pyaudio.Stream] = None
        self._audio_data: List[bytes] = []
        self._buffer_count: int = 0
        self._executor = ThreadPoolExecutor()
        self._logger.info(f"PyAudio recorder v{pyaudio.__version__} initialized.")

    def shutdown(self) -> None:
        self._logger.info("Shutting down.")
        if self._stream:
            self._stream.stop_stream()
            self._stream.close()
        self._audio.terminate()
        self._audio_data.clear()

    def get_input_devices(self) -> List[MicrophoneDevice]:
        if self._devices:
            return self._devices
        for i in range(self._audio.get_device_count()):
            device = self._audio.get_device_info_by_index(i)
            if device["maxInputChannels"] > 0:
                self._devices.append(
                    MicrophoneDevice(id=device["index"], name=device["name"])
                )
        return self._devices

    def is_recording(self) -> bool:
        return self._stream is not None and self._stream.is_active()

    def start_recording(self, recorder_request: RecorderRequest) -> None:
        self._logger.info(f"Recorder request: {recorder_request}")
        format = self._audio.get_format_from_width(recorder_request.sample_width)
        input_device_index = (
            recorder_request.microphone_id
            if recorder_request.microphone_id is not None
            and recorder_request.microphone_id >= 0
            else None
        )

        self._audio_data = []
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

    def _stream_callback(
        self, in_data: bytes, frame_count: int, time_info: Any, status: int
    ) -> Tuple[None, int]:
        self._audio_data.append(in_data)
        self._executor.submit(self._calculate_rms_volume, in_data)
        return (None, pyaudio.paContinue)

    def stop_recording(self) -> bytes:
        self._logger.info("Stopping.")
        if self._stream is not None:
            self._stream.stop_stream()
            self._stream.close()
        self._stream = None
        return b"".join(self._audio_data)
