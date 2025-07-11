# Configure virtual keyboard permissions

Speed of Sound emulates a keyboard to type into any desktop application. By default, Speed of Sound uses `pynput` to emulate a virtual keyboard. Under the hood, this uses the kernel `uinput` module, which works across Wayland and X11 sessions.

The trade-off, however, is the need for additional user permissions to be able to use this subsystem:

## System Configuration

You will need to do two things:

First, you need to add your user to the input group.

```bash
$ sudo usermod -aG input $USER
```

Second, you need to add the provided udev rules to your system so that this input group can access the uinput subsystem. 

```bash
$ sudo cp /path/to/scripts/99-uinput.rules /etc/udev/rules.d/
$ sudo udevadm control --reload-rules && sudo udevadm trigger
```

Once you have completed these steps, you will need to logout/login (or reboot) for the changes to take effect.

You can verify your configuration is working correctly by running the keyboard verification test. See [`troubleshooting.md`](troubleshooting.md) for more details.

## Alternative Backends (advanced)

Besides `pynput`, Speed of Sound provides other typing backends that might be better suited for your system.

Generally, you will not want to change this setting, particularly under Wayland which has stricter security restrictions, but there is a bit more flexibility under X11. 

```toml
typist_backend = "xdotool"
```

Available backends:
- `"pynput"` - This is the default.
- `"xdotool"` - Uses the `xdotool` command (valid for X11 sessions)
- `"ydotool"` - Uses the `ydotool` command (valid for both Wayland and X11 sessions)
- `"atspi"` - GTK accessibility API (X11 only)

### Installation Requirements

- [`xdotool`](https://github.com/jordansissel/xdotool) - tends to work out of the box
- [`ydotool`](https://github.com/ReimuNotMoe/ydotool) - requires additional user permissions for input access

Due to Wayland's security model, `ydotool` requires elevated permissions to access input devices. One solution is to set the setuid bit, allowing any user to run it with the necessary privileges:

```bash
sudo chmod +s $(which ydotool)
```

**Security Note**: Setting the setuid bit should be done carefully and intentionally, as it grants elevated privileges to all users on the system. The instructions here are provided as a guide, but ultimately it is the user's responsibility to configure their system appropriately for their security requirements.
