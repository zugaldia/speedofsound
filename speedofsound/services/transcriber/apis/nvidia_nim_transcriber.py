"""

Documentation on `docs/nvidia.md` needs to be updated when this file is modified.

"""

import riva.client

from speedofsound.models import (
    RecorderResponse,
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration.configuration_service import (
    ConfigurationService,
)
from speedofsound.services.transcriber.apis.base_nvidia_transcriber import (
    BaseNvidiaTranscriber,
)
from speedofsound.utils import is_empty


class NvidiaNimTranscriber(BaseNvidiaTranscriber):
    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.NVIDIA_NIM,
            configuration_service=configuration_service,
        )

        self._client = None

        # Test
        # models = self._get_models_config()
        # self._logger.info(f"Available models: {models}")

        self._logger.info("Initialized.")

    def shutdown(self):
        self._client = None

    def get_name(self) -> str:
        return "NVIDIA NIM"

    # https://build.nvidia.com/explore/speech
    # https://build.nvidia.com/search?filters=usecase%3Ausecase_speech_to_text
    def get_available_models(self) -> list[TranscriberModel]:
        return [
            TranscriberModel(
                id="ee8dc628-76de-4acc-8595-1836e7e857bd",
                name="Canary 1B",
            ),
            TranscriberModel(
                id="c367545c-964a-42b1-b16f-40c262ae3646",
                name="Canary 0.6B Turbo",
            ),
            TranscriberModel(
                id="71203149-d3b7-4460-8231-1be2543a1fca",
                name="Parakeet 1.1B RNNT Multilingual",
            ),
            TranscriberModel(
                id="1598d209-5e27-4d3c-8079-4751568b1081",
                name="Parakeet CTC 1.1B",
            ),
            TranscriberModel(
                id="d8dd4e9b-fbf5-4fb0-9dba-8cf436c8d965",
                name="Parakeet CTC 0.6B",
            ),
            TranscriberModel(
                id="b702f636-f60c-4a3d-a6f4-f3568c13bd7d",
                name="Whisper Large v3",
            ),
            TranscriberModel(
                id="58801c94-37cf-45ee-8911-4d851f095957",
                name="Conformer CTC",
            ),
        ]

    def is_ready(self) -> bool:
        return self._configuration_service.config.nvidia_nim.enabled

    def _ensure_client(self):
        if self._client:
            return

        config = self._configuration_service.config.nvidia_nim
        api_key = config.api_key
        use_ssl = config.ssl
        endpoint = (
            config.endpoint
            if not is_empty(config.endpoint)
            else "grpc.nvcf.nvidia.com:443"
        )
        model_id = (
            config.model
            if not is_empty(config.model)
            else self.get_available_models()[0].id
        )

        auth = riva.client.Auth(
            uri=endpoint,
            use_ssl=use_ssl,
            metadata_args=[
                ["function-id", model_id],
                ["authorization", f"Bearer {api_key}"],
            ],
        )

        self._client = riva.client.ASRService(auth)

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            return self._transcribe(request)
        except Exception as e:
            message = f"NVIDIA NIM transcription failed: {e}"
            self._logger.error(message)
            return TranscriberResponse(success=False, message=message)

    # Docs: https://github.com/nvidia-riva/python-clients/blob/main/scripts/asr/transcribe_file_offline.py
    def _transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        config = self._configuration_service.config

        language = (
            None
            if config.language_auto or is_empty(config.language)
            else self._get_language_code(config.language)
        )

        self._logger.info(f"Transcribing (language={language}).")

        # TODO: Support `speech_contexts`?
        recognition_config = riva.client.RecognitionConfig(
            encoding=riva.client.AudioEncoding.LINEAR_PCM,
            language_code=language,
            max_alternatives=1,
            profanity_filter=False,
            enable_automatic_punctuation=True,
            sample_rate_hertz=request.recorder_response.recorder_request.rate,
            audio_channel_count=request.recorder_response.recorder_request.channels,
        )

        self._ensure_client()
        response = self._client.offline_recognize(
            RecorderResponse.data_decode(request.recorder_response.data),
            recognition_config,
        )

        final_transcript = ""
        if len(response.results) > 0 and len(response.results[0].alternatives) > 0:
            for result in response.results:
                final_transcript += result.alternatives[0].transcript

        return TranscriberResponse(text=final_transcript.strip())
