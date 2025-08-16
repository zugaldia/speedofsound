"""

We need to provide translated prompts because some providers, like OpenAI,
require it:

https://platform.openai.com/docs/api-reference/audio/createTranscription#audio-createtranscription-prompt
https://platform.openai.com/docs/guides/speech-to-text#prompting

"""

#
# English
#

# Some local LLMs need simplified prompts for chat completions transcriptions.
# https://huggingface.co/ibm-granite/granite-speech-3.3-2b#usage-with-vllm
SIMPLE_PROMPT_EN = """
Can you transcribe the speech into a written format?
""".strip()

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

#
# Spanish
#

SIMPLE_PROMPT_ES = """
¿Puedes transcribir el discurso a un formato escrito?
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
