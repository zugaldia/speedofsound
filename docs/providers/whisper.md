# Whisper Configuration

There are two supported ways to run Whisper locally. By default, we recommend using the `faster_whisper` provider that automatically downloads the right model files for you.

However, if you want to run a local Whisper server, for example, on a different machine in your local network, that setup is also supported. 

## Faster Whisper Configuration

[Faster Whisper](https://github.com/SYSTRAN/faster-whisper) is a reimplementation of OpenAI's original Whisper project. This implementation is reportedly up to 4 times faster (hence its name) and supports both CPU and GPU inference.

```toml
[faster_whisper]
enabled = true
device = "auto"
model = "small"
```

- `enabled`: Enable Faster Whisper transcription
- `device`: CPU vs NVIDIA GPU inference, one of "cpu", "cuda", or "auto"
- `model`: Whisper model name (see table below)

| Model Name | Category | Size |
|------------|----------|------|
| `tiny.en` | Faster Whisper | 75 MB |
| `tiny` | Faster Whisper | 75 MB |
| `base.en` | Faster Whisper | 141 MB |
| `base` | Faster Whisper | 142 MB |
| `small.en` | Faster Whisper | 464 MB |
| `small` | Faster Whisper | 464 MB |
| `medium.en` | Faster Whisper | 1.5 GB |
| `medium` | Faster Whisper | 1.5 GB |
| `large-v1` | Faster Whisper | 2.9 GB |
| `large-v2` | Faster Whisper | 2.9 GB |
| `large-v3` | Faster Whisper | 2.9 GB |
| `large` | Faster Whisper | 2.9 GB |
| `distil-large-v2` | Distil Whisper | 1.5 GB |
| `distil-medium.en` | Distil Whisper | 756 MB |
| `distil-small.en` | Distil Whisper | 321 MB |
| `distil-large-v3` | Distil Whisper | 1.5 GB |
| `large-v3-turbo` | Turbo | 1.6 GB |
| `turbo` | Turbo | 1.6 GB |

Notes:
- `.en` models are English only.
- Distil models are a "distilled" version of Whisper that is reportedly 6 times faster, 49% smaller, and performs within 1% word error rate (WER) on out-of-distribution evaluation sets. It's a popular Whisper flavor on the [Open ASR Leaderboard](https://huggingface.co/spaces/hf-audio/open_asr_leaderboard).
- Turbo is a recent fine-tuned version of `large-v3` with fewer decoding layers. As a result, the model is faster at the expense of a minor quality degradation.
- Latest model list [available here](https://github.com/SYSTRAN/faster-whisper/blob/master/faster_whisper/utils.py).

## Whisper Server Configuration

```toml
[whisper]
enabled = true
endpoint = "http://localhost:8080"
```

- `enabled`: Enable Whisper server transcription
- `endpoint`: Whisper server endpoint URL

There are two popular ways to set up a local Whisper server:

### Option 1: Whisperfile Project

This is probably the simplest way to start and leverages Mozilla's Whisperfile project. Follow the [instructions to download](https://huggingface.co/Mozilla/whisperfile) the right file locally. Depending on your computer and available resources, you can download small, medium, or large/turbo models. Larger models provide better transcription quality.

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
