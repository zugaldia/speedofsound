# Configure virtual keyboard permissions

Speed of Sound emulates a keyboard to type into any desktop application. The app automatically detects your display server and selects the appropriate typing backend:
- **X11 sessions**: Uses `xdotool`
- **Wayland sessions**: Uses `ydotool`

The following sections explain how to install and configure these tools. 

## Installation Requirements

### For X11 users
**xdotool**: Usually pre-installed and doesn't require any additional permissions or setup. If not available, install with:
```bash
sudo apt install xdotool
```

### For Wayland users
**ydotool**: Requires installation and configuration:

```bash
# Install ydotool
sudo apt install ydotool

# Grant necessary permissions
sudo chmod +s $(which ydotool)
```

**Security Note**: The `chmod +s` command grants elevated privileges to `ydotool`. This is required for Wayland's security model but should be applied carefully. Consider your system's security requirements before applying this change.

## Typing Backends

Speed of Sound provides several typing backends that can be configured based on your system requirements.

Most users don't need to configure this manually - the app automatically selects the correct backend based on your display server. You can change the backend in the application preferences under **Advanced → Typist → Typist Backend**.

Available backends:
- `"Auto"` - **(Default)** Automatically detects display server and selects the appropriate backend
- `"xdotool"` - Uses the `xdotool` command (X11 only)
- `"ydotool"` - Uses the `ydotool` command (works on both Wayland and X11)
- `"AT-SPI"` - Uses GTK accessibility API (X11 only)

Only change this setting if you experience typing issues or want to use a specific backend. Note that changing this setting requires an application restart to take effect.
