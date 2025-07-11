# Manual Installation

The recommended way to install Speed of Sound is via Flathub or Snapcraft. However, this support is in progress, and in the meantime, these are other ways to install the application.

## Using Meson (recommended)

The provided `Makefile` has the necessary targets to build and install the application locally using Meson:

```bash
$ sudo apt install meson ninja-build
$ make meson-setup
$ make meson-build
$ make meson-install
$ make run-local
```

## Development

This is the preferred method if you are planning to contribute to the application:

```bash
# Install system dependencies
sudo apt install libgirepository-2.0-dev

# Clone and set up the project
git clone git@github.com:zugaldia/speedofsound.git
cd speedofsound
python3 -m venv venv
source venv/bin/activate
pip3 install -r requirements.txt

# Launch the application
python3 launch.py
```
