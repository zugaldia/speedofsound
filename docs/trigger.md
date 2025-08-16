# Set up how to trigger the app

Speed of Sound runs in the background waiting to be triggered when you want to start voice typing. You can set up a keyboard shortcut and/or a joystick as triggers.

> ⚠️ **Important**: You MUST configure at least one trigger method below for Speed of Sound to work. The app runs as a background service and cannot be activated without a keyboard shortcut or joystick trigger.

## Keyboard shortcut (using the Extension)

If you install the provided GNOME Shell extension, the extension will automatically set up `Super+Z` as a global keyboard shortcut to trigger Speed of Sound.

Read [`extension/README.md`](../extension/README.md) for installation instructions.

## Keyboard shortcut (using System Settings)

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

## Joystick

If you have a joystick connected to your system, you can use it to trigger the application and switch the language for voice typing.

### Basic Controls
- **B button**: Start/stop the app
- **Left button**: Switch to English for input (configurable)
- **Right button**: Switch to Spanish for input (configurable)

### Configuration
Configure joystick settings in `config.toml`:

```toml
joystick_id = 0
joystick_language_left = "en"
joystick_language_right = "es"
```

- `joystick_id`: Joystick device ID (as detected by PyGame)
- `joystick_language_left`: Language for left joystick button
- `joystick_language_right`: Language for right joystick button
