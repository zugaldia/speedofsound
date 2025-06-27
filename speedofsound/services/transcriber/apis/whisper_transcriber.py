"""

Documentation on `docs/whisper.md` needs to be updated when this file is modified.

TODO: Docs
$ ./build/bin/whisper-server --port 1234 --model ./models/ggml-medium.bin
$ ./whisper-tiny.en.llamafile --server --port 1234 --gpu nvidia

FIXME: https://github.com/Mozilla-Ocho/llamafile/issues/728

TODO: It now supports PHI-4. Does this implementation work or
do we need to add a separate provider?
https://github.com/Mozilla-Ocho/llamafile/releases/tag/0.9.3

"""

import requests

from speedofsound.models import (
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber
from speedofsound.utils import is_empty


class WhisperTranscriber(BaseTranscriber):
    def __init__(self, configuration_service: ConfigurationService):
        super().__init__(
            provider_type=TranscriberType.WHISPER,
            configuration_service=configuration_service,
        )

        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def get_name(self) -> str:
        return "Whisper"

    def get_available_models(self) -> list[TranscriberModel]:
        # Not configurable because for Whisper.cpp's server, you don't specify
        # the model in the request. Instead, you specify the model when you
        # launch it with the CLI (--model). And in the case of Whisperfile,
        # it's implicitely selected with the download.
        return []

    def is_ready(self) -> bool:
        return self._configuration_service.config.whisper.enabled

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        try:
            return self._transcribe(request)
        except Exception as e:
            message = f"Whisper transcription failed: {e}"
            self._logger.error(message)
            return TranscriberResponse(success=False, message=message)

    def _transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        config = self._configuration_service.config

        language = config.language
        endpoint = (
            config.whisper.endpoint
            if not is_empty(config.whisper.endpoint)
            else "127.0.0.1:8080"
        )
        full_endpoint = f"http://{endpoint}/inference"

        self._logger.info(
            f"Transcribing (language={language}, endpoint={full_endpoint})."
        )

        file_path = request.recorder_response.save_tmp_file()
        files = {"file": open(file_path, "rb")}
        data = {
            "temperature": "0.0",
            "response_format": "json",
            "language": language,
        }

        response = requests.post(
            full_endpoint,
            files=files,
            data=data,
        )

        parsed = response.json()
        self._logger.info(f"Parsed: {parsed}")

        if "error" in parsed:
            self._logger.error(f"Error: {parsed['error']}")
            return TranscriberResponse(success=False, message=parsed["error"])
        return TranscriberResponse(text=parsed["text"].strip())
