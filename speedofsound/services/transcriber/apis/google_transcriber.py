"""

Documentation on `docs/google.md` needs to be updated when this file is modified.

"""

from google import genai
from google.genai import types

from speedofsound.models import (
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber
from speedofsound.utils import is_empty


class GoogleTranscriber(BaseTranscriber):
    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.GOOGLE,
            configuration_service=configuration_service,
        )

        self._client = None
        self._logger.info("Initialized.")

    def shutdown(self):
        self._client = None

    def get_name(self) -> str:
        return "Google Gemini"

    # https://ai.google.dev/gemini-api/docs/models#model-variations
    def get_available_models(self) -> list[TranscriberModel]:
        return [
            TranscriberModel(
                id="gemini-2.5-pro-preview-06-05",
                name="Gemini 2.5 Pro",
            ),
            TranscriberModel(
                id="gemini-2.5-flash-preview-05-20",
                name="Gemini 2.5 Flash",
            ),
        ]

    def is_ready(self) -> bool:
        return self._configuration_service.config.google.enabled

    def _ensure_client(self):
        if self._client:
            return

        # TODO: Add support for Vertex AI
        api_key = self._configuration_service.config.google.api_key
        self._client = genai.Client(api_key=api_key)

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            return self._transcribe(request)
        except Exception as e:
            message = f"Google transcription failed: {e}"
            self._logger.error(message)
            return TranscriberResponse(success=False, message=message)

    def _transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        config = self._configuration_service.config

        language = (
            None
            if config.language_auto or is_empty(config.language)
            else config.language
        )
        model_id = (
            config.google.model
            if not is_empty(config.google.model)
            else self.get_available_models()[0].id
        )

        self._logger.info(f"Transcribing (language={language}, model={model_id}).")

        # https://ai.google.dev/gemini-api/docs/audio#inline-audio
        audio_file = types.Part.from_bytes(
            data=request.recorder_response.get_file_like_object().read(),
            mime_type="audio/wav",
        )

        # TODO: This requires an additional round trip to the server but
        # would allow larger files. The inline audio method is limited to 20MB.
        # https://ai.google.dev/gemini-api/docs/audio#upload-audio
        # audio_file = self._client.files.upload(
        #     file=request.recorder_response.get_file_like_object(),
        #     config={"mime_type": "audio/wav"},
        # )

        # TODO: Stream response
        self._ensure_client()
        prompt = self.get_prompt(language)
        response = self._client.models.generate_content(
            model=model_id,
            contents=[prompt, audio_file],
        )

        # TODO: Delete uploaded file?
        # https://ai.google.dev/gemini-api/docs/files#delete-uploaded
        self._logger.info(f"Transcription completed: {response.text}")
        return TranscriberResponse(text=response.text)
