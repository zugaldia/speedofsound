from pathlib import Path
from typing import Optional

from faster_whisper import WhisperModel
from faster_whisper import __version__ as faster_whisper_version
from faster_whisper import available_models, download_model

from speedofsound.models import (
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber
from speedofsound.utils import get_data_path, is_empty


class FasterWhisperTranscriber(BaseTranscriber):
    def __init__(self, configuration: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.FASTER_WHISPER,
            configuration=configuration,
        )

        model_name = configuration.config.faster_whisper.model
        model_path = self._download_model(model_name)
        self._load_model(model_path)
        self._logger.info(f"v{faster_whisper_version} initialized.")

    def shutdown(self):
        pass

    def _get_data_path(self, model_name: str) -> Path:
        data_path = get_data_path() / self._provider_name / model_name
        data_path.mkdir(parents=True, exist_ok=True)
        return data_path

    def _download_model(self, model_name: str):
        """Download the Whisper model if it is not already available."""
        if model_name not in available_models():
            raise ValueError(
                f"Model {model_name} is not supported. "
                f"Available models: {available_models()}"
            )

        output_dir = str(self._get_data_path(model_name))
        self._logger.info(f"Checking for {model_name} in {output_dir}")

        # This does nothing if the model has already been downloaded
        model_path = download_model(size_or_id=model_name, output_dir=output_dir)
        return model_path

    def _get_and_check_language(self) -> Optional[str]:
        """Get and check the language configuration."""
        language = self._configuration_service.language
        if language not in self._model.supported_languages:
            raise ValueError(
                f"Language {language} is not supported by the model. "
                f"Supported languages: {self._model.supported_languages}"
            )
        return language

    def _load_model(self, model_path: str):
        """Load the Whisper model."""
        self._logger.info(f"Loading model from {model_path}...")
        device = self._configuration_service.config.faster_whisper.device
        self._model = WhisperModel(model_size_or_path=model_path, device=device)
        self._get_and_check_language()

    def get_name(self) -> str:
        """Get the name of the transcriber."""
        return "Faster Whisper"

    def get_available_models(self) -> list[TranscriberModel]:
        """Get the list of available models for the provider."""
        return [
            TranscriberModel(id=model_name, name=model_name)
            for model_name in available_models()
        ]

    def is_ready(self) -> bool:
        """Check if the transcriber is ready to process requests."""
        return self._configuration_service.config.faster_whisper.enabled

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        """Transcribe audio content from the request."""
        audio = request.recorder_response.get_file_like_object()
        language = self._get_and_check_language()
        segments, _ = self._model.transcribe(audio=audio, language=language)
        results = [
            segment.text.strip() for segment in segments if not is_empty(segment.text)
        ]

        return TranscriberResponse(text=" ".join(results))
