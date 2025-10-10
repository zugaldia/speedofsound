import threading
from typing import Optional

from gi.repository import Gst  # type: ignore

from speedofsound.models import AudioDevice, RecorderRequest
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.recorder.base_recorder import BaseRecorder


class GStreamerRecorder(BaseRecorder):
    def __init__(self, configuration: ConfigurationService) -> None:
        super().__init__(provider_name="gstreamer")
        Gst.init()
        self._configuration = configuration
        self._pipeline: Optional[Gst.Pipeline] = None
        self._appsink: Optional[Gst.Element] = None
        self._audio_data: list[bytes] = []
        self._is_recording: bool = False
        self._recording_lock: threading.Lock = threading.Lock()
        self._scan_audio_devices()
        self._logger.info(f"GStreamer recorder v{Gst.version_string()} initialized.")

    def shutdown(self) -> None:
        self._logger.info("Shutting down.")
        with self._recording_lock:
            if self._pipeline:
                self._pipeline.set_state(Gst.State.NULL)
                self._pipeline = None
            self._appsink = None
            self._audio_data.clear()

    def _scan_audio_devices(self) -> None:
        """Enumerate available audio input devices."""
        if self._configuration is None:
            return

        try:
            devices = []
            device_monitor = Gst.DeviceMonitor.new()
            device_monitor.add_filter("Audio/Source", None)
            device_monitor.start()
            gst_devices: list[Gst.Device] = device_monitor.get_devices() or []
            for gst_device in gst_devices:
                device_properties = gst_device.get_properties()
                if device_properties:
                    device_name = (
                        device_properties.get_string("node.name")
                        or device_properties.get_string("alsa.card_name")
                        or device_properties.get_string("api.alsa.card.name")
                    )
                    display_name = (
                        gst_device.get_display_name()
                        or device_properties.get_string("node.description")
                        or device_name
                    )
                    if device_name and display_name:
                        devices.append(
                            AudioDevice(
                                device_name=device_name,
                                display_name=display_name,
                            )
                        )
            device_monitor.stop()
            self._configuration.set_available_microphone_devices(devices)
        except Exception as e:
            self._logger.error(f"Failed to scan audio devices: {e}")
            self._configuration.set_available_microphone_devices([])

    def is_recording(self) -> bool:
        with self._recording_lock:
            return self._is_recording

    def start_recording(self, recorder_request: RecorderRequest) -> None:
        self._logger.info(f"Recorder request: {recorder_request}")
        with self._recording_lock:
            self._audio_data = []
            self._sample_width = recorder_request.sample_width

            # Create pipeline
            format_map = {1: "S8", 2: "S16LE", 4: "S32LE"}
            audio_format = format_map.get(recorder_request.sample_width, "S16LE")

            # Get microphone device from configuration
            device_param = ""
            if self._configuration:
                device_name = self._configuration.microphone_device
                if device_name:
                    device_param = f" device={device_name}"
                    self._logger.info(f"Using microphone device: {device_name}")

            pipeline_str = (
                f"pulsesrc{device_param} ! "
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
