# OpenAI Configuration

This integration works with OpenAI's hosted version of Whisper as well as the new GPT-4o transcription models. For details about pricing and language support, refer to the [OpenAI Speech-to-Text documentation](https://platform.openai.com/docs/guides/speech-to-text).

## Configuration

```toml
[openai]
enabled = false
api_key = ""
model = ""
```

- `enabled`: Enable OpenAI speech-to-text transcription
- `api_key`: Your OpenAI API key (required)
- `model`: OpenAI model identifier (optional, defaults to reasonable choice)

## Setup

1. Sign up for OpenAI API access
2. Create and export an API key following the [API key instructions](https://platform.openai.com/docs/libraries#create-and-export-an-api-key)
3. Configure your credentials:

```toml
transcriber = "openai"

[openai]
enabled = true
api_key = "your-openai-api-key"
model = ""
```