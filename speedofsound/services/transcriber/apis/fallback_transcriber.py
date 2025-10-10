import concurrent.futures
import time
from typing import Dict, List

from speedofsound.models import (
    TranscriberModel,
    TranscriberRequest,
    TranscriberResponse,
    TranscriberType,
)
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber


class FallbackTranscriber(BaseTranscriber):
    """Meta transcriber that uses cloud provider as primary with local fallback."""

    def __init__(
        self,
        configuration: ConfigurationService,
        providers: List[BaseTranscriber],
    ):
        super().__init__(
            provider_type=TranscriberType.FALLBACK,
            configuration=configuration,
        )

        # Map providers by type for easy access
        self._providers: Dict[TranscriberType, BaseTranscriber] = {
            provider._provider_type: provider for provider in providers
        }

        # Validate we have the required providers
        if TranscriberType.OPENAI not in self._providers:
            raise ValueError("FallbackTranscriber requires OpenAI provider")
        if TranscriberType.FASTER_WHISPER not in self._providers:
            raise ValueError("FallbackTranscriber requires FasterWhisper provider")

        self._primary = self._providers[TranscriberType.OPENAI]
        self._fallback = self._providers[TranscriberType.FASTER_WHISPER]
        self._timeout = configuration.fallback_timeout_seconds
        self._logger.info(
            f"Initialized with primary={self._primary.get_name()}, "
            f"fallback={self._fallback.get_name()}, "
            f"timeout={self._timeout}s"
        )

    def shutdown(self) -> None:
        pass

    def get_name(self) -> str:
        return "Fallback"

    def get_available_models(self) -> list[TranscriberModel]:
        """Return empty list since Fallback doesn't have its own models."""
        return []

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        """Run primary and fallback providers with timeout-based selection."""
        self._logger.info(
            f"Starting transcription with primary={self._primary.get_name()}, "
            f"fallback={self._fallback.get_name()}"
        )

        executor = concurrent.futures.ThreadPoolExecutor(max_workers=2)
        try:
            # Submit both providers immediately
            primary_future = executor.submit(self._primary.transcribe, request)
            fallback_future = executor.submit(self._fallback.transcribe, request)
            start_time = time.time()

            try:
                # Wait for primary with timeout
                primary_result = primary_future.result(timeout=self._timeout)
                elapsed = time.time() - start_time
                if primary_result and primary_result.success:
                    self._logger.info(f"Primary provider succeeded in {elapsed:.3f}s")
                    return primary_result
                else:
                    self._logger.warning(
                        f"Primary provider failed in {elapsed:.3f}s: "
                        f"{primary_result.message if primary_result else 'unknown error'}"
                    )
            except concurrent.futures.TimeoutError:
                elapsed = time.time() - start_time
                self._logger.warning(f"Primary provider timed out after {elapsed:.3f}s")

            # Primary failed or timed out, use fallback
            self._logger.info("Waiting for fallback provider")
            fallback_result = fallback_future.result()
            if fallback_result and fallback_result.success:
                total_elapsed = time.time() - start_time
                self._logger.info(
                    f"Fallback provider succeeded in {total_elapsed:.3f}s total"
                )
                return fallback_result
            else:
                self._logger.error(
                    f"Fallback provider also failed: "
                    f"{fallback_result.message if fallback_result else 'unknown error'}"
                )
                return TranscriberResponse(
                    success=False,
                    message="Both primary and fallback transcription providers failed.",
                )
        finally:
            executor.shutdown(wait=False, cancel_futures=True)
