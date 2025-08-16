"""Verification utilities for testing system configuration."""

import time

from speedofsound.models import (
    RecorderRequest,
    RecorderResponse,
    TranscriberRequest,
    TranscriberResponse,
    TypistRequest,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.recorder import RecorderService
from speedofsound.services.transcriber import TranscriberService
from speedofsound.services.typist import TypistService

KEYBOARD_TEST_MESSAGE = "Hello from Speed of Sound."
COUNTDOWN_SECONDS = 5
RECORDING_SECONDS = 10

LINK_DOCS = "https://github.com/zugaldia/speedofsound/blob/main/README.md"
LINK_ISSUES = "https://github.com/zugaldia/speedofsound/issues"


class SystemVerification:
    """Provides methods to verify system configuration and functionality."""

    def __init__(self):
        self._configuration = ConfigurationService()

    def _countdown(self, seconds: int = COUNTDOWN_SECONDS) -> None:
        """Display a countdown timer."""
        for i in range(seconds, 0, -1):
            print(f"T minus {i}...")
            time.sleep(1)

    def verify_keyboard(self) -> int:
        """Verify virtual keyboard configuration and functionality."""
        typist_service = None
        try:
            typist_service = TypistService(configuration=self._configuration)
            return self._verify_keyboard(typist_service)
        except Exception as e:
            print(f"Keyboard verification failed: {e}")
            return 1
        finally:
            if typist_service is not None:
                typist_service.shutdown()

    def _verify_keyboard(self, typist_service: TypistService) -> int:
        """Internal keyboard verification implementation."""
        print(
            f"We'll type '{KEYBOARD_TEST_MESSAGE}' in {COUNTDOWN_SECONDS} seconds.\n"
            f"You can now switch to any app where you want the text to appear."
        )

        backend = (
            self._configuration._config.typist_backend
            if self._configuration._config.typist_backend
            else "default"
        )

        self._countdown()
        print(f"-> Typing now with {backend} backend.")
        mock_transcriber_response = TranscriberResponse(
            text=KEYBOARD_TEST_MESSAGE, confidence=1.0, success=True
        )

        request = TypistRequest(transcriber_response=mock_transcriber_response)
        response = typist_service._typist.type(request)
        print(
            "If you didn't see the text, check your configuration.\n"
            f"Documentation is available at {LINK_DOCS}.\n"
            f"If you think this is a bug, please report it at {LINK_ISSUES}."
        )

        return 0 if response.success else 1

    def verify_speech(self) -> int:
        """Verify speech recognition configuration and functionality."""
        recorder_service = None
        transcriber_service = None
        try:
            recorder_service = RecorderService(
                configuration=self._configuration
            )
            transcriber_service = TranscriberService(
                configuration=self._configuration
            )
            return self._verify_speech(recorder_service, transcriber_service)
        except Exception as e:
            print(f"Speech verification failed: {e}")
            return 1
        finally:
            if recorder_service is not None:
                recorder_service.shutdown()
            if transcriber_service is not None:
                transcriber_service.shutdown()

    def _verify_speech(
        self,
        recorder_service: RecorderService,
        transcriber_service: TranscriberService,
    ) -> int:
        print(f"We'll start recording in {COUNTDOWN_SECONDS} seconds.")

        self._countdown()
        print(f"Recording now for {RECORDING_SECONDS} seconds, say something :-)")

        recorder_service.start_recording()
        time.sleep(RECORDING_SECONDS)
        recorder_data = recorder_service._recorder.stop_recording()
        print(f"Recording stopped, received {len(recorder_data)} bytes of audio data.")
        encoded_data = RecorderResponse.data_encode(recorder_data)
        recorder_response = RecorderResponse(
            recorder_request=RecorderRequest(), data=encoded_data
        )

        backend = self._configuration._config.transcriber
        print(f"Transcribing the recorded audio with {backend} backend.")
        transcriber_request = TranscriberRequest(recorder_response=recorder_response)
        transcriber_response = transcriber_service._transcriber.transcribe(
            transcriber_request
        )

        print(f"-> Transcription: '{transcriber_response.text}'")
        print(
            "Speech verification completed.\n"
            f"If you didn't see the transcription, check your configuration.\n"
            f"Documentation is available at {LINK_DOCS}.\n"
            f"If you think this is a bug, please report it at {LINK_ISSUES}."
        )

        return 0 if transcriber_response.success else 1
