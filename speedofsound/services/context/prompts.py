"""

We need to provide translated prompts because some providers, like OpenAI,
require it:

https://platform.openai.com/docs/api-reference/audio/createTranscription#audio-createtranscription-prompt
https://platform.openai.com/docs/guides/speech-to-text#prompting

"""

#
# Simple prompt (for the transcription API), limited to 224 tokens.
#

SIMPLE_PROMPT_EN = """
Dictated text for typing into a desktop application.
Use proper punctuation, capitalization, and grammar.
Remove filler words like "um" and "uh".
Format as clean, ready-to-type text.
""".strip()

SIMPLE_PROMPT_ES = """
Texto dictado para escribir en una aplicación de escritorio.
Usa puntuación, mayúsculas y gramática correctas.
Elimina muletillas como "eh" y "este".
Formatea como texto limpio y listo para escribir.
""".strip()

#
# Regular prompts
#

SYSTEM_PROMPT_EN = """
# Goal
Generate a transcription of the speech.
- Fix the grammar, punctuation, and spelling errors.
- The output should only be the transcription and nothing else. 
- Do not include any additional information or timestamps.
- The transcription should be in English.
""".strip()

APPLICATION_PROMPT_EN = """
# Target Application
The transcription will be entered into the following application,
adjust the transcription to match the application's context and tone.
- Application name: {application_name}
- Window title: {window_title}
""".strip()

SYSTEM_PROMPT_ES = """
# Objetivo
Genera una transcripción del habla.
- Corrige los errores gramaticales, ortográficos, y de puntuación.
- La respuesta debe incluir únicamente la transcripción y nada más.
- No incluyas ninguna información adicional ni marcas de tiempo.
- La transcripción debe estar en español.
""".strip()

APPLICATION_PROMPT_ES = """
# Aplicación de Destino
La transcripción se introducirá en la siguiente aplicación,
ajusta la transcripción para que coincida con el contexto y tono de la aplicación.
- Nombre de la aplicación: {application_name}
- Título de la ventana: {window_title}
""".strip()

#
# Mapping of languages to prompts
#

SIMPLE_PROMPT = {
    "default": SIMPLE_PROMPT_EN,
    "en": SIMPLE_PROMPT_EN,
    "es": SIMPLE_PROMPT_ES,
}

SYSTEM_PROMPT = {
    "default": SYSTEM_PROMPT_EN,
    "en": SYSTEM_PROMPT_EN,
    "es": SYSTEM_PROMPT_ES,
}

APPLICATION_PROMPT = {
    "default": APPLICATION_PROMPT_EN,
    "en": APPLICATION_PROMPT_EN,
    "es": APPLICATION_PROMPT_ES,
}
