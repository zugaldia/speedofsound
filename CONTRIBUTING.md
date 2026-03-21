# Contributing to Speed of Sound

Contributions are welcome. Please use the [GitHub issues page](https://github.com/zugaldia/speedofsound/issues)
to guide and track your work.

The use of coding agents during development is accepted, with the understanding that regardless of how a contribution
is created, the author is ultimately responsible for its quality, correctness, and adherence to best practices and
the project's established patterns. For more information, see the [FAQ](https://www.speedofsound.io/faq/).

## Building from Source

### Requirements

- **Java 25** — required to build and run the application
- **GStreamer** — required for audio recording (usually preinstalled)
- **GLib utilities** — provides `gdbus`, required for manual shortcut triggering (usually preinstalled)
- **make** — required to run build commands

On Ubuntu:

```bash
sudo apt install openjdk-25-jdk gstreamer1.0-tools libglib2.0-bin git-lfs make
sudo update-java-alternatives -s java-1.25.0-openjdk-amd64
```

On Fedora:

```bash
sudo dnf install java-25-openjdk-devel gstreamer1-plugins-base-tools glib2 git-lfs make
sudo alternatives --set java /usr/lib/jvm/java-25-openjdk/bin/java
```

### Cloning the repository

This repository uses [Git LFS](https://git-lfs.com/) to store the embedded Whisper model files.
Make sure Git LFS is installed before cloning, otherwise the model files will be missing:

```bash
git lfs install
git clone https://github.com/zugaldia/speedofsound.git
```

If you already cloned without LFS, run `git lfs pull` to download the missing files.

### Build commands

All commands use the Makefile, which wraps Gradle:

```bash
make build        # Build all modules
make run          # Clean and run the GUI app in dev mode
make check        # Run all checks including detekt static analysis
make clean        # Clean build artifacts
make shadow-build # Create fat JAR at app/build/libs/speedofsound.jar
make shadow-run   # Build and run the fat JAR
```
