from gi.repository import Adw  # type: ignore

from speedofsound.constants import (
    SETTING_FASTER_WHISPER_DEVICE,
    SETTING_FASTER_WHISPER_ENABLED,
    SETTING_FASTER_WHISPER_MODEL,
    SETTING_OPENAI_API_KEY,
    SETTING_OPENAI_BASE_URL,
    SETTING_OPENAI_ENABLED,
    SETTING_OPENAI_MODEL,
)
from speedofsound.ui.preferences.preferences_page_base import PreferencesPageBase
from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageAsr(PreferencesPageBase):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__(view_model)
        self.set_title("Speech Recognition")
        self.set_icon_name("audio-input-microphone-symbolic")
        self._build_ui()
        self._logger.info("Speech Recognition preferences page initialized")

    def _build_ui(self) -> None:
        faster_whisper_group = Adw.PreferencesGroup()
        faster_whisper_group.set_title("Faster Whisper")
        faster_whisper_group.set_description(
            "Configure local Faster Whisper transcriber"
        )

        enabled_switch = Adw.SwitchRow()
        enabled_switch.set_title("Enable Faster Whisper")
        enabled_switch.set_subtitle("Use local Faster Whisper for transcription")
        self.bind_boolean_setting(SETTING_FASTER_WHISPER_ENABLED, enabled_switch)
        faster_whisper_group.add(enabled_switch)

        available_models = (
            self._view_model.configuration.available_faster_whisper_models
        )
        if available_models:
            model_options = [(model.name, model.id) for model in available_models]
            model_combo = self.create_combo_row(
                title="Model",
                setting_key=SETTING_FASTER_WHISPER_MODEL,
                options=model_options,
                get_current_value=lambda: self._view_model.configuration.faster_whisper_model,
                set_value=lambda value: setattr(
                    self._view_model.configuration, "faster_whisper_model", value
                ),
                subtitle="Select the Faster Whisper model size",
            )
            faster_whisper_group.add(model_combo)

        device_options = [
            ("Auto", "auto"),
            ("CPU", "cpu"),
            ("CUDA", "cuda"),
        ]

        device_combo = self.create_combo_row(
            title="Device",
            setting_key=SETTING_FASTER_WHISPER_DEVICE,
            options=device_options,
            get_current_value=lambda: self._view_model.configuration.faster_whisper_device,
            set_value=lambda value: setattr(
                self._view_model.configuration, "faster_whisper_device", value
            ),
            subtitle="Select the compute device to use",
        )
        faster_whisper_group.add(device_combo)

        self.add(faster_whisper_group)

        openai_group = Adw.PreferencesGroup()
        openai_group.set_title("OpenAI")
        openai_group.set_description("Configure cloud-based OpenAI transcriber")

        openai_enabled_switch = Adw.SwitchRow()
        openai_enabled_switch.set_title("Enable OpenAI")
        openai_enabled_switch.set_subtitle("Use OpenAI API for transcription")
        self.bind_boolean_setting(SETTING_OPENAI_ENABLED, openai_enabled_switch)
        openai_group.add(openai_enabled_switch)

        base_url_entry = Adw.EntryRow()
        base_url_entry.set_title("Base URL")
        self.bind_entry_setting(SETTING_OPENAI_BASE_URL, base_url_entry)
        openai_group.add(base_url_entry)

        api_key_entry = Adw.PasswordEntryRow()
        api_key_entry.set_title("API Key")
        self.bind_entry_setting(SETTING_OPENAI_API_KEY, api_key_entry)
        openai_group.add(api_key_entry)

        available_models = self._view_model.configuration.available_openai_models
        if available_models:
            model_options = [(model.name, model.id) for model in available_models]
            model_combo = self.create_combo_row(
                title="Model",
                setting_key=SETTING_OPENAI_MODEL,
                options=model_options,
                get_current_value=lambda: self._view_model.configuration.openai_model,
                set_value=lambda value: setattr(
                    self._view_model.configuration, "openai_model", value
                ),
                subtitle="Select the OpenAI model to use",
            )
            openai_group.add(model_combo)

        self.add(openai_group)
