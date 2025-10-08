"""

Documentation on `docs/openai.md` needs to be updated when this file is modified.

"""

from openai import OpenAI
from openai.types.chat import (
    ChatCompletionContentPartInputAudioParam,
    ChatCompletionContentPartTextParam,
    ChatCompletionUserMessageParam,
)
from openai.types.chat.chat_completion_content_part_input_audio_param import InputAudio

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

    def _get_client(self) -> OpenAI:
        if self._client:
            return self._client

        base_url = self._configuration_service.config.openai.base_url
        api_key = self._configuration_service.config.openai.api_key
        self._client = OpenAI(base_url=base_url, api_key=api_key)
        return self._client

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            base_url = self._configuration_service.config.openai.base_url
            if base_url and base_url.startswith("http://localhost"):
                # VLLM models are not compatible with the OpenAI's
                # audio.transcriptions.create() method.
                return self._transcribe_completions(request)
            else:
                return self._transcribe(request)
        except Exception as e:
            message = f"OpenAI transcription failed: {e}"
            self._logger.error(message)
            return TranscriberResponse(success=False, message=message)

    def _transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        config = self._configuration_service.config

        # While Whisper allows "auto" for automatic language detection, I can't
        # find documentation that the OpenAI's cloud API does the same thing.
        language = self._configuration_service.language

        model_id = (
            config.openai.model
            if not is_empty(config.openai.model)
            else self.get_available_models()[0].id
        )

        self._logger.info(f"Transcribing (language={language}, model={model_id}).")
        client = self._get_client()
        transcription = client.audio.transcriptions.create(
            file=request.recorder_response.get_file_like_object(),
            model=model_id,
            language=language,
            prompt=request.prompt,
            temperature=0.0,
        )

        return TranscriberResponse(text=transcription.text)

    def _transcribe_completions(
        self, request: TranscriberRequest
    ) -> TranscriberResponse:
        config = self._configuration_service.config

        model_id = (
            config.openai.model
            if not is_empty(config.openai.model)
            else self.get_available_models()[0].id
        )

        wav_file = request.recorder_response.get_file_like_object()
        wav_data = RecorderResponse.data_encode(wav_file.read())

        self._logger.info(f"Transcribing with completions (model={model_id}).")
        client = self._get_client()
        transcription = client.chat.completions.create(
            model=model_id,
            messages=[
                ChatCompletionUserMessageParam(
                    role="user",
                    content=[
                        ChatCompletionContentPartTextParam(
                            type="text",
                            text=request.simple_prompt,
                        ),
                        ChatCompletionContentPartInputAudioParam(
                            type="input_audio",
                            input_audio=InputAudio(format="wav", data=wav_data),
                        ),
                    ],
                )
            ],
            # Params from
            # https://huggingface.co/ibm-granite/granite-speech-3.3-2b#usage-with-vllm
            temperature=0.2,
            max_tokens=64,
        )

        return TranscriberResponse(text=transcription.choices[0].message.content)
