# Troubleshooting

You can test your audio and transcription settings from the terminal using the included `launch.py` utility.

## Testing the Transcription System

To verify that speech recognition is working correctly:

```bash
python launch.py --verify-speech
```

This command will test your microphone and speech recognition configuration without launching the full GUI application. It will check if the system can properly capture and transcribe audio input.

## Testing the Typing System

To verify that the virtual keyboard is configured correctly:

```bash
python launch.py --verify-keyboard
```

This command will test the virtual keyboard functionality to ensure the application can simulate keyboard input on your system.
