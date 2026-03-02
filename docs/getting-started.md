# Getting Started

> The work to integrate this application in the Flathub and the Snapcraft stores is underway.
> If you are familiar with either packaging format and would like to help, please reach out to the author.
> Support for other distribution methods like AppImage, Deb, or RPM is [on the roadmap](https://github.com/zugaldia/speedofsound/issues/4).

## Manual installation

The following instructions assume an Ubuntu installation. The specific commands might change in other distributions.

### Requirements

- **Java 25**: required to run the application.
- **GStreamer**: required for audio recording (usually preinstalled, installation docs [here](https://gstreamer.freedesktop.org/documentation/installing/on-linux.html))
- **gdbus**: required to trigger the keyboard shortcut (usually preinstalled, install `libglib2.0-bin` otherwise).

On Ubuntu LTS, you can install Java 25 (current LTS) and make it the default with:

```bash
sudo apt install openjdk-25-jdk
sudo update-java-alternatives -s java-1.25.0-openjdk-amd64
```

### Download the app

Go to the [GitHub releases page](https://github.com/zugaldia/speedofsound/releases) and download two files
from the latest release:

- `speedofsound.jar`: the application
- `trigger.sh`: the script that triggers a recording from a keyboard shortcut (uses `gdbus` under the hood)

For example, download both files into `$HOME/speedofsound/`. Then make the trigger script executable:

```bash
chmod +x $HOME/speedofsound/trigger.sh
```

### Set up a shortcut

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
