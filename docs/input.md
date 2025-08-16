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

Most users don't need to configure this manually - the app automatically selects the correct backend. Only override this setting if you experience typing issues or want to use a specific backend:

```toml
typist_backend = "xdotool"
```

Available backends:
- `"xdotool"` - Default for X11. Uses the `xdotool` command (X11 only)
- `"ydotool"` - Default for Wayland. Uses the `ydotool` command (works on both Wayland and X11)
- `"atspi"` - Uses GTK accessibility API (X11 only)
