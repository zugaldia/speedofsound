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
