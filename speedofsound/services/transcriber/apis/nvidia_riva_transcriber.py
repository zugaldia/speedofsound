"""

Documentation on `docs/nvidia.md` needs to be updated when this file is modified.

"""

from copy import deepcopy

import riva.client

from speedofsound.models import (
    RecorderResponse,
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis.base_nvidia_transcriber import (
    BaseNvidiaTranscriber,
)
from speedofsound.utils import is_empty


class NvidiaRivaTranscriber(BaseNvidiaTranscriber):
    def __init__(self, configuration: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.NVIDIA_RIVA,
            configuration=configuration,
        )

        self._client = None

        # Test
        # models = self._get_models_config()
        # self._logger.info(f"Available models: {models}")

        self._logger.info("Initialized.")

    def shutdown(self):
        self._client = None

    def get_name(self) -> str:
        return "NVIDIA Riva"

    def get_available_models(self) -> list[TranscriberModel]:
        # The list of available models depends on the local installation
        # and cannot be known beforehand. See _get_models_config() below.
        return []

    def is_ready(self) -> bool:
        return self._configuration_service.config.nvidia_riva.enabled

    def _ensure_client(self):
        if self._client:
            return

        config = self._configuration_service.config.nvidia_riva
        use_ssl = config.ssl
        endpoint = (
            config.endpoint if not is_empty(config.endpoint) else "localhost:50051"
        )

        auth = riva.client.Auth(uri=endpoint, use_ssl=use_ssl)
        self._client = riva.client.ASRService(auth)

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            return self._transcribe(request)
        except Exception as e:
            message = f"NVIDIA Riva transcription failed: {e}"
            self._logger.error(message)
            return TranscriberResponse(success=False, message=message)

    def _transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        config = self._configuration_service.config

        # TODO: Is "multi" allowed for some models?
        # https://docs.nvidia.com/deeplearning/riva/user-guide/docs/asr/asr-overview.html#multilingual-models
        language = self._get_language_code(config.language)

        model_id = (
            config.nvidia_riva.model if not is_empty(config.nvidia_riva.model) else None
        )

        self._logger.info(f"Transcribing (language={language}, model={model_id}).")

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

        # Unlike with NVIDIA NIM, we cannot use offline_recognize() because
        # in Jetson devices, only online is available by default.
        # https://forums.developer.nvidia.com/t/asr-offline-model-not-found-on-jetson-orin/235580
        streaming_config = riva.client.StreamingRecognitionConfig(
            config=deepcopy(recognition_config), interim_results=False
        )

        self._ensure_client()
        response_generator = self._client.streaming_response_generator(
            [RecorderResponse.data_decode(request.recorder_response.data)],
            streaming_config,
        )

        intermediates = []
        for response in response_generator:
            try:
                transcript = response.results[0].alternatives[0].transcript
                is_final = response.results[0].is_final
                if transcript and is_final:
                    intermediates.append(transcript)
            except Exception:
                pass

        final_transcript = "".join(intermediates)
        return TranscriberResponse(text=final_transcript.strip())
