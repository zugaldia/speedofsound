from typing import List, Optional, Tuple

from gi.repository import Adw, Gio  # type: ignore

from speedofsound.constants import (
    SETTING_FALLBACK_TIMEOUT_SECONDS,
    SETTING_FASTER_WHISPER_DEVICE,
    SETTING_FASTER_WHISPER_MODEL,
    SETTING_OPENAI_API_KEY,
    SETTING_OPENAI_BASE_URL,
    SETTING_OPENAI_MODEL,
    SETTING_PREFERRED_TRANSCRIBER,
)
from speedofsound.models import FasterWhisperDevice, TranscriberType
from speedofsound.ui.preferences.preferences_page_base import PreferencesPageBase
from speedofsound.ui.preferences.preferences_view_model import PreferencesViewModel


class PreferencesPageAsr(PreferencesPageBase):
    def __init__(self, view_model: PreferencesViewModel) -> None:
        super().__init__(view_model)
        self.set_title("Speech Recognition")
        self.set_icon_name("audio-input-microphone-symbolic")
        self._transcriber_combo: Optional[Adw.ComboRow] = None
        self._general_group: Optional[Adw.PreferencesGroup] = None
        self._build_ui()
        self._setup_dynamic_updates()

    def _get_transcriber_options(self) -> Tuple[List[Tuple[str, str]], str]:
        """Build transcriber options dynamically based on availability."""
        transcriber_options: List[Tuple[str, str]] = []
        unavailable_options: List[str] = []
        config = self._view_model.configuration

        # Check Faster Whisper availability
        if config.is_faster_whisper_available():
            transcriber_options.append(
                ("Faster Whisper", TranscriberType.FASTER_WHISPER.value)
            )
        else:
            unavailable_options.append("Faster Whisper (select model)")

        # Check OpenAI availability
        if config.is_openai_available():
            transcriber_options.append(("OpenAI", TranscriberType.OPENAI.value))
        else:
            unavailable_options.append("OpenAI (provide API key)")

        # Check Fallback availability (requires both)
        if config.is_fallback_available():
            transcriber_options.append(("Fallback", TranscriberType.FALLBACK.value))
        else:
            unavailable_options.append("Fallback (requires both providers)")

        # Build subtitle showing what's available
        if unavailable_options:
            subtitle = f"Select the transcriber to use. Unavailable: {', '.join(unavailable_options)}"
        else:
            subtitle = "Select the transcriber to use for speech recognition"

        return transcriber_options, subtitle

    def _build_ui(self) -> None:
        self._general_group = Adw.PreferencesGroup()
        self._general_group.set_title("General")
        self._general_group.set_description("Configure general ASR settings")

        transcriber_options, subtitle = self._get_transcriber_options()
        transcriber_attr = self._setting_key_to_attr(SETTING_PREFERRED_TRANSCRIBER)

        self._transcriber_combo = self.create_combo_row(
            title="Preferred Transcriber",
            setting_key=SETTING_PREFERRED_TRANSCRIBER,
            options=transcriber_options,
            get_current_value=lambda: self._view_model.configuration.preferred_transcriber,
            set_value=lambda value: setattr(
                self._view_model.configuration,
                transcriber_attr,
                value,
            ),
            subtitle=subtitle,
        )
        self._general_group.add(self._transcriber_combo)

        self.add(self._general_group)

        faster_whisper_group = Adw.PreferencesGroup()
        faster_whisper_group.set_title("Faster Whisper (Local)")
        faster_whisper_group.set_description(
            "Configure local Faster Whisper transcriber"
        )

        available_models = (
            self._view_model.configuration.available_faster_whisper_models
        )
        if available_models:
            model_options = [(model.name, model.id) for model in available_models]
            model_attr = self._setting_key_to_attr(SETTING_FASTER_WHISPER_MODEL)
            model_combo = self.create_combo_row(
                title="Model",
                setting_key=SETTING_FASTER_WHISPER_MODEL,
                options=model_options,
                get_current_value=lambda: self._view_model.configuration.faster_whisper_model,
                set_value=lambda value: setattr(
                    self._view_model.configuration,
                    model_attr,
                    value,
                ),
                subtitle="Select the Faster Whisper model size",
            )
            faster_whisper_group.add(model_combo)

        device_options = [
            ("Auto", FasterWhisperDevice.AUTO.value),
            ("CPU", FasterWhisperDevice.CPU.value),
            ("CUDA", FasterWhisperDevice.CUDA.value),
        ]
        device_attr = self._setting_key_to_attr(SETTING_FASTER_WHISPER_DEVICE)
        device_combo = self.create_combo_row(
            title="Device",
            setting_key=SETTING_FASTER_WHISPER_DEVICE,
            options=device_options,
            get_current_value=lambda: self._view_model.configuration.faster_whisper_device,
            set_value=lambda value: setattr(
                self._view_model.configuration,
                device_attr,
                value,
            ),
            subtitle="Select the compute device to use",
        )
        faster_whisper_group.add(device_combo)

        self.add(faster_whisper_group)

        openai_group = Adw.PreferencesGroup()
        openai_group.set_title("OpenAI (Cloud)")
        openai_group.set_description("Configure cloud-based OpenAI transcriber")

        api_key_entry = Adw.PasswordEntryRow()
        api_key_entry.set_title("API Key")
        self.bind_entry_setting(SETTING_OPENAI_API_KEY, api_key_entry)
        openai_group.add(api_key_entry)

        available_models = self._view_model.configuration.available_openai_models
        if available_models:
            model_options = [(model.name, model.id) for model in available_models]
            model_attr = self._setting_key_to_attr(SETTING_OPENAI_MODEL)

            model_combo = self.create_combo_row(
                title="Model",
                setting_key=SETTING_OPENAI_MODEL,
                options=model_options,
                get_current_value=lambda: self._view_model.configuration.openai_model,
                set_value=lambda value: setattr(
                    self._view_model.configuration,
                    model_attr,
                    value,
                ),
                subtitle="Select the OpenAI model to use",
            )
            openai_group.add(model_combo)

        base_url_entry = Adw.EntryRow()
        base_url_entry.set_title("Base URL")
        self.bind_entry_setting(SETTING_OPENAI_BASE_URL, base_url_entry)
        openai_group.add(base_url_entry)

        self.add(openai_group)

        fallback_group = Adw.PreferencesGroup()
        fallback_group.set_title("Fallback")
        fallback_group.set_description(
            "Configure timeout for fallback transcriber (cloud to local)"
        )

        timeout_spin = Adw.SpinRow.new_with_range(0.1, 10.0, 0.1)
        timeout_spin.set_title("Timeout")
        timeout_spin.set_subtitle("Seconds before falling back to local transcription")
        timeout_spin.set_digits(1)
        self.bind_double_setting(SETTING_FALLBACK_TIMEOUT_SECONDS, timeout_spin)
        fallback_group.add(timeout_spin)

        self.add(fallback_group)

    def _setup_dynamic_updates(self) -> None:
        """Setup listeners for configuration changes to update available transcribers."""
        settings = self._view_model.configuration.settings
        if settings is None:
            return

        # Listen to changes that affect transcriber availability
        relevant_settings = [
            SETTING_FASTER_WHISPER_MODEL,
            SETTING_OPENAI_API_KEY,
            SETTING_OPENAI_MODEL,
        ]

        for setting_key in relevant_settings:
            settings.connect(f"changed::{setting_key}", self._on_config_changed)

    def _on_config_changed(self, settings: Gio.Settings, _key: str) -> None:
        """Handle configuration changes that affect transcriber availability."""
        self._update_transcriber_combo()

    def _update_transcriber_combo(self) -> None:
        """Rebuild the transcriber combo box with updated options."""
        if self._transcriber_combo is None or self._general_group is None:
            return

        # Remove old combo
        self._general_group.remove(self._transcriber_combo)

        # Create new combo with updated options
        transcriber_options, subtitle = self._get_transcriber_options()
        transcriber_attr = self._setting_key_to_attr(SETTING_PREFERRED_TRANSCRIBER)
        self._transcriber_combo = self.create_combo_row(
            title="Preferred Transcriber",
            setting_key=SETTING_PREFERRED_TRANSCRIBER,
            options=transcriber_options,
            get_current_value=lambda: self._view_model.configuration.preferred_transcriber,
            set_value=lambda value: setattr(
                self._view_model.configuration,
                transcriber_attr,
                value,
            ),
            subtitle=subtitle,
        )

        self._general_group.add(self._transcriber_combo)
