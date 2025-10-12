# Development

During development, you don't need to install the application on the system to see your changes. You can simply create a virtual environment with the Python dependencies and run it directly. 

## Prerequisites

First, clone the repository and install the required system dependencies:

```bash
# Clone and navigate to the repository
git clone git@github.com:zugaldia/speedofsound.git
cd speedofsound

# Install system dependencies
sudo apt update
sudo apt install -y \
    build-essential \
    gettext \
    gir1.2-adw-1 \
    gir1.2-gtk-4.0 \
    libcairo2-dev \
    libgirepository1.0-dev \
    meson \
    ninja-build \
    pkg-config \
    python3 \
    python3-dev \
    python3-gi \
    python3-gi-cairo \
    python3-pip
```

### Install PyGObject and GTK

Before you can run the application, you need to install PyGObject, GTK, and their dependencies. Follow the instructions for your platform at https://pygobject.gnome.org/getting_started.html

## Set up the development environment

```bash
python3 -m venv venv
source venv/bin/activate
pip3 install -r requirements.txt
```

## Launch the app

```bash
# Launch the application
python3 launch.py
```

## Development commands

The provided `Makefile` includes convenience targets for development:

```bash
make test         # Run pytests
make lint         # Run ruff linter
make format-check # Run ruff format check
```
