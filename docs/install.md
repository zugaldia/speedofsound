# Manual Installation

The recommended way to install Speed of Sound is via Flathub or Snapcraft. However, support for both is in progress. In the meantime, here is another way to install the application.

## Prerequisites

First, clone the repository and install the required system dependencies:

```bash
# Clone and navigate to the repository
git clone git@github.com:zugaldia/speedofsound.git
cd speedofsound

# Install system dependencies
sudo apt install python3 python3-pip python3-dev gcc pkg-config meson ninja-build gettext libgirepository-2.0-dev libcairo2-dev gir1.2-gtk-4.0
```

## Using Python

This method is preferred for development and contributing to the application. After completing the prerequisites above, set up the Python development environment:

### Install PyGObject and GTK

Before we can run the application we need to install PyGObject, GTK and their dependencies. Follow the instructions for your platform on https://pygobject.gnome.org/getting_started.html

### Set up the development environment

```bash
python3 -m venv venv
source venv/bin/activate
pip3 install -r requirements.txt

# Launch the application
python3 launch.py
```
