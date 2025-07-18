import concurrent.futures
from typing import Dict, List

from speedofsound.models import TranscriberRequest, TranscriberResponse, TranscriberType
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber


class FastestTranscriber(BaseTranscriber):
    """Meta transcriber that runs multiple providers and returns the fastest result."""

    def __init__(
        self,
        configuration: ConfigurationService,
        providers: List[BaseTranscriber],
    ):
        super().__init__(
            provider_type=TranscriberType.FASTEST,
            configuration=configuration,
        )
        self._providers: Dict[str, BaseTranscriber] = {
            provider._provider_name: provider for provider in providers
        }

        provider_names = ", ".join(self._providers.keys())
        self._logger.info(f"Initialized with providers: {provider_names}")

    def shutdown(self) -> None:
        pass

    def get_name(self) -> str:
        return "Fastest"

    def transcribe(self, request: TranscriberRequest) -> TranscriberResponse:
        """Run all providers and return the first successful result."""
        result = None
        self._logger.info(f"Running {len(self._providers)} transcribers.")
        executor = concurrent.futures.ThreadPoolExecutor(
            max_workers=len(self._providers)
        )

        try:
            future_to_name: Dict[concurrent.futures.Future, str] = {
                executor.submit(provider.transcribe, request): name
                for name, provider in self._providers.items()
            }

            for completed_future in concurrent.futures.as_completed(
                future_to_name.keys()
            ):
                provider_name = future_to_name[completed_future]
                current_result = completed_future.result()
                if current_result and current_result.success:
                    self._logger.info(f"Provider {provider_name} won")
                    result = current_result
                    break
        finally:
            executor.shutdown(wait=False, cancel_futures=True)

        return (
            result
            if result
            else TranscriberResponse(
                success=False, message="All transcription providers failed."
            )
        )
