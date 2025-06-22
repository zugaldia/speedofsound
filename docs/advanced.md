# Advanced Configuration

This document covers advanced configuration options for Speed of Sound.

## Typist Backend Selection

Typing into applications across different display servers (X11, Wayland) presents unique challenges. X11 allows applications to freely emit keyboard events, while Wayland's security model is more restrictive.

Speed of Sound automatically detects your display server and selects the most appropriate backend. Manual selection is only needed if you encounter issues or want to override the automatic choice.

This setting is optional:

```toml
typist_backend = "xdotool"
```

Available backends:
- `"xdotool"` - X11-based typing (default for X11 sessions)
- `"ydotool"` - Universal input tool (default for Wayland sessions) 
- `"atspi"` - GTK accessibility API (default when session type is unknown)

### Installation Requirements

- [`xdotool`](https://github.com/jordansissel/xdotool) - tends to work out of the box
- [`ydotool`](https://github.com/ReimuNotMoe/ydotool) - may require additional user permissions for input access

Due to Wayland's security model, `ydotool` requires elevated permissions to access input devices. One solution is to set the setuid bit, allowing any user to run it with the necessary privileges:

```bash
sudo chmod +s $(which ydotool)
```

**Security Note**: Setting the setuid bit should be done carefully and intentionally, as it grants elevated privileges to all users on the system. The instructions here are provided as a guide, but ultimately it is the user's responsibility to configure their system appropriately for their security requirements.

**Test the configuration**:

```bash
ydotool type "Hello World"
```

You should see "Hello World" typed in the current terminal window, confirming that `ydotool` is working correctly.

## Joystick/Gamepad Control

If you have one or more joysticks connected to your system, you can use them to start and stop the app. You can also use left and right buttons to switch the input language.

This section is optional:

```toml
joystick_id = 0
joystick_language_left = "en"
joystick_language_right = "es"
```

- `joystick_id`: Joystick device ID (as detected by PyGame)
- `joystick_language_left`: Language for left joystick button
- `joystick_language_right`: Language for right joystick button
