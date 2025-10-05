# Configure the app

Speed of Sound uses a `config.toml` file for all settings. When you first launch the application, it will automatically create a default configuration that uses a local Whisper model for speech recognition. It will also automatically download the right model files for local usage.

This file is created under your user config folder, which is typically under `~/.config/io.speedofsound.App/config.toml`. 

## Input Language

Although some transcribers support automatic language detection, this is not currently supported by the application. An input language value must be specified. 

```toml
language = "en"
```

- `language`: Language code for transcription (ISO 639-1 format, e.g.: "en" for English, "es" for Spanish)

If you need to frequently switch between two input languages, you can set up the joystick to do so. See the joystick section on the [`trigger.md`](trigger.md) page for details. 

## Transcription Provider

By default, Speed of Sound uses a local Whisper model for all transcriptions. However, other providers are supported. See their individual pages for provider-specific instructions:

- **[ElevenLabs](providers/elevenlabs.md)** - ElevenLabs Speech-to-Text model
- **[Google](providers/google.md)** - Google Gemini Multimodal transcriptions
- **[OpenAI](providers/openai.md)** - OpenAI Whisper API and GPT-4o
- **[Whisper](providers/whisper.md)** - Local Whisper options

### Fastest Provider

In addition to the providers mentioned above, Speed of Sound provides a meta-provider called `fastest`. When this provider is selected, it will simultaneously send your audio to all enabled providers and use the transcription from whichever provider responds first.

```toml
transcriber = fastest
```

This provider is particularly helpful if you opt to use cloud providers. Cloud providers experience outages and latency issues. By enabling multiple cloud providers with this meta provider, you increase the speed and reliability of the application.

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
