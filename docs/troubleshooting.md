# Troubleshooting

## Speed of Sound appears on the wrong workspace and doesn't type into my active application

**Symptom:** On a multi-workspace setup, the main Speed of Sound window opens on a different workspace,
and transcribed text lands in the wrong application or nowhere at all.

**Why this happens:** When a minimized window is restored, some desktop environments bring it back
on its original workspace rather than the current one, stealing focus and breaking text input.
By default, Speed of Sound minimizes the main window before typing.

**How to fix it:** Enable **Hide instead of minimize** in `Preferences` → `General` → `App Behavior`.
A hidden window is typically restored on the active workspace instead.

**Trade-off:** The app no longer appears in the dock. To bring the main window back, you can, for example, use a
global shortcut (`Preferences` → `General` → `Global Shortcut`).

## I don't want the Speed of Sound main window to show every time I dictate.

**Symptom:** The main window appears on every dictation, which feels intrusive for frequent voice typing.

**Why this happens:** By default, Speed of Sound presents its main window each time you trigger dictation
to show recording and transcription progress.

**How to fix it:** Enable **Record in background** in `Preferences` → `General` → `App Behavior`.
When active, recording, transcription, and typing all run silently without showing the main window.

**Note:** Because Speed of Sound uses the Desktop Portals standard, your desktop environment will still
display a microphone in-use indicator while recording is active and while the app has typing permissions.
This serves as a lightweight substitute for the in-app progress display.

## Transcribed text is incomplete

**Symptom:** Typically, the beginning of the text is missing.

**Why this happens:** After transcription, Speed of Sound minimizes or hides its main window,
waits briefly to give the desktop environment time to do so, and then starts typing.
If the wait is too short, the first few characters will be lost because they land in the
Speed of Sound window rather than your target application.

**How to fix it:** Increase the **Post-hide delay** in `Preferences` → `Advanced`. This is the pause
between the window hiding and the first keystroke. Alternatively, enable **Record in background** (see above).

## Some characters are missing or appear out of order

**Symptom:** The transcribed text arrives incomplete or with letters jumbled.

**Why this happens:** Two things can cause this. First, some applications can't process
keystrokes as fast as Speed of Sound sends them, causing dropped or reordered characters. Second, some desktop
environments have trouble typing special characters like tildes or the `ñ` in `piñata`.

**How to fix it:** Increase the **Typing delay** in `Preferences` → `Advanced`. This is the pause
between each individual keystroke. You can also increase this value if you simply like the effect of
text appearing more gradually. For special characters, Speed of Sound does some automatic sanitization
(for example, replacing `á` with `a` or `ñ` with `n`). Customizing these substitutions is not currently supported.
If the current behavior doesn't work for your use case, [let us know](https://www.speedofsound.io/support/).
