# Manual Installation

The following instructions assume an Ubuntu installation. The specific commands might change in other distributions.

## Requirements

- **Java 25**: required to run the application.
- **GStreamer**: required for audio recording (usually preinstalled, installation docs [here](https://gstreamer.freedesktop.org/documentation/installing/on-linux.html))
- **gdbus**: required to trigger the keyboard shortcut (usually preinstalled, install `libglib2.0-bin` otherwise).

On Ubuntu LTS, you can install Java 25 (current LTS) and make it the default with:

```bash
sudo apt install openjdk-25-jdk
sudo update-java-alternatives -s java-1.25.0-openjdk-amd64
```

## Download the app

Go to the [GitHub releases page](https://github.com/zugaldia/speedofsound/releases) and download two files from the latest release:

- `speedofsound.jar`: the application
- `trigger.sh`: the script that triggers a recording from a keyboard shortcut (uses `gdbus` under the hood)

For example, download both files into `$HOME/speedofsound/`. Then make the trigger script executable:

```bash
chmod +x $HOME/speedofsound/trigger.sh
```

## Run the app

Launch the application like any other Java application:

```bash
java --enable-native-access=ALL-UNNAMED -jar $HOME/speedofsound/speedofsound.jar
```

(`--enable-native-access=ALL-UNNAMED` is required to enable warning-free access to GTK/GNOME libraries
by [Java GI](https://java-gi.org/usage/#linux).)

On the first launch, two things will happen:

1. **Model setup**: The built-in Whisper Tiny model is unpacked into your user data folder.
   The app window will open and show a **Loading…** state while this happens in the background.
   Subsequent launches will be faster.
2. **Permissions prompt**: The app will ask you to grant permission to type on your behalf.
   To support both X11 and Wayland desktops without requiring root access, Speed of Sound uses XDG Desktop Portals
   for keyboard input. You must approve this prompt for dictation to work.

Once permissions are granted, press your shortcut to start dictating and press it again to stop.
The transcribed text will be typed into whatever application is currently focused.
