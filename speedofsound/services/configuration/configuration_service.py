from typing import List, Optional

from gi.repository import Gio, GObject  # type: ignore

from speedofsound.constants import (
    APPLICATION_ID,
    DEFAULT_COPY_TO_CLIPBOARD,
    DEFAULT_EXT_ERROR,
    DEFAULT_EXT_STATUS,
    DEFAULT_FALLBACK_TIMEOUT_SECONDS,
    DEFAULT_FASTER_WHISPER_DEVICE,
    DEFAULT_FASTER_WHISPER_MODEL,
    DEFAULT_INCLUDE_APPLICATION_NAME,
    DEFAULT_JOYSTICK_ID,
    DEFAULT_JOYSTICK_LANGUAGE_LEFT,
    DEFAULT_JOYSTICK_LANGUAGE_RIGHT,
    DEFAULT_LANGUAGE,
    DEFAULT_MICROPHONE_DEVICE,
    DEFAULT_OPENAI_API_KEY,
    DEFAULT_OPENAI_BASE_URL,
    DEFAULT_OPENAI_MODEL,
    DEFAULT_PREFERRED_TRANSCRIBER,
    DEFAULT_RECORDING_TIMEOUT_SECONDS,
    DEFAULT_SAVE_TRANSCRIPTIONS,
    DEFAULT_TYPIST_BACKEND,
    PREFERRED_TRANSCRIBER_CHANGED_SIGNAL,
    SETTING_COPY_TO_CLIPBOARD,
    SETTING_EXT_ERROR,
    SETTING_EXT_STATUS,
    SETTING_FALLBACK_TIMEOUT_SECONDS,
    SETTING_FASTER_WHISPER_DEVICE,
    SETTING_FASTER_WHISPER_MODEL,
    SETTING_INCLUDE_APPLICATION_NAME,
    SETTING_JOYSTICK_ID,
    SETTING_JOYSTICK_LANGUAGE_LEFT,
    SETTING_JOYSTICK_LANGUAGE_RIGHT,
    SETTING_LANGUAGE,
    SETTING_MICROPHONE_DEVICE,
    SETTING_OPENAI_API_KEY,
    SETTING_OPENAI_BASE_URL,
    SETTING_OPENAI_MODEL,
    SETTING_PREFERRED_TRANSCRIBER,
    SETTING_RECORDING_TIMEOUT_SECONDS,
    SETTING_SAVE_TRANSCRIPTIONS,
    SETTING_TYPIST_BACKEND,
)
from speedofsound.models import AudioDevice, JoystickDevice, TranscriberModel
from speedofsound.services.base_service import BaseService


class ConfigurationService(BaseService):
    SERVICE_NAME = "configuration"

    __gsignals__ = {
        PREFERRED_TRANSCRIBER_CHANGED_SIGNAL: (
            GObject.SignalFlags.RUN_FIRST,
            None,
            (str,),
        ),
    }

    def __init__(self):
        super().__init__(service_name=self.SERVICE_NAME)
        self._schema: Optional[Gio.SettingsSchema] = None
        self._settings: Optional[Gio.Settings] = self._initialize_settings()
        self._available_joystick_devices: List[JoystickDevice] = []
        self._available_faster_whisper_models: List[TranscriberModel] = []
        self._available_openai_models: List[TranscriberModel] = []
        self._available_microphone_devices: List[AudioDevice] = []
        self._setup_settings_handlers()
        self._logger.info(
            f"Initialized (GSettings available: {self._settings is not None})."
        )

    def _initialize_settings(self) -> Optional[Gio.Settings]:
        """Initialize GSettings for the application."""
        try:
            source = Gio.SettingsSchemaSource.get_default()
            if source is None:
                self._logger.error("System source schema not found.")
                return None
            schema = source.lookup(schema_id=APPLICATION_ID, recursive=True)
            if schema is None:
                self._logger.error("Application schema not found.")
                return None
            self._schema = schema
            return Gio.Settings.new(schema_id=APPLICATION_ID)
        except Exception as e:
            self._logger.error(f"Failed to initialize settings: {e}")
            return None

    def _setup_settings_handlers(self) -> None:
        """Setup handlers for GSettings changes."""
        if self._settings is None:
            return

        def on_settings_changed(_settings: Gio.Settings, key: str) -> None:
            if key == SETTING_PREFERRED_TRANSCRIBER:
                new_value = self.preferred_transcriber
                self._logger.info(f"Preferred transcriber changed to: {new_value}")
                self.safe_emit(PREFERRED_TRANSCRIBER_CHANGED_SIGNAL, new_value)

        self._settings.connect("changed", on_settings_changed)

    @property
    def settings(self) -> Optional[Gio.Settings]:
        """Get the GSettings instance."""
        return self._settings

    def has_key(self, key: str) -> bool:
        """Check if a key exists in the schema."""
        if self._schema is None:
            return False
        return self._schema.has_key(key)

    def _get_boolean(self, key: str, default: bool) -> bool:
        """Get a boolean setting from GSettings or fallback to default."""
        if self._settings is not None and self.has_key(key):
            return self._settings.get_boolean(key)
        return default

    def _set_boolean(self, key: str, value: bool) -> None:
        """Set a boolean setting in GSettings."""
        if self._settings is not None and self.has_key(key):
            self._settings.set_boolean(key, value)

    def _get_string(self, key: str, default: str) -> str:
        """Get a string setting from GSettings or fallback to default."""
        if self._settings is not None and self.has_key(key):
            return self._settings.get_string(key)
        return default

    def _set_string(self, key: str, value: str) -> None:
        """Set a string setting in GSettings."""
        if self._settings is not None and self.has_key(key):
            self._settings.set_string(key, value)

    def _get_int(self, key: str, default: int) -> int:
        """Get an integer setting from GSettings or fallback to default."""
        if self._settings is not None and self.has_key(key):
            return self._settings.get_int(key)
        return default

    def _set_int(self, key: str, value: int) -> None:
        """Set an integer setting in GSettings."""
        if self._settings is not None and self.has_key(key):
            self._settings.set_int(key, value)

    def _get_double(self, key: str, default: float) -> float:
        """Get a double setting from GSettings or fallback to default."""
        if self._settings is not None and self.has_key(key):
            return self._settings.get_double(key)
        return default

    def _set_double(self, key: str, value: float) -> None:
        """Set a double setting in GSettings."""
        if self._settings is not None and self.has_key(key):
            self._settings.set_double(key, value)

    @property
    def copy_to_clipboard(self) -> bool:
        """Get the copy to clipboard setting from GSettings or fallback to default constant."""
        return self._get_boolean(SETTING_COPY_TO_CLIPBOARD, DEFAULT_COPY_TO_CLIPBOARD)

    @copy_to_clipboard.setter
    def copy_to_clipboard(self, value: bool) -> None:
        """Set the copy to clipboard setting."""
        self._set_boolean(SETTING_COPY_TO_CLIPBOARD, value)

    @property
    def extension_status(self) -> str:
        """Get the extension status indicator."""
        return self._get_string(SETTING_EXT_STATUS, DEFAULT_EXT_STATUS)

    @extension_status.setter
    def extension_status(self, status: str) -> None:
        """Set the extension status indicator."""
        self._set_string(SETTING_EXT_STATUS, status)

    @property
    def extension_error(self) -> str:
        """Get the extension error message."""
        return self._get_string(SETTING_EXT_ERROR, DEFAULT_EXT_ERROR)

    @extension_error.setter
    def extension_error(self, error: str) -> None:
        """Set the extension error message."""
        self._set_string(SETTING_EXT_ERROR, error)

    @property
    def save_transcriptions(self) -> bool:
        """Get the save transcriptions setting from GSettings or fallback to default constant."""
        return self._get_boolean(
            SETTING_SAVE_TRANSCRIPTIONS, DEFAULT_SAVE_TRANSCRIPTIONS
        )

    @save_transcriptions.setter
    def save_transcriptions(self, value: bool) -> None:
        """Set the save transcriptions setting."""
        self._set_boolean(SETTING_SAVE_TRANSCRIPTIONS, value)

    @property
    def recording_timeout_seconds(self) -> int:
        """Get the recording timeout setting from GSettings or fallback to default constant."""
        return self._get_int(
            SETTING_RECORDING_TIMEOUT_SECONDS, DEFAULT_RECORDING_TIMEOUT_SECONDS
        )

    @recording_timeout_seconds.setter
    def recording_timeout_seconds(self, value: int) -> None:
        """Set the recording timeout setting."""
        self._set_int(SETTING_RECORDING_TIMEOUT_SECONDS, value)

    @property
    def language(self) -> str:
        """Get the language setting from GSettings or fallback to default constant."""
        return self._get_string(SETTING_LANGUAGE, DEFAULT_LANGUAGE)

    @language.setter
    def language(self, language: str) -> None:
        """Set the language setting."""
        self._set_string(SETTING_LANGUAGE, language)

    @property
    def joystick_id(self) -> int:
        """Get the joystick ID setting from GSettings or fallback to default constant."""
        return self._get_int(SETTING_JOYSTICK_ID, DEFAULT_JOYSTICK_ID)

    @joystick_id.setter
    def joystick_id(self, value: int) -> None:
        """Set the joystick ID setting."""
        self._set_int(SETTING_JOYSTICK_ID, value)

    @property
    def joystick_language_left(self) -> str:
        """Get the joystick left button language from GSettings or fallback to default constant."""
        return self._get_string(
            SETTING_JOYSTICK_LANGUAGE_LEFT, DEFAULT_JOYSTICK_LANGUAGE_LEFT
        )

    @joystick_language_left.setter
    def joystick_language_left(self, value: str) -> None:
        """Set the joystick left button language."""
        self._set_string(SETTING_JOYSTICK_LANGUAGE_LEFT, value)

    @property
    def joystick_language_right(self) -> str:
        """Get the joystick right button language from GSettings or fallback to default constant."""
        return self._get_string(
            SETTING_JOYSTICK_LANGUAGE_RIGHT, DEFAULT_JOYSTICK_LANGUAGE_RIGHT
        )

    @joystick_language_right.setter
    def joystick_language_right(self, value: str) -> None:
        """Set the joystick right button language."""
        self._set_string(SETTING_JOYSTICK_LANGUAGE_RIGHT, value)

    @property
    def faster_whisper_model(self) -> str:
        """Get the Faster Whisper model from GSettings or fallback to default constant."""
        return self._get_string(
            SETTING_FASTER_WHISPER_MODEL, DEFAULT_FASTER_WHISPER_MODEL
        )

    @faster_whisper_model.setter
    def faster_whisper_model(self, value: str) -> None:
        """Set the Faster Whisper model."""
        self._set_string(SETTING_FASTER_WHISPER_MODEL, value)

    @property
    def faster_whisper_device(self) -> str:
        """Get the Faster Whisper device from GSettings or fallback to default constant."""
        return self._get_string(
            SETTING_FASTER_WHISPER_DEVICE, DEFAULT_FASTER_WHISPER_DEVICE
        )

    @faster_whisper_device.setter
    def faster_whisper_device(self, value: str) -> None:
        """Set the Faster Whisper device."""
        self._set_string(SETTING_FASTER_WHISPER_DEVICE, value)

    @property
    def available_joystick_devices(self) -> List[JoystickDevice]:
        """Get the list of available joystick devices detected at runtime."""
        return self._available_joystick_devices

    def set_available_joystick_devices(self, devices: List[JoystickDevice]) -> None:
        """Set the list of available joystick devices."""
        self._available_joystick_devices = devices
        self._logger.info(f"Updated available joystick devices: {len(devices)} found.")

    @property
    def available_faster_whisper_models(self) -> List[TranscriberModel]:
        """Get the list of available Faster Whisper models detected at runtime."""
        return self._available_faster_whisper_models

    def set_available_faster_whisper_models(
        self, models: List[TranscriberModel]
    ) -> None:
        """Set the list of available Faster Whisper models."""
        self._available_faster_whisper_models = models
        self._logger.info(
            f"Updated available Faster Whisper models: {len(models)} found."
        )

    @property
    def available_openai_models(self) -> List[TranscriberModel]:
        """Get the list of available OpenAI models detected at runtime."""
        return self._available_openai_models

    def set_available_openai_models(self, models: List[TranscriberModel]) -> None:
        """Set the list of available OpenAI models."""
        self._available_openai_models = models
        self._logger.info(f"Updated available OpenAI models: {len(models)} found.")

    @property
    def openai_base_url(self) -> str:
        """Get the OpenAI base URL from GSettings or fallback to default constant."""
        return self._get_string(SETTING_OPENAI_BASE_URL, DEFAULT_OPENAI_BASE_URL)

    @openai_base_url.setter
    def openai_base_url(self, value: str) -> None:
        """Set the OpenAI base URL."""
        self._set_string(SETTING_OPENAI_BASE_URL, value)

    @property
    def openai_api_key(self) -> str:
        """Get the OpenAI API key from GSettings or fallback to default constant."""
        return self._get_string(SETTING_OPENAI_API_KEY, DEFAULT_OPENAI_API_KEY)

    @openai_api_key.setter
    def openai_api_key(self, value: str) -> None:
        """Set the OpenAI API key."""
        self._set_string(SETTING_OPENAI_API_KEY, value)

    @property
    def openai_model(self) -> str:
        """Get the OpenAI model from GSettings or fallback to default constant."""
        return self._get_string(SETTING_OPENAI_MODEL, DEFAULT_OPENAI_MODEL)

    @openai_model.setter
    def openai_model(self, value: str) -> None:
        """Set the OpenAI model."""
        self._set_string(SETTING_OPENAI_MODEL, value)

    @property
    def fallback_timeout_seconds(self) -> float:
        """Get the fallback timeout from GSettings or fallback to default constant."""
        return self._get_double(
            SETTING_FALLBACK_TIMEOUT_SECONDS, DEFAULT_FALLBACK_TIMEOUT_SECONDS
        )

    @fallback_timeout_seconds.setter
    def fallback_timeout_seconds(self, value: float) -> None:
        """Set the fallback timeout."""
        self._set_double(SETTING_FALLBACK_TIMEOUT_SECONDS, value)

    @property
    def typist_backend(self) -> str:
        """Get the typist backend from GSettings or fallback to default constant."""
        return self._get_string(SETTING_TYPIST_BACKEND, DEFAULT_TYPIST_BACKEND)

    @typist_backend.setter
    def typist_backend(self, value: str) -> None:
        """Set the typist backend."""
        self._set_string(SETTING_TYPIST_BACKEND, value)

    @property
    def preferred_transcriber(self) -> str:
        """Get the preferred transcriber from GSettings or fallback to default constant."""
        return self._get_string(
            SETTING_PREFERRED_TRANSCRIBER, DEFAULT_PREFERRED_TRANSCRIBER
        )

    @preferred_transcriber.setter
    def preferred_transcriber(self, value: str) -> None:
        """Set the preferred transcriber."""
        self._set_string(SETTING_PREFERRED_TRANSCRIBER, value)

    @property
    def include_application_name(self) -> bool:
        """Get the include application name setting from GSettings or fallback to default constant."""
        return self._get_boolean(
            SETTING_INCLUDE_APPLICATION_NAME, DEFAULT_INCLUDE_APPLICATION_NAME
        )

    @include_application_name.setter
    def include_application_name(self, value: bool) -> None:
        """Set the include application name setting."""
        self._set_boolean(SETTING_INCLUDE_APPLICATION_NAME, value)

    @property
    def microphone_device(self) -> str:
        """Get the microphone device from GSettings or fallback to default constant."""
        return self._get_string(SETTING_MICROPHONE_DEVICE, DEFAULT_MICROPHONE_DEVICE)

    @microphone_device.setter
    def microphone_device(self, value: str) -> None:
        """Set the microphone device."""
        self._set_string(SETTING_MICROPHONE_DEVICE, value)

    @property
    def available_microphone_devices(self) -> List[AudioDevice]:
        """Get the list of available microphone devices detected at runtime."""
        return self._available_microphone_devices

    def set_available_microphone_devices(self, devices: List[AudioDevice]) -> None:
        """Set the list of available microphone devices."""
        self._available_microphone_devices = devices
        self._logger.info(
            f"Updated available microphone devices: {len(devices)} found."
        )

    def is_openai_available(self) -> bool:
        """Check if OpenAI provider has all requirements met."""
        return bool(self.openai_api_key.strip()) and bool(self.openai_model)

    def is_faster_whisper_available(self) -> bool:
        """Check if FasterWhisper provider has all requirements met."""
        return bool(self.faster_whisper_model)

    def is_fallback_available(self) -> bool:
        """Check if Fallback mode can be used (requires both providers)."""
        return self.is_openai_available() and self.is_faster_whisper_available()

    def shutdown(self):
        pass
