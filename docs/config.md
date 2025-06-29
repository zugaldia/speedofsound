# Configuration

Speed of Sound uses a TOML configuration file located at `config.toml` in the project root. Copy the example configuration to get started:

```bash
cp config.example.toml config.toml
```

The default setup uses Whisper for local transcriptions. It will automatically download the `small` model, which is the easiest way to get started as it runs on most hardware configurations.

## Permissions

$ sudo cp scripts/99-uinput.rules /etc/udev/rules.d/
$ sudo udevadm control --reload-rules && sudo udevadm trigger

## General Settings

This document describes common settings you likely want to tweak. For other settings like joystick integration or typing backend configuration, see [`advanced.md`](advanced.md).

### Language Configuration

```toml
language = "en"
```

- `language`: Language code for transcription (ISO 639-1 format, e.g.: "en" for English, "es" for Spanish)

Although some transcribers support automatic language detection, this is not currently supported by the application. A language value needs to be specified. If you need to frequently switch between two languages, you can [set up the joystick](advanced.md) to do so.

### Audio Input

```toml
microphone_id = 0
```

- `microphone_id`: Audio input device ID. Leave it unset to use the default device.

### Transcriber Selection

```toml
transcriber = "faster_whisper"
```

Choose your transcription provider:
- `"faster_whisper"` - OpenAI Whisper local model (default)
- `"whisper"` - OpenAI Whisper local server
- `"nvidia_riva"` - NVIDIA Riva (local)
- `"nvidia_nim"` - NVIDIA NIM (cloud)
- `"google"` - Google Gemini (cloud)
- `"openai"` - OpenAI GPT-4o and Whisper API (cloud)
- `"elevenlabs"` - ElevenLabs Speech-to-Text (cloud)
- `"fastest"` - Run multiple providers simultaneously (hybrid)

## Provider Configurations

Each transcription provider has its own configuration section. For detailed setup instructions and configuration options, see the individual provider documentation:

- **[Whisper](whisper.md)** - Local Whisper options
- **[NVIDIA](nvidia.md)** - NVIDIA Riva (local) and NVIDIA NIM (cloud)
- **[Google](google.md)** - Google Speech-to-Text
- **[OpenAI](openai.md)** - OpenAI Whisper API
- **[ElevenLabs](elevenlabs.md)** - ElevenLabs Speech-to-Text

## Configuration Tips

1. **Start Simple**: Begin with the default Whisper configuration for local processing.
2. **Multiple Providers**: Enable multiple providers and use the "fastest" transcriber to run them simultaneously. This ensures fast responses when cloud providers experience outages or latency issues. This is particularly useful with cloud providers but you can mix and match both local and cloud providers.

## Example Configurations

### Local-Only Setup (Whisper)

Similar to the default setup but using a larger `turbo` model.
```toml
language = "en"
transcriber = "faster_whisper"

[faster_whisper]
enabled = true
model = "turbo"
```
See [Whisper documentation](whisper.md) for detailed setup.

### Cloud Setup (OpenAI)
Use a cloud Whisper service instead of running it locally:

```toml
language = "en"
transcriber = "openai"

[openai]
enabled = true
api_key = "your-openai-api-key"
model = "whisper-1"
```

### Hybrid Setup (Fastest Mode)
Race two cloud providers and choose the fastest transcription:

```toml
language = "en"
transcriber = "fastest"

[google]
enabled = true
api_key = "your-google-api-key"
model = "gemini-2.5-flash-preview-05-20"

[openai]
enabled = true
api_key = "your-openai-api-key"
model = "whisper-1"
```
