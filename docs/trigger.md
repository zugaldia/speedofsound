# Trigger Methods

Besides the GNOME Shell Extension, Speed of Sound (SOS) provides two additional methods to activate voice input, each designed for different use cases and accessibility needs. Once activated, the application will capture your voice, transcribe it using your configured AI model, and type the result into the currently active window.

## Keyboard Shortcut

A keyboard shortcut allows you to activate voice input from any application without switching windows.

### GNOME System Settings

To set up a keyboard shortcut:

1. Open **Settings** from the applications menu
2. Navigate to **Keyboard**
3. In the **Keyboard Shortcuts** section, click **View and Customize Shortcuts**
4. Scroll down to **Custom Shortcuts** and click the **+** button
5. Configure the shortcut:
   - **Name**: "Speed of Sound"
   - **Command**: Full path to trigger script (i.e., `/path/to/speedofsound/scripts/trigger.sh`)
   - **Shortcut**: Your preferred key combination (e.g., **Super+Z**)

![Set Custom Shortcut Dialog](../assets/sos-shortcut.png)

**Make the script executable:**
```bash
chmod +x /path/to/speedofsound/scripts/trigger.sh
```

Once configured, press your keyboard shortcut from any application to activate voice input.

## Joystick/Gamepad

Joystick/gamepad control provides enhanced accessibility for users who prefer physical button controls or need hands-free activation.

### Basic Controls
- **B button**: Start/stop voice recording and transcription
- **Left button**: Switch to English (configurable)
- **Right button**: Switch to Spanish (configurable)

### Configuration
Configure joystick settings in `config.toml`:
- **joystick_id**: Specify which controller to use (if multiple connected), default is `0`
- **joystick_language_left/right**: Customize language assignments

For detailed configuration options, see the [configuration documentation](config.md).
