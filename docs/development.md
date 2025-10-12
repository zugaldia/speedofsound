# Development

During development, you don't need to install the application on the system to see your changes. Simply create a virtual environment with the Python dependencies and run it directly. 

## Prerequisites

First, clone the repository and install the required system dependencies:

```bash
# Clone and navigate to the repository
git clone https://github.com/zugaldia/speedofsound.git
cd speedofsound

# Install system dependencies
sudo apt update
sudo apt install -y \
   build-essential \
   cmake \
   gettext \
   git \
   libcairo2-dev \
   libgirepository-2.0-dev \
   meson \
   ninja-build \
   pkg-config \
   python3 \
   python3-dev \
   python3-gi \
   python3-gi-cairo \
   python3-pip \
   xdotool \
   ydotool
```

## Install PyGObject and GTK

Before you can run the application, you need to install PyGObject, GTK, and their dependencies. Follow the instructions for your platform at https://pygobject.gnome.org/getting_started.html.

## Set up the development environment

```bash
python3 -m venv venv
source venv/bin/activate
pip3 install -r requirements.txt
```

## Launch the app

Install the preferences schema the first time you set up the application and anytime you make subsequent modifications:

```bash
make install-schema
```

Launch the application:

```bash
make run
```

## Development commands

The provided `Makefile` includes convenience targets for development:

```bash
make test         # Run pytests
make lint         # Run ruff linter
make format-check # Run ruff format check
```
