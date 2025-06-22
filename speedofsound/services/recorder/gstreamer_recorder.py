import threading
from typing import List, Optional

from gi.repository import Gst  # type: ignore

from speedofsound.models import MicrophoneDevice, RecorderRequest
from speedofsound.services.recorder.base_recorder import BaseRecorder


class GStreamerRecorder(BaseRecorder):
    def __init__(self) -> None:
        super().__init__(provider_name="gstreamer")
        Gst.init()
        self._pipeline: Optional[Gst.Pipeline] = None
        self._appsink: Optional[Gst.Element] = None
        self._audio_data: List[bytes] = []
        self._is_recording: bool = False
        self._recording_lock: threading.Lock = threading.Lock()
        self._logger.info(f"GStreamer recorder v{Gst.version_string()} initialized.")

    def shutdown(self) -> None:
        self._logger.info("Shutting down.")
        with self._recording_lock:
            if self._pipeline:
                self._pipeline.set_state(Gst.State.NULL)
                self._pipeline = None

    def get_input_devices(self) -> List[MicrophoneDevice]:
        devices = []
        monitor = Gst.DeviceMonitor.new()
        monitor.add_filter("Audio/Source", None)
        monitor.start()
        for device in monitor.get_devices():
            device_class = device.get_device_class()
            if "Audio/Source" in device_class:
                display_name = device.get_display_name()
                device_name = device.get_properties().get_string("device.path")
                if not device_name:
                    device_name = display_name
                devices.append(MicrophoneDevice(id=len(devices), name=device_name))

        monitor.stop()
        return devices

    def is_recording(self) -> bool:
        with self._recording_lock:
            return self._is_recording

    def start_recording(self, recorder_request: RecorderRequest) -> None:
        self._logger.info(f"Recorder request: {recorder_request}")

        with self._recording_lock:
            if self._is_recording:
                self._logger.warning("Already recording, stopping current recording")
                self._stop_recording_internal()

            self._audio_data = []
            self._sample_width = recorder_request.sample_width

            # Create pipeline
            format_map = {1: "S8", 2: "S16LE", 4: "S32LE"}
            audio_format = format_map.get(recorder_request.sample_width, "S16LE")
            pipeline_str = (
                f"pulsesrc ! "
                f"audio/x-raw,format={audio_format},"
                f"channels={recorder_request.channels},"
                f"rate={recorder_request.rate} ! "
                f"appsink name=sink emit-signals=true"
            )

            self._pipeline = Gst.parse_launch(pipeline_str)
            self._appsink = self._pipeline.get_by_name("sink")

            # Connect to new-sample signal
            self._appsink.connect("new-sample", self._on_new_sample)

            # Start pipeline
            self._pipeline.set_state(Gst.State.PLAYING)
            self._is_recording = True

    def stop_recording(self) -> bytes:
        with self._recording_lock:
            return self._stop_recording_internal()

    def _stop_recording_internal(self) -> bytes:
        self._logger.info("Stopping.")

        if self._pipeline:
            self._pipeline.set_state(Gst.State.NULL)
            self._pipeline = None
            self._appsink = None

        self._is_recording = False
        audio_data = b"".join(self._audio_data)
        self._audio_data = []

        return audio_data

    def _on_new_sample(self, appsink: Gst.Element) -> Gst.FlowReturn:
        sample = appsink.emit("pull-sample")
        if sample:
            buffer = sample.get_buffer()
            success, map_info = buffer.map(Gst.MapFlags.READ)
            if success:
                audio_chunk = map_info.data
                self._audio_data.append(audio_chunk)
                self._calculate_rms_volume(audio_chunk)
                buffer.unmap(map_info)
        return Gst.FlowReturn.OK
