# User Guide

Launch the application like any other Java application:

```bash
java --enable-native-access=ALL-UNNAMED -jar $HOME/speedofsound/speedofsound.jar
```

On the first launch, two things will happen:

1. **Model setup**: The built-in Whisper Tiny model is unpacked into your user data folder.
   This is automatic and the app will start faster in the future.
2. **Permissions prompt**: The app will ask you to grant permission to type on your behalf.
   To support both X11 and Wayland desktops without requiring root access, Speed of Sound uses XDG Desktop Portals
   for keyboard input. You must approve this prompt for dictation to work.

Once permissions are granted, press your shortcut to start dictating and press it again to stop.
The transcribed text will be typed into whatever application is currently focused.

## Keyboard shortcuts

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
