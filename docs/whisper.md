# Whisper Configuration

## Configuration

```toml
[whisper]
enabled = true
endpoint = "http://localhost:8080"
```

- `enabled`: Enable local Whisper transcription
- `endpoint`: Whisper server endpoint URL

## Setup

There are two main ways to set up a local Whisper server:

### Option 1: Whisperfile Project

This is probably the easiest way to start and leverages Mozilla's Whisperfile project. Follow the [instructions to download](https://huggingface.co/Mozilla/whisperfile) the right file locally. Depending on your computer and available resources, you can download small, medium, or large models. Larger models provide better transcription quality.

Then launch the [local HTTP server](https://github.com/Mozilla-Ocho/llamafile/blob/main/whisper.cpp/doc/server.md).

### Option 2: Embedded whisper.cpp Server

This approach uses the official `whisper.cpp` project to build and run a local server. The server will start on `http://localhost:8080` by default.

For detailed instructions and additional configuration options, see the [whisper.cpp server documentation](https://github.com/ggml-org/whisper.cpp/tree/master/examples/server).

### Final Configuration

Once you have your Whisper server running, configure Speed of Sound:

1. Set the correct endpoint URL in your configuration
2. Enable Whisper and set it as your transcriber:

```toml
transcriber = "whisper"

[whisper]
enabled = true
endpoint = "http://localhost:8080"
```
