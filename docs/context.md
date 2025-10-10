# Context

You can supply additional context to some providers to improve the transcription. For example, you can decide to provide custom prompts or include the name of the active application.

## Custom Prompts

Speed of Sound supports two types of custom prompts that you can customize per language to improve transcription quality:

### 1. System Prompt (Full Context)

Used by advanced models that support the Chat Completions API (e.g., GPT Audio models). This prompt can include detailed instructions and context.

**File naming:** `prompt_{language_id}.txt`

**Example location:** `~/.config/io.speedofsound.App/prompt_en.txt`

**Used by:**
- OpenAI GPT Audio models (gpt-audio, gpt-audio-mini)
- OpenAI GPT-4o Audio Preview models

**Example system prompt file (`prompt_en.txt`):**

```
# Additional Instructions
- Make sure that company names are spelled correctly, for example: XXX.
- Make sure that product names are spelled correctly, for example: YYY.
- Make sure that people names are spelled correctly, for example: ZZZ.
- Format technical terms consistently: API, JSON, GitHub, PostgreSQL.
- Use American English spelling conventions.
```

### 2. Simple Prompt (Concise)

Used by models that support the Audio Transcription API. This prompt is limited to 224 tokens and should be concise and focused.

**File naming:** `prompt_simple_{language_id}.txt`

**Example location:** `~/.config/io.speedofsound.App/prompt_simple_en.txt`

**Used by:**
- OpenAI Whisper models (whisper-1)
- OpenAI Transcription models (gpt-4o-transcribe, gpt-4o-mini-transcribe)

**Example simple prompt file (`prompt_simple_en.txt`):**

```
Transcribe dictated text for a desktop application.
Use proper punctuation and capitalization.
Remove filler words like "um" and "uh".
Format technical terms correctly: API, JSON, GitHub.
```

### Setup

1. Create text files in your configuration directory (`~/.config/io.speedofsound.App/`)
2. Use the naming pattern `prompt_{language_id}.txt` for system prompts or `prompt_simple_{language_id}.txt` for simple prompts
3. Replace `{language_id}` with the language code (e.g., `en`, `es`, `fr`)
4. Add your custom instructions

**Important notes:**
- Custom prompts are automatically loaded and cached when the application starts
- Simple prompts are limited to 224 tokens (approximately 170 words)
- Keep prompts concise to maintain fast response times
- Custom prompts are supported by OpenAI models only (Faster Whisper does not use custom prompts)
- Replace placeholder terms (XXX, YYY, ZZZ) with content relevant to your use case

### Which Prompt Type Should I Use?

- If you're using **GPT Audio models** → customize the **system prompt** (`prompt_{language_id}.txt`)
- If you're using **Whisper or transcription models** → customize the **simple prompt** (`prompt_simple_{language_id}.txt`)
- You can create both types to support all OpenAI models seamlessly

## Active Application (experimental)

Speed of Sound can optionally include information about the active application window to provide better transcription context. This setting is available in the application's **Advanced Preferences** page under the **Context** section.

Enable "Include Application Context" to include active application and window information in transcription prompts (default: disabled).
