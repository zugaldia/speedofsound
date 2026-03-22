# Getting Started

> The work to integrate this application in the Flathub and Snapcraft stores is underway.
> If you are familiar with either packaging format and would like to help, please reach out to the author.
> Support for other distribution methods like AppImage, Deb, or RPM is [on the roadmap](https://github.com/zugaldia/speedofsound/issues/4).

## 1. Installation

Follow the instructions under [Manual Installation](manual-installation.md).

## 2. Accept Remote Desktop Permissions

When Speed of Sound is first launched, the main window displays a banner with a **Start** button.
Clicking it will prompt your system to grant the application the permissions it needs to type on your behalf.

![Main screen](assets/screenshots/main-banner-light.png#only-light)
![Main screen](assets/screenshots/main-banner-dark.png#only-dark)

After clicking **Start**, your system will show the Remote Desktop permissions dialog.
You must check **Allow remote interaction**, which is required for the application to simulate keyboard input.
You should also check **Remember this selection**. Without it, you will need to re-accept these permissions every
time you launch Speed of Sound.

![Remote Desktop Permissions Dialog](assets/screenshots/remote-desktop-permissions-light.png#only-light)
![Remote Desktop Permissions Dialog](assets/screenshots/remote-desktop-permissions-dark.png#only-dark)

Under the hood, Speed of Sound uses the [XDG Desktop Portal](https://flatpak.github.io/xdg-desktop-portal/) standard
to simulate keyboard input. This is supported by all major desktop environments, including GNOME and KDE,
and works on both X11 and Wayland.

## 3. Set up a shortcut

"By default, [$APPLICATION_SHORTCUT_TRIGGER] starts and stops listening, but only when " +
"the $APPLICATION_NAME window is open and focused.\n\n" +
"For a better experience, we recommend setting up a global shortcut in Preferences. " +
"This lets you keep the window minimized or hidden and trigger $APPLICATION_NAME " +
"from anywhere, typing directly into any app.",

Follow the instructions under [Set Up a Keyboard Shortcut](keyboard-shortcut.md).
