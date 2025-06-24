from concurrent.futures import Future, ThreadPoolExecutor
from typing import List

from gi.repository import GObject  # type: ignore

from speedofsound.constants import TRANSCRIBER_RESPONSE_SIGNAL
from speedofsound.models import TranscriberRequest, TranscriberResponse, TranscriberType
from speedofsound.services.base_service import BaseService
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import (
    BaseTranscriber,
    ElevenLabsTranscriber,
    FastestTranscriber,
    GoogleTranscriber,
    NvidiaNimTranscriber,
    NvidiaRivaTranscriber,
    OpenAiTranscriber,
    WhisperTranscriber,
)


class TranscriberService(BaseService):
    SERVICE_NAME = "transcriber"

    __gsignals__ = {
        TRANSCRIBER_RESPONSE_SIGNAL: (GObject.SignalFlags.RUN_FIRST, None, (str,)),
    }

    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(service_name=self.SERVICE_NAME)
        self._executor = ThreadPoolExecutor(max_workers=1)
        self._configuration = configuration_service
        self._transcriber: BaseTranscriber = self._setup_transcriber()
        self._logger.info("Initialized.")

    def shutdown(self):
        self._logger.info("Shutting down.")
        self._transcriber.shutdown()
        self._executor.shutdown(wait=True)

    def _setup_transcriber(self) -> BaseTranscriber:
        selected = TranscriberType(self._configuration.config.transcriber)
        if selected == TranscriberType.FASTEST:
            return self._get_fastest_transcriber()
        else:
            return self._get_transcriber(selected)

    def _get_transcriber(self, transcriber_type: TranscriberType) -> BaseTranscriber:
        if transcriber_type == TranscriberType.WHISPER:
            return WhisperTranscriber(configuration_service=self._configuration)
        elif transcriber_type == TranscriberType.NVIDIA_RIVA:
            return NvidiaRivaTranscriber(configuration_service=self._configuration)
        elif transcriber_type == TranscriberType.NVIDIA_NIM:
            return NvidiaNimTranscriber(configuration_service=self._configuration)
        elif transcriber_type == TranscriberType.ELEVENLABS:
            return ElevenLabsTranscriber(configuration_service=self._configuration)
        elif transcriber_type == TranscriberType.GOOGLE:
            return GoogleTranscriber(configuration_service=self._configuration)
        elif transcriber_type == TranscriberType.OPENAI:
            return OpenAiTranscriber(configuration_service=self._configuration)
        else:
            message = f"Unsupported transcriber type: {transcriber_type}"
            self._logger.error(message)
            raise RuntimeError(message)

    def _get_fastest_transcriber(self) -> BaseTranscriber:
        enabled_providers: List[BaseTranscriber] = []
        if self._configuration.config.whisper.enabled:
            enabled_providers.append(self._get_transcriber(TranscriberType.WHISPER))
        if self._configuration.config.nvidia_riva.enabled:
            enabled_providers.append(self._get_transcriber(TranscriberType.NVIDIA_RIVA))
        if self._configuration.config.nvidia_nim.enabled:
            enabled_providers.append(self._get_transcriber(TranscriberType.NVIDIA_NIM))
        if self._configuration.config.elevenlabs.enabled:
            enabled_providers.append(self._get_transcriber(TranscriberType.ELEVENLABS))
        if self._configuration.config.google.enabled:
            enabled_providers.append(self._get_transcriber(TranscriberType.GOOGLE))
        if self._configuration.config.openai.enabled:
            enabled_providers.append(self._get_transcriber(TranscriberType.OPENAI))

        for provider in enabled_providers:
            if not provider.is_ready():
                message = f"Transcriber provider {provider.get_name()} is not ready."
                self._logger.error(message)
                raise RuntimeError(message)

        return FastestTranscriber(
            configuration_service=self._configuration,
            providers=enabled_providers,
        )

    def transcribe_async(self, request: TranscriberRequest):
        self._logger.info("Transcribing...")
        future = self._executor.submit(self._transcriber.transcribe, request)
        future.add_done_callback(self._handle_transcription_result)

    def _handle_transcription_result(self, future: Future):
        try:
            self._logger.info("Transcription completed.")
            result: TranscriberResponse = future.result()
            self.safe_emit(TRANSCRIBER_RESPONSE_SIGNAL, result.model_dump_json())
        except Exception as e:
            message = f"Error during transcription: {str(e)}"
            self._logger.error(message)
            response = TranscriberResponse(success=False, message=message)
            self.safe_emit(TRANSCRIBER_RESPONSE_SIGNAL, response.model_dump_json())
