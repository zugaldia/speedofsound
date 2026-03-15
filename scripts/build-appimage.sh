#!/bin/bash

# Build an AppImage from the jpackage app-image output (which is NOT a valid AppImage, but it's close).
# The version is always read from the VERSION file.
# Requires appimagetool to be available in PATH.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
APP_DIR="$ROOT_DIR/app/build/jpackage/speedofsound"
OUTPUT_DIR="$ROOT_DIR/app/build/jpackage"
VERSION="$(tr -d '[:space:]' < "$ROOT_DIR/VERSION")"
ARCH="$(uname -m)"

if [ ! -d "$APP_DIR" ]; then
    echo "Error: jpackage app-image directory not found at $APP_DIR"
    echo "Run 'make jpackage-app-image' first."
    exit 1
fi

# AppRun: symlink to the jpackage native launcher
ln -sf bin/speedofsound "$APP_DIR/AppRun"

# Desktop file under usr/share/applications (for appstreamcli and FHS convention),
# symlinked to the AppDir root (required by the AppImage spec)
mkdir -p "$APP_DIR/usr/share/applications"
cat > "$APP_DIR/usr/share/applications/io.speedofsound.SpeedOfSound.desktop" << 'DESKTOP'
[Desktop Entry]
Version=1.0
Type=Application
Name=Speed of Sound
Comment=Voice typing for the Linux desktop
Categories=Utility;Accessibility;
Keywords=voice;typing;dictation;transcription;speech;microphone;whisper;
Icon=speedofsound
Exec=speedofsound
Terminal=false
StartupNotify=true
DESKTOP
ln -sf usr/share/applications/io.speedofsound.SpeedOfSound.desktop \
    "$APP_DIR/io.speedofsound.SpeedOfSound.desktop"

# Icon at root (appimagetool requires it here, matching Icon= above)
ln -sf lib/speedofsound.png "$APP_DIR/speedofsound.png"

# .DirIcon: required by AppDir spec for thumbnailers and file managers
ln -sf lib/speedofsound.png "$APP_DIR/.DirIcon"

# AppStream metadata
mkdir -p "$APP_DIR/usr/share/metainfo"
cp "$ROOT_DIR/data/io.speedofsound.SpeedOfSound.metainfo.xml.in" \
    "$APP_DIR/usr/share/metainfo/io.speedofsound.SpeedOfSound.appdata.xml"

# Build the AppImage, APPIMAGE_EXTRACT_AND_RUN=1 avoids a FUSE dependency
OUTPUT="$OUTPUT_DIR/speedofsound-${VERSION}-${ARCH}.AppImage"
APPIMAGE_EXTRACT_AND_RUN=1 appimagetool "$APP_DIR" "$OUTPUT"
echo "AppImage created: $OUTPUT"
