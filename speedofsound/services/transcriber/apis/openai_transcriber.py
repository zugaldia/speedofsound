"""

Documentation on `docs/openai.md` needs to be updated when this file is modified.

"""

from openai import OpenAI

from speedofsound.constants import CUSTOM_MODEL_VALUE, DEFAULT_OPENAI_MODEL
from speedofsound.models import (
    RecorderResponse,
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber
from speedofsound.utils import is_empty


class OpenAiTranscriber(BaseTranscriber):
    def __init__(self, configuration: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.OPENAI,
            configuration=configuration,
        )

        available_models = self.get_available_models()
        configuration.set_available_openai_models(available_models)

        self._client = None
        self._logger.info("Initialized.")

    def shutdown(self):
        self._client = None

    def get_name(self) -> str:
        return "OpenAI"

    def get_available_models(self) -> list[TranscriberModel]:
        return [
            TranscriberModel(
                id="gpt-audio",
                name="GPT Audio",
            ),
            TranscriberModel(
                id="gpt-audio-mini",
                name="GPT Audio Mini",
            ),
            TranscriberModel(
                id="gpt-4o-audio-preview",
                name="GPT-4o Audio (Preview)",
            ),
            TranscriberModel(
                id="gpt-4o-mini-audio-preview",
                name="GPT-4o Mini Audio (Preview)",
            ),
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

    def _get_client(self) -> OpenAI:
        if self._client:
            return self._client

        base_url = self._configuration_service.openai_base_url
        base_url = None if is_empty(base_url) else base_url
        api_key = self._configuration_service.openai_api_key
        self._client = OpenAI(base_url=base_url, api_key=api_key)
        return self._client

    def _get_model_id(self) -> str:
        """Get the effective model ID to use for transcription."""
        custom_model = self._configuration_service.openai_custom_model
        if not is_empty(custom_model):
            return custom_model
        openai_model = self._configuration_service.openai_model
        if openai_model != CUSTOM_MODEL_VALUE and not is_empty(openai_model):
            return openai_model
        return DEFAULT_OPENAI_MODEL

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            # VLLM models are not compatible with the OpenAI's
            # audio.transcriptions.create() method.
            base_url = self._configuration_service.openai_base_url
            is_local = base_url and base_url.startswith("http://localhost")
            is_transcriptions = self._get_model_id() in [
                "gpt-4o-transcribe",
                "gpt-4o-mini-transcribe",
                "whisper-1",
            ]

            if is_transcriptions and not is_local:
                return self._audio_transcriptions(request)
            else:
                return self._chat_completions(request)
        except Exception as e:
            self._logger.error(e)
            return TranscriberResponse(
                success=False,
                message=f"OpenAI transcription failed: {e}",
            )

    def _audio_transcriptions(self, request: TranscriberRequest) -> TranscriberResponse:
        # While Whisper allows "auto" for automatic language detection, I can't
        # find documentation that the OpenAI's cloud API does the same thing.
        language = self._configuration_service.language
        model_id = self._get_model_id()
        self._logger.info(f"Transcribing (language={language}, model={model_id}).")

        client = self._get_client()
        transcription = client.audio.transcriptions.create(
            file=request.recorder_response.get_file_like_object(),
            model=model_id,
            language=language,
            prompt=request.simple_prompt,
            temperature=0.0,
        )

        return TranscriberResponse(text=transcription.text)

    def _chat_completions(self, request: TranscriberRequest) -> TranscriberResponse:
        model_id = self._get_model_id()
        wav_file = request.recorder_response.get_file_like_object()
        wav_data = RecorderResponse.data_encode(wav_file.read())
        self._logger.info(f"Transcribing (model={model_id}, size={len(wav_data)}).")

        client = self._get_client()
        transcription = client.chat.completions.create(
            model=model_id,
            modalities=["text"],
            temperature=0.0,
            messages=[
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": request.prompt,
                        },
                        {
                            "type": "input_audio",
                            "input_audio": {
                                "data": wav_data,
                                "format": "wav",
                            },
                        },
                    ],
                },
            ],
        )

        return TranscriberResponse(text=transcription.choices[0].message.content)
