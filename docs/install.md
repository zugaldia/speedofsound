# Manual Installation

> **🤝 Help Wanted**
> We're looking for contributors familiar with Flatpak or Snapcraft to help finalize packaging for these distribution platforms. If you have experience with either, your contribution would be greatly appreciated. Please reach out if you'd like to help make Speed of Sound more accessible to users.

The recommended way to install Speed of Sound is via Flathub or Snapcraft. However, support for both is in progress. In the meantime, this document describes how to install the application manually. You can also [install the app in a regular Python virtual environment](development.md).

**Note:** The following instructions were validated on Ubuntu 24.04 LTS. Package names and other steps may vary slightly depending on your distribution. 

## Prerequisites

Install the required system dependencies:

```bash
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

## Installation Steps

1. **Clone the repository** (or download the source):

```bash
git clone https://github.com/zugaldia/speedofsound.git
cd speedofsound
```

2. **Install Python dependencies**:

```bash
pip3 install --user -r requirements.txt
```

**Note:** You may need to pass the `--break-system-packages` flag to install the packages locally.

3. **Build and install using Meson**:

```bash
make install
```

4. **Update your PATH** (if needed):

The app launcher is installed to `~/.local/bin/speedofsound` and its code to `~/.local/lib/python3/dist-packages`. Make sure both are added to your environment by adding these lines to `~/.bashrc` or `~/.profile`:

```bash
export PATH="$HOME/.local/bin:$PATH"
export PYTHONPATH="$HOME/.local/lib/python3/dist-packages:$PYTHONPATH"
```

Then reload your shell:
```bash
source ~/.bashrc
```

5. **Launch the application**:

```bash
speedofsound
```

The first time you launch the application, it will take a few minutes to download the default local small Whisper model. Once installed, you can use the application preferences to choose other models, both local and cloud-based. 

## Uninstalling

To remove the application:

```bash
cd speedofsound
make uninstall
```
