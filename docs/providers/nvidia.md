# NVIDIA Configuration

NVIDIA provides both hosted and self-hosted approaches to their speech recognition models. Under the hood, they use the same NVIDIA Riva SDK and models. This page describes how to configure both options. For more information about NVIDIA Riva, visit [developer.nvidia.com/riva](https://developer.nvidia.com/riva).

## NVIDIA Riva (Local)

In this setup, you need to create a local NVIDIA Riva server. One popular method to achieve this is using NVIDIA Jetson hardware, which is relatively affordable (compared to desktop/server GPUs) and designed for edge AI use cases like robotics.

To configure a local NVIDIA Riva server for speech recognition, follow the instructions on the [NVIDIA Riva ASR Quick Start Guide](https://docs.nvidia.com/deeplearning/riva/user-guide/docs/quick-start-guide/asr.html).

### Configuration

```toml
[nvidia_riva]
enabled = false
model = ""
endpoint = ""
ssl = false
```

- `enabled`: Enable NVIDIA Riva transcription
- `model`: Riva model identifier (optional, defaults to reasonable choice)
- `endpoint`: Riva server endpoint URL
- `ssl`: Use SSL/TLS for connection

### Setup

1. Follow the NVIDIA Riva ASR Quick Start Guide to install and configure your server
2. Configure your credentials:

```toml
transcriber = "nvidia_riva"

[nvidia_riva]
enabled = true
model = ""
endpoint = "your-riva-endpoint"
ssl = false
```

## NVIDIA NIM (Cloud)

These are models hosted on the NVIDIA cloud. You can see the available models by visiting [build.nvidia.com/models](https://build.nvidia.com/models) and filtering them by speech-to-text use cases.

For getting started with NVIDIA NIM, refer to the [NVIDIA NIM Getting Started Guide](https://docs.nvidia.com/nim/large-language-models/latest/getting-started.html).

### Configuration

```toml
[nvidia_nim]
enabled = false
api_key = ""
model = ""
endpoint = ""
ssl = true
```

- `enabled`: Enable NVIDIA NIM transcription
- `api_key`: Your NVIDIA NIM API key (required)
- `model`: NIM model identifier (optional, defaults to reasonable choice)
- `endpoint`: NIM service endpoint URL
- `ssl`: Use SSL/TLS for connection (recommended: `true`)

### Setup

1. Sign up for NVIDIA NIM API access
2. Get your API key
3. Configure your credentials:

```toml
transcriber = "nvidia_nim"

[nvidia_nim]
enabled = true
api_key = "your-nvidia-nim-api-key"
model = ""
endpoint = "your-nim-endpoint"
ssl = true
```