# Getting Started

> The work to integrate this application in the Flathub and Snapcraft stores is underway.
> If you are familiar with either packaging format and would like to help, please reach out to the author.
> Support for other distribution methods like AppImage, Deb, or RPM is [on the roadmap](https://github.com/zugaldia/speedofsound/issues/4).

## 1. Installation

Follow the instructions under [Manual Installation](manual-installation.md).

## 2. Set up a shortcut

In this step, you will assign a global keyboard shortcut that starts and stops dictation.
The exact steps vary by desktop environment. On GNOME, for example:

1. Open **Settings** and navigate to **Keyboard**.
2. Click **View and Customize Shortcuts**.
3. Scroll to the bottom and select **Custom Shortcuts**.
4. Click the **+** button to add a new shortcut and fill in the fields:
    - **Name:** anything you like, e.g. `Speed of Sound`
    - **Command:** the full path to the trigger script, e.g. `/home/your-username/speedofsound/trigger.sh`
    - **Shortcut:** press your desired key combination, e.g. `Super+Z`
5. Click **Add** to save.

You can add multiple shortcuts targeting the same trigger script if you wish to.

## 3. Accept Remote Desktop Permissions

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
