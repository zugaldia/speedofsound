"""

Documentation on `docs/elevenlabs.md` needs to be updated when this file is modified.

"""

from elevenlabs.client import ElevenLabs

from speedofsound.models import (
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber
from speedofsound.utils import is_empty


class ElevenLabsTranscriber(BaseTranscriber):
    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.ELEVENLABS,
            configuration_service=configuration_service,
        )

        self._client = None
        self._logger.info("Initialized.")

    def shutdown(self):
        self._client = None

    def get_name(self) -> str:
        return "ElevenLabs"

    # https://elevenlabs.io/docs/api-reference/speech-to-text/convert#request.body.model_id.model_id
    def get_available_models(self) -> list[TranscriberModel]:
        return [
            TranscriberModel(
                id="scribe_v1",
                name="Scribe v1",
            ),
            TranscriberModel(
                id="scribe_v1_experimental",
                name="Scribe v1 Experimental",
            ),
        ]

    def is_ready(self) -> bool:
        return self._configuration_service.config.elevenlabs.enabled

    def _ensure_client(self):
        if self._client:
            return

        api_key = self._configuration_service.config.elevenlabs.api_key
        self._client = ElevenLabs(api_key=api_key)

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            return self._transcribe(request)
        except Exception as e:
            message = f"ElevenLabs transcription failed: {e}"
            self._logger.error(message)
            return TranscriberResponse(success=False, message=message)

    def _transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        config = self._configuration_service.config

        language = config.language
        model_id = (
            config.elevenlabs.model
            if not is_empty(config.elevenlabs.model)
            else self.get_available_models()[0].id
        )

        self._logger.info(f"Transcribing (language={language}, model={model_id}).")

        self._ensure_client()
        transcription = self._client.speech_to_text.convert(
            file=request.recorder_response.get_file_like_object(),
            model_id=model_id,
            language_code=language,
        )

        return TranscriberResponse(text=transcription.text)
