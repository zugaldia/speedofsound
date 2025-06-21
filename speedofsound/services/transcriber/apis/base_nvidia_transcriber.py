import riva.client

from speedofsound.models import TranscriberType
from speedofsound.services.configuration import ConfigurationService
from speedofsound.services.transcriber.apis import BaseTranscriber


class BaseNvidiaTranscriber(BaseTranscriber):
    def __init__(
        self,
        provider_type: TranscriberType,
        configuration_service: ConfigurationService,
    ):
        super().__init__(
            provider_type=provider_type,
            configuration_service=configuration_service,
        )

    def _get_models_config(self):
        """
        Typical entry:
        [{  'type': 'offline',
            'streaming': 'True',
            'sample_rate': '16000',
            'offline': 'True',
            'model_family': 'riva',
            'language_code': 'en-US,en-GB,es-ES,ar-AR,es-US,pt-BR,fr-FR,de-DE,it-IT,ja-JP,ko-KR,ru-RU,hi-IN'}]
        """
        models = []
        try:
            self._ensure_client()
            config_response = self._client.stub.GetRivaSpeechRecognitionConfig(
                riva.client.proto.riva_asr_pb2.RivaSpeechRecognitionConfigRequest()
            )

            for model_config in config_response.model_config:
                models.append(
                    {
                        "type": model_config.parameters["type"],
                        "streaming": model_config.parameters["streaming"],
                        "sample_rate": model_config.parameters["sample_rate"],
                        "offline": model_config.parameters["offline"],
                        "model_family": model_config.parameters["model_family"],
                        "language_code": model_config.parameters["language_code"],
                    }
                )
        except Exception as e:
            self._logger.error(f"Failed to get models config: {e}")
        return models

    def _get_language_code(self, language_id: str) -> str:
        # Mapping of language IDs to language codes, based on:
        # https://docs.nvidia.com/deeplearning/riva/user-guide/docs/asr/asr-overview.html#multilingual-models
        mapping = {
            "ar": "ar-AR",
            "cs": "cs-CZ",
            "da": "da-DK",
            "de": "de-DE",
            "en": "en-US",
            "es": "es-ES",
            "fr": "fr-FR",
            "he": "he-IL",
            "hi": "hi-IN",
            "it": "it-IT",
            "ja": "ja-JP",
            "ko": "ko-KR",
            "nb": "nb-NO",
            "nl": "nl-NL",
            "nn": "nn-NO",
            "pl": "pl-PL",
            "pt": "pt-PT",
            "ru": "ru-RU",
            "sv": "sv-SE",
            "th": "th-TH",
            "tr": "tr-TR",
        }

        return mapping.get(language_id)
