# Speed of Sound

Speed of Sound (SOS) provides voice input to any Linux desktop application, powered by state-of-the-art generative AI models, both local and cloud. 

## Features

- 🏠 Support for both local (Whisper, NVIDIA RIVA) and cloud providers (ElevenLabs, Google, NVIDIA NIM, OpenAI).
- 🎨 Developed with GNOME Adwaita for a modern look and compatibility with any desktop environment.
- 🎮 Joystick/gamepad control for enhanced accessibility.

## Launch the App

Clone this repository, install the dependencies in a virtual environment, and launch the app with Python:

```bash
$ git clone git@github.com:zugaldia/speedofsound.git
$ cd speedofsound
$ python3 -m venv venv
$ source venv/bin/activate
$ pip3 install -r requirements.txt
$ python3 launch.py
```

## Activation

There are three main mechanisms to activate voice input:

1. **Main Screen Button** - Tap the button on the main application window
2. **Keyboard Shortcut** - Set up a global keyboard shortcut for quick access
3. **Joystick/Gamepad** - Use connected gamepad buttons for activation

In all cases, once transcription completes, the application will minimize itself to type the text into the active window. For more details on how to set up each mechanism, refer to the [docs/trigger.md](docs/trigger.md) file.

## Configure the App

All configuration is managed through the `config.toml` file at the root of the repository. The easiest way to start is to copy the example file:

```bash
$ cp config.example.toml config.toml
```

By default, the configuration is set up to use a local Whisper server. For additional configuration options, refer to the [docs/config.md](docs/config.md) file.

## Reporting Issues

If you encounter any bugs, have feature requests, or need help with Speed of Sound, please open an issue on this repository:

**[Report an Issue](https://github.com/zugaldia/speedofsound/issues)**

When reporting issues, please include:
- Your operating system and version
- The model and configuration you're using
- Steps to reproduce the issue
- Any relevant error messages or logs
