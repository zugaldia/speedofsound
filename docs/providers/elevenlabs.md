# ElevenLabs Configuration

ElevenLabs Speech-to-Text uses their new Scribe STT model for transcription. For details about pricing and language support, visit the [ElevenLabs Speech-to-Text documentation](https://elevenlabs.io/docs/capabilities/speech-to-text).

## Configuration

```toml
[elevenlabs]
enabled = false
api_key = ""
model = ""
```

- `enabled`: Enable ElevenLabs Speech-to-Text
- `api_key`: Your ElevenLabs API key (required)
- `model`: ElevenLabs model identifier (optional, currently only `scribe_v1` is supported)

## Setup

1. Sign up for ElevenLabs API access
2. Create an API key in the dashboard following [these instructions](https://elevenlabs.io/docs/quickstart)
3. Configure your credentials:

```toml
transcriber = "elevenlabs"

[elevenlabs]
enabled = true
api_key = "your-elevenlabs-api-key"
model = "scribe_v1"
```