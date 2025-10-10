# Configure the app

Speed of Sound uses GSettings for all user-facing settings, accessible through the Preferences window in the application. When you first launch the application, it will use default settings with a local Faster Whisper model for speech recognition. It will also automatically download the right model files for local usage.

Settings are stored using the GSettings system and can be accessed either through the UI or via the `gsettings` command-line tool under the schema `io.speedofsound.App`.

## Input Language

Although some transcribers support automatic language detection, this is not currently supported by the application. An input language value must be specified.

You can configure the language in the Preferences window under the "General" tab, or via command line:

```bash
gsettings set io.speedofsound.App language "en"
```

- `language`: Language code for transcription (ISO 639-1 format, e.g.: "en" for English, "es" for Spanish)

If you need to frequently switch between two input languages, you can set up the joystick to do so. See the joystick section on the [`trigger.md`](trigger.md) page for details.

## Transcription Options

Speed of Sound offers three transcription options, configurable in the Preferences window under the "Transcriber" tab:

### 1. Faster Whisper (Default)

Local transcription running entirely on your device. No internet connection required, and your audio data never leaves your computer.

Select "Faster Whisper" in the Preferences window, or via command line:

```bash
gsettings set io.speedofsound.App transcriber "faster_whisper"
```

See **[Faster Whisper configuration](providers/whisper.md)** for detailed setup instructions.

### 2. OpenAI

Cloud-based transcription using OpenAI's Whisper and GPT-4o models. Requires an API key and internet connection.

Select "OpenAI" in the Preferences window, or via command line:

```bash
gsettings set io.speedofsound.App transcriber "openai"
```

See **[OpenAI configuration](providers/openai.md)** for detailed setup instructions.

### 3. Fallback (Meta-Transcriber)

Uses a timeout-based approach to ensure fast transcriptions. It starts with OpenAI for speed, but automatically falls back to local Faster Whisper if the cloud service doesn't respond within the configured timeout. This option provides a good balance between speed and reliability.

Select "Fallback" in the Preferences window, or via command line:

```bash
gsettings set io.speedofsound.App transcriber "fallback"
gsettings set io.speedofsound.App fallback-timeout-seconds 2.0
```

- `fallback-timeout-seconds`: Default timeout in seconds (default: 2.0, range: 0.1-10.0)

This option requires both OpenAI and Faster Whisper to be configured. It's particularly useful for ensuring consistent performance even when cloud services experience latency or outages.

## (Optional) Context settings

You can supply additional context to some providers to improve the transcription. For example, you can decide to include the name of the active application or provide a custom prompt.

See [`context.md`](context.md) for details.

## (Optional) Typist settings

By default, Speed of Sound uses xdotool to emulate a virtual keyboard. Other options include `ydotool` and `atspi`.

These settings are configured in the Preferences window under the "Advanced" tab.

Read [`input.md`](input.md) for details.

## (Optional) Recording settings

By default, recordings automatically stop after 60 seconds to prevent indefinitely long recordings. You can customize this timeout in the Preferences window under the "General" tab, or via command line:

```bash
gsettings set io.speedofsound.App recording-timeout-seconds 60
```

- `recording-timeout-seconds`: Maximum recording duration in seconds (default: 60, range: 1-300)

## (Optional) Joystick settings

If you have a joystick connected to your system, you can use it to trigger the application and to switch the language for voice typing.

See the joystick section on the [`trigger.md`](trigger.md) document for details. 
