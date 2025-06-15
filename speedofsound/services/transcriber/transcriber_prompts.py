# TODO: Add app/window name
# "The CORRECTION will be used in online messaging, so it should avoid
# sophisticated language and avoid symbols like hyphens, semicolons, etc."


PROMPT_TEMPLATE_EN = """
Generate a transcription of the speech.
Fix the grammar, punctuation, and spelling errors.
The output should be only the transcription and nothing else. 
Do not include any additional information or timestamps.
The audio is in English.
{CUSTOM_PROMPT}
""".strip()

# OpenAI requires the prompt to match the audio language.
# https://platform.openai.com/docs/api-reference/audio/createTranscription#audio-createtranscription-prompt
PROMPT_TEMPLATE_ES = """
Genera una transcripción del habla.
Corrige los errores gramaticales, ortográficos, y de puntuación.
La respuesta debe ser únicamente la transcripción y nada más.
No incluyas ninguna información adicional ni marcas de tiempo.
El audio está en Español.
{CUSTOM_PROMPT}
""".strip()

# FIXME: Add support for other languages
PROMPTS = {
    "en": PROMPT_TEMPLATE_EN,
    "es": PROMPT_TEMPLATE_ES,
}
