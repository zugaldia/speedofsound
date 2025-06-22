from concurrent.futures import ThreadPoolExecutor
from typing import List, Optional, Tuple, Any

import pyaudio

from speedofsound.models import MicrophoneDevice, RecorderRequest
from speedofsound.services.recorder.base_recorder import BaseRecorder


class PyAudioRecorder(BaseRecorder):
    def __init__(self) -> None:
        super().__init__(provider_name="pyaudio")
        self._audio = pyaudio.PyAudio()
        self._stream: Optional[pyaudio.Stream] = None
        self._frames: List[bytes] = []
        self._buffer_count: int = 0
        self._executor = ThreadPoolExecutor()
        self._logger.info(f"PyAudio recorder v{pyaudio.__version__} initialized.")

    def shutdown(self) -> None:
        self._logger.info("Shutting down.")
        if self._stream:
            self._stream.stop_stream()
            self._stream.close()
        self._audio.terminate()

    def get_input_devices(self) -> List[MicrophoneDevice]:
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

    def _stream_callback(
        self, in_data: bytes, frame_count: int, time_info: Any, status: int
    ) -> Tuple[None, int]:
        self._frames.append(in_data)
        self._executor.submit(self._calculate_rms_volume, in_data)
        return (None, pyaudio.paContinue)

    def stop_recording(self) -> bytes:
        self._logger.info("Stopping.")
        if self._stream is not None:
            self._stream.stop_stream()
            self._stream.close()
        self._stream = None
        return b"".join(self._frames)
