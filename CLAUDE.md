# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Speed of Sound is a voice-typing application for the Linux desktop.
It captures microphone audio, transcribes it using ASR (the default is locally using Sherpa ONNX Whisper models),
optionally polishes the text with an LLM (Anthropic/Google/OpenAI and compatible endpoints),
and types the result into the active application via XDG Desktop Portal keyboard simulation.

## Build & Run Commands

All commands use the Makefile, which wraps Gradle:

- `make run` — Run the GUI app (sets `SOS_DISABLE_GIO_STORE=true` for dev mode)
- `make build` — Build all modules
- `make check` — Run all checks including detekt static analysis
- `make clean` — Clean build artifacts
- `make shadow-build` — Create fat JAR at `app/build/libs/speedofsound.jar`
- `make shadow-run` — Build and run the fat JAR
- `./cli.sh <command>` — Run CLI tool (subcommands: `record`, `asr`, `download`, `llm`)

Direct Gradle usage: `./gradlew :app:run`, `./gradlew :core:check`, etc.

Detekt reports: `{module}/build/reports/detekt/`

## Module Structure

Three Gradle modules with a shared convention plugin in `buildSrc/`:

- **core** — Shared library with no GUI dependency. Contains the audio pipeline, ASR engine, LLM integrations,
  settings, and plugin framework.
- **app** — GTK4/Adwaita desktop application. Depends on the core module. Uses java-gi bindings and Stargate
  for D-Bus portal access.
- **cli** — Command-line tool built with Clikt. Depends on the core module.

## Architecture

**Plugin system:** All major components extend `AppPlugin<Options>` with lifecycle methods
(`initialize` → `enable` → `disable` → `shutdown`) and communicate via `SharedFlow<AppPluginEvent>`.

**Sample pipeline (orchestrated by `DefaultDirector`):**
1. `JvmRecorder` — Captures PCM16 audio via `javax.sound.sampled`
2. `WhisperAsr` — Transcribes audio using Sherpa ONNX (30-second max segments)
3. `LlmPlugin` — Polishes transcription (supports Anthropic, Google, OpenAI)

**Director event flow:** `RecordingStarted` → `TranscriptionStarted` → `PolishingStarted` → `PipelineCompleted`

**App GUI threading:** `MainState` extends `GObject` and uses GObject signals with `GLib.idleAdd` for
thread-safe UI updates from coroutine backgrounds.

**Settings:** `SettingsStore` interface with two implementations — `GioStore` (GSettings, production) and
`PropertiesStore` (Java Properties file, dev mode via `SOS_DISABLE_GIO_STORE=true`).

**Text output:** Converts text to X11 keysyms and simulates keyboard input through the XDG Remote
Desktop Portal via Stargate.

## Key Technical Details

- **Kotlin 2.3.0**, JVM toolchain Java 22, Gradle 9.2.0 with Kotlin DSL
- **JVM flag required:** `--enable-native-access=ALL-UNNAMED` (for java-gi native bindings)
- Sherpa ONNX JARs are local in `core/libs/` (x64 + aarch64, version 1.12.23)
- ASR models stored in `$XDG_DATA_HOME/speedofsound/models/` (or `~/.local/share/speedofsound/models/`)
- Application ID: `io.speedofsound.App`

## Conventions

- Error handling uses `Result<T>` and `runCatching` extensively
- Coroutines with `Dispatchers.IO` for blocking operations
- No TODO comments in code — work items tracked in `BACKLOG.md` (gitignored)
- Avoid using the double bang operator (`!!`) on any Kotlin operations
- Detekt suppressions used selectively with `@Suppress` annotations
- Logging via SLF4J with Log4j2 backend
