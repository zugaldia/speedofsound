from speedofsound.services.transcriber.apis.base_transcriber import BaseTranscriber
from speedofsound.services.transcriber.apis.fallback_transcriber import (
    FallbackTranscriber,
)
from speedofsound.services.transcriber.apis.faster_whisper_transcriber import (
    FasterWhisperTranscriber,
)
from speedofsound.services.transcriber.apis.fastest_transcriber import (
    FastestTranscriber,
)
from speedofsound.services.transcriber.apis.openai_transcriber import OpenAiTranscriber

__all__ = [
    "BaseTranscriber",
    # Direct integrations
    "FasterWhisperTranscriber",
    "OpenAiTranscriber",
    # Meta
    "FallbackTranscriber",
    "FastestTranscriber",
]
