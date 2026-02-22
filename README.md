[![Version](https://img.shields.io/github/v/release/zugaldia/speedofsound)](https://github.com/zugaldia/speedofsound/releases)
[![Build](https://github.com/zugaldia/speedofsound/actions/workflows/build.yml/badge.svg)](https://github.com/zugaldia/speedofsound/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Platform](https://img.shields.io/badge/platform-Linux-lightgrey)

# Speed of Sound

Voice typing for the Linux desktop: press a key, speak your text, and it types itself. That's it.

## Features

- Offline, on-device transcription using Whisper. No data leaves your machine.
- Types the result directly into any focused application using Portals for wide desktop support (X11, Wayland).
- [Multi-language support](https://github.com/openai/whisper#available-models-and-languages) with switchable primary and secondary languages on the fly.
- Works out of the box with the built-in Whisper Tiny model. Download additional models from within the app to improve accuracy.
- *Optional* text polishing with LLMs (Anthropic, Google, OpenAI), with support for a custom context and vocabulary.
- Supports self-hosted services like vLLM, Ollama, and llama.cpp (cloud services supported but not required).

## Getting Started

> Note: Support for Flatpak and Snap distribution is underway. If you are familiar with either packaging format and
> would like to help, please reach out to the author.

> Note: The following sections assume an Ubuntu installation. The specific commands might change in other distributions.

### Requirements

- **Java 25**: required to run the application.
- **GStreamer**: required for audio recording (usually preinstalled, installation docs [here](https://gstreamer.freedesktop.org/documentation/installing/on-linux.html))
- **gdbus**: required to trigger the keyboard shortcut (usually preinstalled, install `libglib2.0-bin` otherwise).

On Ubuntu LTS, you can install Java 25 (current LTS) and make it the default with:

```bash
sudo apt install openjdk-25-jdk
sudo update-java-alternatives -s java-1.25.0-openjdk-amd64 
```

### Download the app

Go to the [GitHub releases page](https://github.com/zugaldia/speedofsound/releases) and download two files
from the latest release:

- `speedofsound.jar`: the application
- `trigger.sh`: the script that triggers a recording from a keyboard shortcut (uses `gdbus` under the hood)

For example, download both files into `$HOME/speedofsound/`. Then make the trigger script executable:

```bash
chmod +x $HOME/speedofsound/trigger.sh
```

### Set up a shortcut

In this step, you will assign a global keyboard shortcut that starts and stops dictation.
The exact steps vary by desktop environment. On GNOME, for example:

1. Open **Settings** and navigate to **Keyboard**.
2. Click **View and Customize Shortcuts**.
3. Scroll to the bottom and select **Custom Shortcuts**.
4. Click the **+** button to add a new shortcut and fill in the fields:
    - **Name:** anything you like, e.g. `Speed of Sound`
    - **Command:** the full path to the trigger script, e.g. `/home/your-username/speedofsound/trigger.sh`
    - **Shortcut:** press your desired key combination, e.g. `Super+Z`
5. Click **Add** to save.

You can add multiple shortcuts targeting the same trigger script if you wish to.

### Usage

Launch the application like any other Java application:

```bash
java -jar $HOME/speedofsound/speedofsound.jar
```

(If you see warnings about using `--enable-native-access=ALL-UNNAMED`, feel free to add it in future invocations.
These warnings are safe to ignore: they basically mean that [Java GI](https://java-gi.org/) is accessing the
native GTK and GNOME libraries to render the application UI.)

On the first launch, two things will happen:

1. **Model setup**: The built-in Whisper Tiny model is unpacked into your user data folder.
   This is automatic and happens in the background.
2. **Permissions prompt**: The app will ask you to grant permission to type on your behalf.
   To support both X11 and Wayland desktops without requiring root access, Speed of Sound uses XDG Desktop Portals
   for keyboard input. You must approve this prompt for dictation to work.

Once permissions are granted, press your shortcut to start dictating and press it again to stop.
The transcribed text will be typed into whatever application is currently focused.

### Keyboard shortcuts

| Shortcut      | Action                                                            |
|---------------|-------------------------------------------------------------------|
| `Super+Z`     | Start or stop dictation (global, configured in the previous step) |
| `S`           | Start or stop listening (while the app window is focused)         |
| `Escape`      | Cancel listening                                                  |
| `M`           | Minimize window                                                   |
| `Left Shift`  | Select primary language                                           |
| `Right Shift` | Select secondary language                                         |
| `Ctrl+Q`      | Quit                                                              |

The in-app shortcuts (all except the global one) are active only while the Speed of Sound window is focused.

## FAQ

Common questions about privacy, cloud providers, Wayland support, model selection, and more are answered
in the [FAQ](docs/FAQ.md).

## Support

If you run into any issues, have questions, or need troubleshooting help, please open a ticket on the
[GitHub issues page](https://github.com/zugaldia/speedofsound/issues).
