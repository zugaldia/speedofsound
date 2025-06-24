from speedofsound.services.transcriber.apis.base_transcriber import BaseTranscriber
from speedofsound.services.transcriber.apis.elevenlabs_transcriber import (
    ElevenLabsTranscriber,
)
from speedofsound.services.transcriber.apis.fastest_transcriber import (
    FastestTranscriber,
)
from speedofsound.services.transcriber.apis.google_transcriber import GoogleTranscriber
from speedofsound.services.transcriber.apis.nvidia_nim_transcriber import (
    NvidiaNimTranscriber,
)
from speedofsound.services.transcriber.apis.nvidia_riva_transcriber import (
    NvidiaRivaTranscriber,
)
from speedofsound.services.transcriber.apis.openai_transcriber import OpenAiTranscriber
from speedofsound.services.transcriber.apis.whisper_transcriber import (
    WhisperTranscriber,
)

__all__ = [
    "BaseTranscriber",
    # Direct integrations
    "ElevenLabsTranscriber",
    "GoogleTranscriber",
    "NvidiaNimTranscriber",
    "NvidiaRivaTranscriber",
    "OpenAiTranscriber",
    "WhisperTranscriber",
    # Meta
    "FastestTranscriber",
]
