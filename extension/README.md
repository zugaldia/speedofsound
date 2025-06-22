# Speed of Sound GNOME Shell Extension

This GNOME Shell extension provides a top bar indicator for the Speed of Sound application.

## About top bar icons

As it turns out, top bar icons are a controversial topic in Linux these days :-)

There is [a specification](https://www.freedesktop.org/wiki/Specifications/StatusNotifierItem/) but there's no consensus around it. This recent [discussion thread](https://gitlab.freedesktop.org/xdg/xdg-specs/-/issues/205) provides a good summary of where things stand. [This video](https://www.youtube.com/watch?v=02nFos3iHlo) also does a good job explaining the longer story and context.

You can use Speed of Sound without a top bar icon, but we believe its usability improves significantly with one. This is because Speed of Sound isn't your typical desktop application: it runs in the background, its window only appears when triggered, and it needs to interact with any other application on your desktop. In other words, a top bar icon is the right pattern for this use case.

GNOME Shell extensions are currently the recommended way to add icons to the top bar, which is exactly what this extension does. Most of Speed of Sound's logic remains in the main application—this extension is minimal by design. It provides a visual color-coded indicator showing the application's status, surfaces error messages through desktop notifications, and offers basic interactions with the background application.

## Features

- Adds an icon to the top bar to show the application status.
- Surfaces errors through desktop notifications.
- Allows showing and quitting the background application. 

## Installation

### Manual Installation (Current)

This extension is not yet available on the [GNOME Extensions website](https://extensions.gnome.org/), so installation must be done manually. In the future, it will be available for easy installation through the official website.

To install manually:

1. **Pack the extension:**
   ```bash
   make pack-extension
   ```

2. **Install the extension:**
   ```bash
   make install-extension
   ```

3. **Enable the extension:**
   ```bash
   make enable-extension
   ```

### Development

For development and testing:

- **Disable the extension:** `make disable-extension`
- **Lint the code:** `make lint`
- **Test schema:** `make test-schema`

After making changes, repeat the pack → install → enable workflow to test your modifications.
