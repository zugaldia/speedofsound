import json
import shutil
from datetime import datetime
from pathlib import Path

from speedofsound.models import TranscriberRequest, TranscriberResponse
from speedofsound.services.base_service import BaseService
from speedofsound.services.benchmark.models import BenchmarkEntry
from speedofsound.services.configuration import ConfigurationService
from speedofsound.utils import get_data_path


class BenchmarkService(BaseService):
    SERVICE_NAME = "benchmark"

    def __init__(self, configuration: ConfigurationService):
        super().__init__(service_name=self.SERVICE_NAME)
        self._configuration = configuration
        self._benchmark_path = get_data_path() / "benchmarks"
        self._benchmark_path.mkdir(parents=True, exist_ok=True)
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def save_transcription(
        self, request: TranscriberRequest, response: TranscriberResponse
    ) -> None:
        if not self._configuration.save_transcriptions:
            return
        if not response.success:
            self._logger.debug("Skipping failed transcription")
            return

        try:
            session_id = self._generate_session_id()
            session_dir = self._benchmark_path / session_id
            session_dir.mkdir(parents=True, exist_ok=True)
            audio_path = self._save_audio(session_dir, request)
            entry = BenchmarkEntry(
                session_id=session_id,
                timestamp=datetime.now(),
                language=self._configuration.language,
                transcriber=self._configuration.preferred_transcriber,
                audio_file_path=str(audio_path),
                duration_seconds=request.recorder_response.get_duration_seconds(),
                text=response.text or "",
                confidence=response.confidence,
                prompt=request.prompt,
                simple_prompt=request.simple_prompt,
            )

            self._save_metadata(session_dir, entry)
            self._logger.info(f"Saved transcription benchmark: {session_id}")
        except Exception as e:
            self._logger.error(f"Failed to save transcription: {e}")

    def _generate_session_id(self) -> str:
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S_%f")
        return f"{timestamp}"

    def _save_audio(self, session_dir: Path, request: TranscriberRequest) -> Path:
        audio_path = session_dir / "audio.wav"
        tmp_audio_path = request.recorder_response.save_tmp_file()
        shutil.copy(tmp_audio_path, audio_path)
        return audio_path

    def _save_metadata(self, session_dir: Path, entry: BenchmarkEntry) -> None:
        metadata_path = session_dir / "metadata.json"
        with open(metadata_path, "w") as f:
            json.dump(entry.model_dump(mode="json"), f, indent=2, default=str)
