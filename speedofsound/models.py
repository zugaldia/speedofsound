import base64
import io
import wave
from enum import IntEnum, StrEnum
from typing import Literal, Optional

from gi.repository import GObject  # type: ignore
from pydantic import BaseModel, Field

from speedofsound.utils import get_cache_path, get_uuid

#
# Language
#


class Language(BaseModel):
    # The ID is the ISO 639-1 code for the language, which is used by
    # e.g. Whisper and OpenAI Transcriptions API.
    # https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes
    id: str
    name: str


LANGUAGE_ENGLISH = Language(id="en", name="English")
LANGUAGE_SPANISH = Language(id="es", name="Spanish")
DEFAULT_LANGUAGE = LANGUAGE_ENGLISH


#
# Configuration
#


class TranscriberType(StrEnum):
    # Local
    FASTER_WHISPER = "faster_whisper"

    # Cloud
    OPENAI = "openai"

    # Hybrid
    FALLBACK = "fallback"


class DisplayServer(StrEnum):
    X11 = "x11"
    WAYLAND = "wayland"
    UNKNOWN = "unknown"


class TypistBackend(StrEnum):
    ATSPI = "atspi"
    XDOTOOL = "xdotool"
    YDOTOOL = "ydotool"


class FasterWhisperConfig(BaseModel):
    """Faster Whisper transcriber configuration."""

    enabled: bool = False
    model: str = "small"
    device: Literal["cpu", "cuda", "auto"] = "auto"


class OpenAIConfig(BaseModel):
    """OpenAI transcriber configuration."""

    enabled: bool = False
    base_url: Optional[str] = None
    api_key: str = ""
    model: str = ""


class FallbackConfig(BaseModel):
    """Fallback transcriber configuration."""

    timeout_seconds: float = Field(default=2.0, ge=0.1, le=10.0)


class ContextConfig(BaseModel):
    """Context service configuration."""

    include_application: bool = False


class AppConfig(BaseModel):
    """Application configuration loaded from config.toml."""

    language: str = DEFAULT_LANGUAGE.id

    # Optional: This is the ID of the joystick as detected by PyGame.
    # The id argument must be a value from 0 to pygame.joystick.get_count() - 1.
    joystick_id: Optional[int] = None
    joystick_language_left: str = LANGUAGE_ENGLISH.id
    joystick_language_right: str = LANGUAGE_SPANISH.id

    # Transcriber settings
    transcriber: str = TranscriberType.FASTER_WHISPER.value

    # Typist settings
    typist_backend: Optional[str] = None

    # Context settings
    context: ContextConfig = ContextConfig()

    # Provider configurations
    faster_whisper: FasterWhisperConfig = FasterWhisperConfig()
    openai: OpenAIConfig = OpenAIConfig()
    fallback: FallbackConfig = FallbackConfig()


#
# Shared across services
#


class BaseRequest(BaseModel):
    pass


class BaseResponse(BaseModel):
    success: bool = True
    message: Optional[str] = None


class BaseEvent(BaseModel):
    pass


#
# Orchestrator
#


class OrchestratorStage(GObject.GEnum):
    INITIALIZING = 1
    READY = 2
    RECORDING = 3
    TRANSCRIBING = 4
    TYPING = 5


class OrchestratorRequest(BaseRequest):
    session_id: str = get_uuid()


class OrchestratorEvent(BaseEvent):
    stage: OrchestratorStage
    success: bool = True
    message: Optional[str] = None


#
# Supporting models
#


class ActiveApplication(BaseModel):
    application_name: Optional[str] = None
    window_name: Optional[str] = None


#
# Controller
#


# This mapping is valid for the 8BitDo SN30 Pro Gamepad, which is detected
# by PyGame as a Xbox 360 Controller. We might need to add support for other
# configurations in the future.
class JoystickButton(IntEnum):
    B = 0
    A = 1
    Y = 2
    X = 3
    Left = 4
    Right = 5
    Select = 6
    Start = 7
    Home = 8


class JoystickDevice(BaseModel):
    id: int
    name: str


class ControlEvent(BaseEvent):
    button: JoystickButton


#
# Recorder
#


class RecorderRequest(BaseRequest):
    rate: int = 16000
    channels: int = 1
    sample_width: int = 2
    frames_per_buffer: int = 1024


class RecorderResponse(BaseResponse):
    recorder_request: RecorderRequest
    data: Optional[str] = None

    @staticmethod
    def data_encode(data: bytes) -> str:
        return base64.b64encode(data).decode("utf-8")

    @staticmethod
    def data_decode(data: str) -> bytes:
        return base64.b64decode(data)

    def get_file_like_object(self) -> io.BytesIO:
        wav_file = io.BytesIO()
        wav_file.name = "speedofsound.wav"
        with wave.open(wav_file, "wb") as wav_object:
            wav_object.setnchannels(self.recorder_request.channels)
            wav_object.setframerate(self.recorder_request.rate)
            wav_object.setsampwidth(self.recorder_request.sample_width)
            wav_object.writeframes(RecorderResponse.data_decode(self.data))

        wav_file.seek(0)
        return wav_file

    def save_tmp_file(self):
        path = str(get_cache_path() / "speedofsound.wav")
        with wave.open(path, "wb") as wf:
            wf.setnchannels(self.recorder_request.channels)
            wf.setframerate(self.recorder_request.rate)
            wf.setsampwidth(self.recorder_request.sample_width)
            wf.writeframes(RecorderResponse.data_decode(self.data))
        return path

    def get_duration_seconds(self) -> float:
        """Calculate the duration of the recording in seconds."""
        if self.data is None:
            return 0.0

        raw_bytes = RecorderResponse.data_decode(self.data)
        total_frames = len(raw_bytes) / (
            self.recorder_request.channels * self.recorder_request.sample_width
        )

        return total_frames / self.recorder_request.rate


#
# Transcriber
#


# TODO: This could be a good place to add a list of supported languages
# by model and implement the proper checks?
class TranscriberModel(BaseModel):
    id: str
    name: str


class TranscriberRequest(BaseRequest):
    recorder_response: RecorderResponse
    simple_prompt: str
    prompt: str


class TranscriberResponse(BaseResponse):
    text: Optional[str] = None
    confidence: Optional[float] = None

    def is_empty(self) -> bool:
        return self.text is None or self.text == "" or self.text.isspace()

    def get_text(self) -> str:
        # Add an extra space at the end to separate multiple sentences in the same paragraph.
        return "" if self.is_empty() else self.text + " "

    def get_total_words(self) -> int:
        """Get the total number of words in the transcribed text."""
        return 0 if self.is_empty() else len(self.text.split())


#
# Typist
#


class TypistRequest(BaseRequest):
    transcriber_response: TranscriberResponse


class TypistResponse(BaseResponse):
    pass
