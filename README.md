<div align="center">
  <img src="assets/logo-square-512.png" width="250" alt="Speed of Sound logo">
</div>

# Speed of Sound

Speed of Sound enables voice typing on the Linux desktop, allowing you to type at over 100 words per minute—more than double the average typing speed with a keyboard—and reduce the risk of repetitive strain injury (RSI).

This is the app in action, typing into Text Editor:
<div align="center">
  <img src="assets/sos-text-editor.gif" alt="Speed of Sound typing into the GNOME Text Editor">
</div>

## Features

- 🏠 **Local and Cloud Options** - Works with local speech recognition models (like Whisper and NVIDIA Riva) as well as cloud providers (ElevenLabs, Google Gemini, NVIDIA NIM, and OpenAI)
- 🖥️ **Cross-Platform Compatibility** - Supports both X11 and Wayland with pluggable typing backends (AT-SPI, `xdotool`, `ydotool`)
- 🔌 **GNOME Shell Extension** - Provides a system-wide keyboard shortcut and desktop notifications
- 🎨 **Modern UI** - Built with GNOME Adwaita design system, compatible with any desktop environment
- 🎮 **Accessibility** - Joystick/gamepad control support

## Launch the App

> **Flatpak and Snap support** Support for both is well underway. If you are familiar with either, please review and provide feedback on the current setup. The plan is to submit the app to Flathub and Snapcraft soon™.

Clone the repository, install dependencies, and launch the application:

```bash
# Install system dependencies
sudo apt install libgirepository-2.0-dev

# Clone and set up the project
git clone git@github.com:zugaldia/speedofsound.git
cd speedofsound
python3 -m venv venv
source venv/bin/activate
pip3 install -r requirements.txt

# Launch the application
python3 launch.py
```

## Configuration

Speed of Sound uses a `config.toml` file for all settings. When you first launch the application, it will automatically create a default configuration file from the included example if one doesn't exist.

The default configuration uses a local Whisper model for speech recognition. For additional providers and configuration options, see the [configuration documentation](docs/config.md).

If you want to customize your configuration before the first launch, you can manually copy the example configuration and edit it:

```bash
cp config.example.toml config.toml
```

### ⚠️ Wayland Compatibility

Wayland has stricter security restrictions than X11 for keyboard event simulation, which is required for voice typing functionality.

Speed of Sound automatically detects your display server and selects the appropriate typing backend. On Wayland, we use `ydotool` instead of traditional X11-only tools like `xdotool` or AT-SPI. However, `ydotool` may require additional configuration.

> **Troubleshooting:** If you see speech being transcribed but not typed into applications, see the [typist backend configuration guide](docs/advanced.md#typist-backend-selection). 

## GNOME Shell Extension

Speed of Sound includes an optional GNOME Shell extension for enhanced functionality. The extension provides:

- Status indicator in the top bar
- System-wide keyboard shortcut
- Desktop notifications

**Installation**: [Follow the extension setup instructions](./extension/README.md) 

## Activation

Choose how to activate voice input:

1. **GNOME Shell Extension** (Recommended) - Use `Super+Z` to start/stop voice typing from any application
2. **Custom Keyboard Shortcut** - Alternatively, set up a global shortcut without the extension
3. **Joystick/Gamepad** - Or use a connected controller for activation and language switching

For keyboard shortcut and joystick setup, see the [trigger configuration guide](docs/trigger.md).

Once activated, you can cancel the recording by pressing **Escape**. Recording will automatically stop after 60 seconds (configurable in `config.toml`, see below).

### ⚠️ Privacy Considerations

By default, Speed of Sound is preconfigured to work fully offline using Whisper without requiring an internet connection. When operating this way, no data leaves your computer—everything runs locally on your device. We don't collect metrics, analytics, or any sort of telemetry. And you don't need to take our word for it, the code is open source.

However, if your machine lacks the processing power to run a speech recognition model locally or you need higher quality transcription from larger cloud-based models, cloud providers are also supported. The choice of which model to use is entirely yours. Keep in mind that while cloud providers are convenient to set up and typically inexpensive, your audio data is shared with third parties, so you should review their terms of service and privacy policies.

> **Tip:** One common pattern is maintaining separate configuration files for different use cases. For example, you could have a `config-local.toml` for sensitive work where no data should leave your computer, and a `config-cloud.toml` for less sensitive situations like typing into public websites or generating public content.

## Reporting Issues

If you encounter any bugs, have feature requests, or need help with Speed of Sound, please open an issue on this repository:

**[Report an Issue](https://github.com/zugaldia/speedofsound/issues)**

When reporting issues, please include:
- Your operating system and version
- The model and configuration you're using
- Steps to reproduce the issue
- Any relevant error messages or logs
