# Context

You can supply additional context to some providers to improve the transcription. For example, you can decide to provide a custom prompt or the name of the active application.

## Custom prompts

You can provide language-specific custom prompts to improve transcription quality with providers like OpenAI. These prompts are automatically loaded and included in the transcription context.

### Setup

Create a markdown file in your configuration directory with the naming pattern `prompt_{language_id}.md`, where `{language_id}` is the language code (e.g., `en`, `es`, `fr`). For example, for English, the file would be under `~/.config/io.speedofsound.App/prompt_en.md`.

You decide the content of this file with any information that is useful to your use case. An example based on real use is provided below. It's recommended that this file not be too long to keep requests fast and the context relevant.

> **Note:** Not all transcription providers support this feature. Currently, only OpenAI models take advantage of this additional prompt. 

### Usage

The custom prompts are automatically loaded and cached when the application starts. They are appended to the system prompt to provide additional transcription guidance.

**Example custom prompt file (`prompt_en.md`):**

```markdown
# Additional Instructions
- Make sure that company names are spelled correctly, for example: XXX.
- Make sure that product names are spelled correctly, for example: YYY.
- Make sure that people names are spelled correctly, for example: ZZZ.
```

(Logically replace the XXX, YYY, ZZZ placeholders in this example with terms that are relevant to you.)

## Active Application (experimental)

Speed of Sound can optionally include information about the active application window to provide better transcription context.

```toml
[context]
include_application = false
```

- `include_application`: Include active application and window information in transcription prompts (default: false)
