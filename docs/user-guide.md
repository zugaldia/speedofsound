# User Guide

## Main Screen

![Main screen](assets/screenshots/main-light.png#only-light)
![Main screen](assets/screenshots/main-dark.png#only-dark)

The main screen is divided into two halves. The top half contains a progress bar that reacts to your voice while you
speak and shows transcription progress as Speed of Sound processes your audio. If you have an LLM text model
configured, the progress bar will also reflect the polishing step.

The bottom half displays the currently selected voice model, text model, and expected input language. The gear button
opens a menu where you can access Preferences, Keyboard Shortcuts, the About screen, or quit the application.

## Keyboard shortcuts

Most shortcuts are only active when the Speed of Sound main window is open and focused. The exception is `Super+Z`
(configurable, and shown below as an example), which is the global shortcut you will use day-to-day to start and stop
dictation from any application.

| Shortcut      | Action                                                                                |
|---------------|---------------------------------------------------------------------------------------|
| `Super+Z`     | Start or stop dictation (global, configured in [Getting Started](getting-started.md)) |
| `Ctrl+S`      | Start or stop listening (while the app window is focused)                             |
| `Escape`      | Cancel listening                                                                      |
| `Ctrl+M`      | Minimize window                                                                       |
| `Left Shift`  | Select primary language                                                               |
| `Right Shift` | Select secondary language                                                             |
| `Ctrl+Q`      | Quit                                                                                  |

A quick reference is also available from within the application. Tap the gear icon on the main window and select
**Keyboard Shortcuts** to open the shortcuts dialog as a reminder while you work.

![Keyboard shortcuts](assets/screenshots/shortcuts-light.png#only-light)
![Keyboard shortcuts](assets/screenshots/shortcuts-dark.png#only-dark)

## Preferences

The Preferences dialog gives you control over how Speed of Sound behaves, from language selection and model
configuration to personalization and backup.

![Preferences](assets/screenshots/preferences-light.png#only-light)
![Preferences](assets/screenshots/preferences-dark.png#only-dark)

**General** — Set the primary language used for speech recognition. You can optionally configure a secondary language
and switch between the two using the Left Shift and Right Shift keys while the main window is focused.

**Model Library** — Browse and manage the locally available voice models. You can download new models or remove
ones you no longer need. Keep this window open while a download is in progress.

!!! note "Cloud providers are optional"
    Using cloud providers is entirely optional and not required to use Speed of Sound.
    This feature is intended for devices with limited hardware resources that cannot run models locally,
    or for cases where a large cloud-only model is needed to meet specific accuracy or latency requirements.
    See the [FAQ](faq.md) for more information.

**Cloud Credentials** — Store API keys for cloud services. Speed of Sound supports Anthropic, Google, and OpenAI
directly, as well as any third-party provider that offers compatible endpoints, such as OpenRouter.
Credentials saved here can be referenced when configuring voice or text model providers.

**Voice Models** — Configure the speech recognition provider used to transcribe your audio. By default,
Speed of Sound transcribes locally using a Whisper model. You can add additional providers, including cloud-based ones,
and select which one is active.

**Text Models** — Optionally enable an LLM to post-process your transcriptions for improved accuracy, grammar,
and formatting. You can add one or more providers and choose which one is active. This step is disabled by default.

**Personalization** — Provide optional context to improve transcription quality. You can, for example, add
information about your writing style and add custom vocabulary entries such as names, technical terms, or
acronyms that the model should recognize.

**Advanced** — Fine-tune low-level typing behavior. You can adjust the delay between hiding the main window and issuing keystrokes, as well as the delay between individual keystrokes. The defaults work well for most desktop environments and do not normally need to be changed.

**Import / Export** — Back up your preferences to a file, useful to set up Speed of Sound on a different machine.
