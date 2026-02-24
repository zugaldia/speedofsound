# Frequently Asked Questions

## Does my audio or text get sent to the cloud?

No. Everything runs on your device by default. Audio and text never leave your machine **unless you
explicitly add API keys in the settings** and enable a cloud-based provider. Cloud connectivity is
optional and can happen at two points in the pipeline: transcription (ASR) and text polishing (LLM).
Neither is enabled unless you configure it.

## Why do you support cloud providers at all?

On-device inference is the default and works well for most users. Cloud providers are disabled unless
you configure them. We added support for them because not everyone has hardware capable of running
models that meet their accuracy needs. This matters especially for users who rely on this app for
accessibility reasons, where transcription quality is not just a preference but a requirement. Cloud
providers give those users a path to better results without needing a powerful machine. On-device
remains the recommended starting point for everyone.

## Does it work on Wayland? How about X11?

Yes. Speed of Sound uses [XDG Desktop Portals](https://flatpak.github.io/xdg-desktop-portal/docs/) for keyboard input,
which works on both X11 and Wayland. Under the hood, it uses the [Stargate library](https://github.com/zugaldia/stargate/)
by the same author. On the first launch you will be prompted to grant the app permission to type on your behalf.
You need to approve that prompt for dictation to work.

## Why does the app ask for permission to type on my behalf?

This is the [XDG Remote Desktop Portal](https://flatpak.github.io/xdg-desktop-portal/docs/doc-org.freedesktop.portal.RemoteDesktop.html)
requesting access to simulate keyboard input. It is the standard sandboxed mechanism for typing into other
applications without requiring root or elevated privileges. It is also the only way to make typing work inside
sandboxed packaging formats like Flatpak and Snap. The app cannot type anything without your explicit approval.

## Which Whisper model should I use?

The bundled Whisper Tiny model is a good starting point and works well for some use cases. If you need
better accuracy, especially for technical vocabulary or challenging accents, download a larger model
from within the app. All models in the Whisper family are supported (Tiny, Base, Small, Medium, Large, Turbo),
including English-only and optimized ones ("distilled"). Larger models are slower and use more memory, so it is
worth trying smaller ones first.

## What does text polishing do, and do I need it?

Text polishing is an optional step that sends the raw transcript to an LLM for further improvement.
It is disabled by default and, if enabled, it adds some latency depending on the provider and model you choose.
It is most useful in two ways: giving the LLM instructions (writing style, your intended audience, etc.)
and supplying a custom vocabulary of names, companies, or technical terms that speech recognition tends to mishear.
Whisper alone might get you 95% of the way there, polishing with the right context can close that gap.

## I have multiple microphones, can I choose which one to use?

No, the app currently uses your system default microphone. There is no in-app microphone selector, though we
might add one. Under the hood, recording uses
[GStreamer with `autoaudiosrc`](https://gstreamer.freedesktop.org/documentation/autodetect/autoaudiosrc.html),
which automatically detects the appropriate audio source to use. To change the microphone, adjust the default
input device in your system sound settings.

## Why do I need to set up a trigger script for the global keyboard shortcut?

The [XDG Desktop Global Shortcuts Portal](https://flatpak.github.io/xdg-desktop-portal/docs/doc-org.freedesktop.portal.GlobalShortcuts.html)
would allow Speed of Sound to register a global shortcut automatically, but it is still not widely supported across
desktop environments. Until it is, the`trigger.sh` script provides a manual alternative that works with whatever
shortcut system your desktop already has. We know it is awkward and plan to make this step optional once portal
support improves.

## Why is the download so large?

The JAR is large because it bundles the ~100 MB Whisper Tiny model, so the app works out of the box
with no extra setup. Even without the model, the app would still be large because the included on-device AI
inference engines require native libraries that cannot be stripped out. The size is a deliberate tradeoff
in favor of ease of use.

## Where does the app store my models, settings, and logs?

Audio and transcriptions are never written to disk. They are discarded as soon as they are processed.
In the future we may add a history or statistics feature, but that is not currently in place.
Settings are stored using GSettings unless you run the JAR standalone, in which case they are stored
in a properties file, typically under `$HOME/.local/share/speedofsound/speedofsound.properties`.

File storage follows standard desktop conventions to work correctly in Flatpak and Snap environments.
Downloaded models are stored under `$SNAP_USER_COMMON` (when running as a Snap),
or `$XDG_DATA_HOME/speedofsound` (when that variable is set), falling back to
`$HOME/.local/share/speedofsound`.

## Was this application "vibe-coded"?

If you are asking whether this application was cobbled together in a couple of evenings by sending a few prompts to
an LLM and hoping for the best, the answer is no. If you are asking whether a coding agent was used to assist during
development, the answer is yes.

Speed of Sound was built with a deliberate focus on maintainability and correctness, supported by
a growing test suite. Any AI-generated code was reviewed before being included, if only because the author relies on
this application for everyday use and wants all users to have the same reliable experience he expects for himself.
In other words, any bugs or bad design decisions are the author's fault.

The same is expected from any contributors to this project. You may or may not use a coding agent to contribute to
this project, but the PR author is ultimately responsible for the quality of their contributions. Coding agents are
not an excuse to take shortcuts. In fact, the opposite is true.
