# Configuration

Speed of Sound uses a TOML configuration file located at `config.toml` in the project root. Copy the example configuration to get started:

```bash
cp config.example.toml config.toml
```

This shows the basic settings needed to set up the application with Whisper as the speech-to-text backend. 

## General Settings

This document describes common settings you likely want to tweak. For other settings like joystick integration or typing backend configuration, see [`advanced.md`](advanced.md).

### Language Configuration

```toml
language_auto = true
language = "en"
```

- `language_auto`: When `true`, automatically detects the language from audio input
- `language`: Default language code (ISO 639-1 format, e.g.: "en" for English, "es" for Spanish)

### Audio Input

```toml
microphone_id = -1
```

- `microphone_id`: Audio input device ID. Use `-1` for system default, or specify a device ID

### Transcriber Selection

```toml
transcriber = "whisper"
```

Choose your transcription provider:
- `"whisper"` - Local Whisper server (default)
- `"nvidia_riva"` - NVIDIA Riva (local)
- `"nvidia_nim"` - NVIDIA NIM (cloud)
- `"google"` - Google Gemini (cloud)
- `"openai"` - OpenAI GPT-4o and Whisper API (cloud)
- `"elevenlabs"` - ElevenLabs Speech-to-Text (cloud)
- `"race"` - Run multiple providers simultaneously (hybrid)


## Provider Configurations

Each transcription provider has its own configuration section. For detailed setup instructions and configuration options, see the individual provider documentation:

- **[Whisper](whisper.md)** - Local Whisper server (default, privacy-focused)
- **[NVIDIA](nvidia.md)** - NVIDIA Riva (local) and NVIDIA NIM (cloud)
- **[Google](google.md)** - Google Speech-to-Text
- **[OpenAI](openai.md)** - OpenAI Whisper API
- **[ElevenLabs](elevenlabs.md)** - ElevenLabs Speech-to-Text

## Configuration Tips

1. **Start Simple**: Begin with the default Whisper configuration for local processing
2. **Multiple Providers**: Enable multiple providers and use the "race" transcriber to run them simultaneously. This ensures fast responses when cloud providers experience outages or latency issues. You can mix and match both local and cloud providers
3. **Language Detection**: Enable `language_auto` for automatic language detection, or set a specific language for better performance

## Example Configurations

### Local-Only Setup (Whisper)
```toml
language_auto = true
language = "en"
microphone_id = -1
transcriber = "whisper"

[whisper]
enabled = true
endpoint = "http://localhost:8080"
```
See [Whisper documentation](whisper.md) for detailed setup.

### Cloud Setup (OpenAI)
```toml
language_auto = true
language = "en"
microphone_id = -1
transcriber = "openai"

[openai]
enabled = true
api_key = "your-openai-api-key"
model = "whisper-1"
```
See [OpenAI documentation](openai.md) for detailed setup.

### Hybrid Setup (Race Mode)
```toml
language_auto = true
language = "en"
microphone_id = -1
transcriber = "race"

[whisper]
enabled = true
endpoint = "http://localhost:8080"

[openai]
enabled = true
api_key = "your-openai-api-key"
model = "whisper-1"
```
Race mode runs multiple providers simultaneously. See individual provider documentation for detailed configuration.