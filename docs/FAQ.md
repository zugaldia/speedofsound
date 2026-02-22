# Frequently Asked Questions

## Why do I need to set up a trigger script for the global keyboard shortcut?

Ideally, Speed of Sound would register a global shortcut automatically using the XDG Desktop Global
Shortcuts Portal — a standard interface that allows applications to request system-wide key bindings
in a desktop-agnostic way. That would make setup seamless and require no manual steps.

However, this portal is a fairly recent addition to the Linux desktop and is still not widely supported
across desktop environments. Because we cannot rely on it being available on most systems today, Speed of
Sound instead exposes a D-Bus interface that you can call from anywhere. The `trigger.sh` script is a
thin wrapper around that interface, designed to be plugged into whatever shortcut or automation mechanism
your desktop environment already provides.

It is an awkward setup, and we know it. As support for the portal improves across desktop environments,
we plan to adopt it and remove this manual step.

## Why is the download so large?

The application JAR is around 100 MB, which may seem large for a desktop app. Here is why.

Speed of Sound is designed to work out of the box: install it, launch it, and start dictating — no separate
model downloads, no accounts, no internet connection required. To make that possible, the Whisper Tiny
multilingual model is bundled directly into the JAR. This model is what powers on-device transcription
from the very first launch.

Even without the bundled model, the application would still be sizable. On-device AI inference requires
native libraries that must be packaged alongside the app. These cannot be reduced away.

The tradeoff is intentional: we prioritize ease of use over a smaller download. If you need a specific
language or higher accuracy, additional models can be downloaded from within the app — but you never
have to do that just to get started.
