from datetime import datetime
from typing import Optional

from pydantic import BaseModel


class BenchmarkEntry(BaseModel):
    """Represents a saved transcription entry for benchmarking."""

    session_id: str
    timestamp: datetime
    language: str
    transcriber: str
    audio_file_path: str
    duration_seconds: float
    text: str
    confidence: Optional[float] = None
    prompt: str
    simple_prompt: str
