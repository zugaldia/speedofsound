# OpenAI Configuration

This integration supports multiple OpenAI audio models including:
- **GPT Audio models** (gpt-audio, gpt-audio-mini) - Latest production models using the completions API
- **GPT-4o Audio Preview models** - Preview models with advanced capabilities
- **Transcription models** (gpt-4o-transcribe, gpt-4o-mini-transcribe) - Specialized transcription models
- **Whisper v2** (whisper-1) - OpenAI's hosted Whisper model

For details about pricing and language support, refer to the [OpenAI Speech-to-Text documentation](https://platform.openai.com/docs/guides/speech-to-text).

## Configuration

Configure OpenAI in the Preferences window under the "Transcriber" tab, or via command line:

```bash
gsettings set io.speedofsound.App openai-enabled true
gsettings set io.speedofsound.App openai-api-key "your-api-key"
gsettings set io.speedofsound.App openai-model ""
```

- `openai-enabled`: Enable OpenAI speech-to-text transcription
- `openai-api-key`: Your OpenAI API key (required)
- `openai-model`: OpenAI model identifier (optional, defaults to `gpt-audio`)

## Setup

1. Sign up for OpenAI API access
2. Create and export an API key following the [API key instructions](https://platform.openai.com/docs/libraries#create-and-export-an-api-key)
3. Configure your credentials in the Preferences window or via command line:

```bash
gsettings set io.speedofsound.App transcriber "openai"
gsettings set io.speedofsound.App openai-enabled true
gsettings set io.speedofsound.App openai-api-key "your-openai-api-key"
gsettings set io.speedofsound.App openai-model ""
```

## Available Models

Speed of Sound supports the following OpenAI models:

### GPT Audio Models (Recommended)
- **gpt-audio** (default) - Latest production audio model
- **gpt-audio-mini** - Smaller, faster variant

These models use the Chat Completions API with audio input and provide advanced context-aware transcription.

### GPT-4o Audio Preview Models
- **gpt-4o-audio-preview** - Preview version with advanced features
- **gpt-4o-mini-audio-preview** - Smaller preview variant

These preview models also use the Chat Completions API.

### Transcription Models
- **gpt-4o-transcribe** - Specialized transcription model
- **gpt-4o-mini-transcribe** - Smaller transcription variant

These models use the Audio Transcription API.

### Legacy Models
- **whisper-1** - OpenAI's hosted Whisper v2 model

Uses the Audio Transcription API.

## Technical Details

Speed of Sound automatically routes requests to the appropriate OpenAI API based on the selected model:

- **Chat Completions API**: Used for GPT Audio models (gpt-audio, gpt-audio-mini) and GPT-4o Audio Preview models. These models receive the full system prompt with application context and support more sophisticated transcription behavior.

- **Audio Transcription API**: Used for the transcription models (gpt-4o-transcribe, gpt-4o-mini-transcribe) and Whisper v2 (whisper-1). These models receive a simplified prompt optimized for basic transcription tasks.

The implementation handles this routing transparently, so you can switch between models without any additional configuration.