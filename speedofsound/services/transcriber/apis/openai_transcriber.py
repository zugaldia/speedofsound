"""

Documentation on `docs/openai.md` needs to be updated when this file is modified.

"""

from openai import OpenAI

from speedofsound.models import (
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber
from speedofsound.utils import is_empty


class OpenAiTranscriber(BaseTranscriber):
    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.OPENAI,
            configuration_service=configuration_service,
        )

        self._client = None
        self._logger.info("Initialized.")

    def shutdown(self):
        self._client = None

    def get_name(self) -> str:
        return "OpenAI"

    # https://platform.openai.com/docs/api-reference/audio/createTranscription#audio-createtranscription-model
    def get_available_models(self) -> list[TranscriberModel]:
        return [
            TranscriberModel(
                id="gpt-4o-transcribe",
                name="GPT-4o Transcribe",
            ),
            TranscriberModel(
                id="gpt-4o-mini-transcribe",
                name="GPT-4o Mini Transcribe",
            ),
            TranscriberModel(
                id="whisper-1",
                name="Whisper v2",
            ),
        ]

    def is_ready(self) -> bool:
        return self._configuration_service.config.openai.enabled

    def _ensure_client(self):
        if self._client:
            return

        api_key = self._configuration_service.config.openai.api_key
        self._client = OpenAI(api_key=api_key)

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            return self._transcribe(request)
        except Exception as e:
            message = f"OpenAI transcription failed: {e}"
            self._logger.error(message)
            return TranscriberResponse(success=False, message=message)

    def _transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        config = self._configuration_service.config

        # While Whisper allows "auto" for automatic language detection, I can't
        # find documentation that the OpenAI's cloud API does the same thing.
        language = config.language

        model_id = (
            config.openai.model
            if not is_empty(config.openai.model)
            else self.get_available_models()[0].id
        )

        self._logger.info(f"Transcribing (language={language}, model={model_id}).")

        self._ensure_client()
        prompt = self.get_prompt(language)
        transcription = self._client.audio.transcriptions.create(
            file=request.recorder_response.get_file_like_object(),
            model=model_id,
            language=language,
            prompt=prompt,
            temperature=0.0,
        )

        self._logger.info(f"Transcription completed: {transcription.text}")
        return TranscriberResponse(text=transcription.text)
