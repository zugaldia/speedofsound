# Manual Installation

The recommended way to install Speed of Sound is via Flathub or Snapcraft. However, support for both is in progress. In the meantime, here is how to install the application manually on Ubuntu.

> **🤝 Help Wanted**
> We're looking for contributors familiar with Flatpak or Snapcraft to help finalize packaging for these distribution platforms. If you have experience with either, your contribution would be greatly appreciated. Please reach out if you'd like to help make Speed of Sound more accessible to users.

## Prerequisites

Install the required system dependencies:

```bash
sudo apt update
sudo apt install -y \
  build-essential \
  meson \
  ninja-build \
  pkg-config \
  gir1.2-adw-1 \
  gir1.2-gtk-4.0 \
  libcairo2-dev \
  libgirepository1.0-dev \
  python3 \
  python3-dev \
  python3-gi \
  python3-gi-cairo \
  python3-pip
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

3. **Build and install using Meson**:
   ```bash
   make install
   ```

4. **Update your PATH** (if needed):

   Ensure `~/.local/bin` is in your PATH by adding this to your `~/.bashrc` or `~/.profile`:
   ```bash
   export PATH="$HOME/.local/bin:$PATH"
   ```

   Then reload your shell:
   ```bash
   source ~/.bashrc
   ```

5. **Launch the application**:
   ```bash
   speedofsound
   ```

## Uninstalling

To remove the application:

```bash
cd speedofsound
make uninstall
```
