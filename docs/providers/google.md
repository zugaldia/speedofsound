# Google Configuration

This integration uses Gemini's audio understanding capabilities rather than traditional Google Speech-to-Text models. For details about Gemini's audio functionality, visit the [Gemini API audio documentation](https://ai.google.dev/gemini-api/docs/audio).

## Configuration

```toml
[google]
enabled = false
api_key = ""
model = ""
```

- `enabled`: Enable Google Gemini audio transcription
- `api_key`: Google API key with Gemini API access (required)
- `model`: Gemini model identifier (optional, defaults to reasonable choice)

## Setup

1. Get a Gemini API key following the [API key instructions](https://ai.google.dev/gemini-api/docs/api-key). The easiest way to start is by getting an API key from Google AI Studio as explained on that page, but there are other options.
2. Configure your credentials:

```toml
transcriber = "google"

[google]
enabled = true
api_key = "your-google-api-key"
model = ""
```