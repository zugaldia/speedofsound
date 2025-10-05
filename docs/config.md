# Configure the app

Speed of Sound uses a `config.toml` file for all settings. When you first launch the application, it will automatically create a default configuration that uses a local Faster Whisper model for speech recognition. It will also automatically download the right model files for local usage.

This file is created under your user config folder, which is typically under `~/.config/io.speedofsound.App/config.toml`. 

## Input Language

Although some transcribers support automatic language detection, this is not currently supported by the application. An input language value must be specified. 

```toml
language = "en"
```

- `language`: Language code for transcription (ISO 639-1 format, e.g.: "en" for English, "es" for Spanish)

If you need to frequently switch between two input languages, you can set up the joystick to do so. See the joystick section on the [`trigger.md`](trigger.md) page for details. 

## Transcription Options

Speed of Sound offers three transcription options:

### 1. Faster Whisper (Default)

Local transcription running entirely on your device. No internet connection required, and your audio data never leaves your computer.

```toml
transcriber = "faster_whisper"
```

See **[Faster Whisper configuration](providers/whisper.md)** for detailed setup instructions.

### 2. OpenAI

Cloud-based transcription using OpenAI's Whisper and GPT-4o models. Requires an API key and internet connection.

```toml
transcriber = "openai"
```

See **[OpenAI configuration](providers/openai.md)** for detailed setup instructions.

### 3. Fastest (Meta-Transcriber)

Runs both local (Faster Whisper) and cloud (OpenAI) transcription simultaneously, using whichever responds first. This option provides maximum speed and reliability by combining the benefits of both approaches.

```toml
transcriber = "fastest"
```

This option requires both Faster Whisper and OpenAI to be enabled in your configuration. It's particularly useful for ensuring consistent performance even when cloud services experience latency or outages.

## (Optional) Context settings

You can supply additional context to some providers to improve the transcription. For example, you can decide to include the name of the active application or provide a custom prompt.

See [`context.md`](context.md) for details. 

## (Optional) Typist settings

By default, Speed of Sound uses xdotool to emulate a virtual keyboard. Other options include `ydotool` and `atspi`.

Read [`input.md`](input.md) for details. 

## (Optional) Recording settings

By default, recordings automatically stop after 60 seconds to prevent indefinitely long recordings. You can customize this timeout:

```toml
recording_timeout_seconds = 60
```

- `recording_timeout_seconds`: Maximum recording duration in seconds (default: 60, range: 1-300)

## (Optional) Joystick settings

If you have a joystick connected to your system, you can use it to trigger the application and to switch the language for voice typing.

See the joystick section on the [`trigger.md`](trigger.md) document for details. 
