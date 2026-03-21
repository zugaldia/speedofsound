# Troubleshooting

## Speed of Sound appears on the wrong workspace and doesn't type into my active application

**Symptom:** The main window opens on a different workspace, and transcribed text lands in the wrong
application or nowhere at all.

**Why this happens:** When a minimized window is restored, some desktop environments bring it back
on its original workspace rather than the current one, stealing focus and breaking text input.

**How to fix it:** Enable **Hide instead of minimize** in Preferences → General → App Behavior. A
hidden window is typically restored on the active workspace instead.

**Trade-off:** The app no longer appears in the dock. To bring the main window back, use a global
shortcut (Preferences → General → Global Shortcut).

## Transcribed text is typed into the wrong application

**Symptom:** Dictation completes but the text appears in the wrong window, or the cursor jumps to an
unexpected application.

**Why this happens:** After transcription, Speed of Sound hides its window and immediately starts
typing. If the previous application hasn't had enough time to regain focus, the keystrokes land in
the wrong place.

**How to fix it:** Increase the **Post-hide delay** in Preferences → Advanced. This is the pause
between the window hiding and the first keystroke. The default is 100 ms; try increasing it in
steps of 50 ms until text reliably appears in the right application.

## Characters are missing or appear out of order

**Symptom:** The transcribed text arrives incomplete or with letters jumbled, particularly in
browser text fields or Electron-based apps.

**Why this happens:** Some applications can't process keystrokes as fast as Speed of Sound sends
them, causing dropped or reordered characters.

**How to fix it:** Increase the **Typing delay** in Preferences → Advanced. This is the pause
between each individual keystroke. The default is 10 ms; try increasing it in steps of 5–10 ms
until the output is clean.
