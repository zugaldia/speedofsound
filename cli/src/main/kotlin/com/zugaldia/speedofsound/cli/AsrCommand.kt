package com.zugaldia.speedofsound.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.zugaldia.speedofsound.core.Language
import com.zugaldia.speedofsound.core.audio.AudioInfo
import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.models.voice.DEFAULT_ASR_MODEL_ID
import com.zugaldia.speedofsound.core.plugins.asr.AsrPlugin
import com.zugaldia.speedofsound.core.plugins.asr.AsrRequest
import com.zugaldia.speedofsound.core.plugins.asr.WhisperAsr
import com.zugaldia.speedofsound.core.plugins.asr.WhisperOnnxAsr
import com.zugaldia.speedofsound.core.plugins.asr.WhisperOptions
import org.slf4j.LoggerFactory
import java.nio.file.Path

class AsrCommand : CliktCommand(name = "asr") {
    override val printHelpOnEmptyArgs = true
    private val logger = LoggerFactory.getLogger(AsrCommand::class.java)

    private val inputFile: Path by argument(
        name = "wav",
        help = "Path to the WAV file to transcribe"
    ).path(mustExist = true, canBeDir = false, mustBeReadable = true)

    private val modelId: String by option(
        "--model", "-m",
        help = "Model ID to use for transcription"
    ).default(DEFAULT_ASR_MODEL_ID)

    private val languageCode: String by option(
        "--language", "-l",
        help = "Language code (ISO 639-1, e.g., 'en', 'es', 'fr')"
    ).default(DEFAULT_LANGUAGE.iso2)

    private val provider: String by option(
        "--provider", "-p",
        help = "ASR provider to use (onnx or sherpa)"
    ).default("sherpa")

    override fun run() {
        logger.info("Loading audio file: $inputFile")
        val (samples, sampleRate) = AudioManager.loadFromWav(inputFile)
        logger.info("Audio file loaded: sample rate = $sampleRate Hz, ${samples.size} samples")

        // Find the language from the language code
        val language = Language.all.find { it.iso2 == languageCode }
            ?: throw IllegalArgumentException("Unknown language code: $languageCode")

        logger.info("Using provider: $provider, model: $modelId, language: ${language.name} ($languageCode)")
        val options = WhisperOptions(modelID = modelId, language = language)
        val asr: AsrPlugin<*> = when (provider.lowercase()) {
            "onnx" -> WhisperOnnxAsr(options)
            "sherpa" -> WhisperAsr(options)
            else -> throw IllegalArgumentException("Unknown provider: $provider. Use 'onnx' or 'sherpa'.")
        }

        asr.initialize()
        asr.enable()

        logger.info("Transcribing audio.")
        val result = asr.transcribe(AsrRequest(samples, AudioInfo.Default))
        result.onSuccess { response ->
            logger.info("Transcription: ${response.text}")
            println(response.text)
        }.onFailure { error ->
            logger.error("Transcription failed: ${error.message}", error)
        }

        asr.disable()
        asr.shutdown()
    }
}
