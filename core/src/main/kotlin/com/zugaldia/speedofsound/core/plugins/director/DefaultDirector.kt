package com.zugaldia.speedofsound.core.plugins.director

import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.plugins.asr.AsrPlugin
import com.zugaldia.speedofsound.core.plugins.asr.AsrRequest
import com.zugaldia.speedofsound.core.plugins.llm.LlmPlugin
import com.zugaldia.speedofsound.core.plugins.llm.LlmRequest
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderPlugin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
class DefaultDirector(
    private val recorder: RecorderPlugin<*>,
    private val asr: AsrPlugin<*>,
    private val llm: LlmPlugin<*>,
    options: DirectorOptions = DirectorOptions(),
) : DirectorPlugin<DirectorOptions>(options) {
    private val pipelineMutex = Mutex()

    @Volatile
    private var isCancelled = false

    override suspend fun start() {
        pipelineMutex.withLock {
            try {
                isCancelled = false
                emitEvent(DirectorEvent.RecordingStarted)
                withContext(Dispatchers.IO) { recorder.startRecording() }
            } catch (e: CancellationException) {
                throw e
            } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
                log.error("Failed to start recording: ${e.message}")
                emitEvent(DirectorEvent.PipelineError(PipelineStage.RECORDING, e))
            }
        }
    }

    override suspend fun stop() {
        pipelineMutex.withLock {
            if (isCancelled) {
                log.info("Pipeline was cancelled, skipping stop.")
                return@withLock
            }

            try {
                executePipeline()
            } catch (e: CancellationException) {
                log.info("Pipeline cancelled via coroutine cancellation: ${e.message}")
                emitEvent(DirectorEvent.PipelineCancelled)
            } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
                handlePipelineError(e)
            }
        }
    }

    @Suppress("ReturnCount")
    private suspend fun executePipeline() {
        val audioData = stopRecordingAndGetData() ?: return
        val rawTranscription = transcribeAudio(audioData) ?: return
        if (rawTranscription.isBlank()) {
            log.warn("Empty transcription, skipping LLM polishing.")
            val error = IllegalStateException("Empty transcription")
            emitEvent(DirectorEvent.PipelineError(PipelineStage.TRANSCRIPTION, error))
            return
        }

        val polishedText = polishWithLlm(rawTranscription)
        val finalResult = polishedText ?: rawTranscription
        emitEvent(DirectorEvent.PipelineCompleted(rawTranscription, polishedText, finalResult))
    }

    @Suppress("ReturnCount")
    private suspend fun stopRecordingAndGetData(): ByteArray? {
        val recorderResult = withContext(Dispatchers.IO) { recorder.stopRecording() }
        val response = recorderResult.getOrElse { error ->
            log.error("Failed to stop recording: ${error.message}")
            emitEvent(DirectorEvent.PipelineError(PipelineStage.RECORDING, error))
            return null
        }

        return if (isCancelled) {
            emitEvent(DirectorEvent.PipelineCancelled)
            null
        } else {
            response.audioData
        }
    }

    @Suppress("ReturnCount")
    private suspend fun transcribeAudio(audioData: ByteArray): String? {
        emitEvent(DirectorEvent.TranscriptionStarted)
        val floatAudio = AudioManager.convertPcm16ToFloat(audioData)
        val transcriptionResult = withContext(Dispatchers.IO) { asr.transcribe(AsrRequest(floatAudio)) }
        val rawTranscription = transcriptionResult.getOrElse { error ->
            log.error("Transcription failed: ${error.message}")
            emitEvent(DirectorEvent.PipelineError(PipelineStage.TRANSCRIPTION, error))
            return null
        }

        return if (isCancelled) {
            emitEvent(DirectorEvent.PipelineCancelled)
            null
        } else {
            rawTranscription.text
        }
    }

    private suspend fun polishWithLlm(rawTranscription: String): String? {
        emitEvent(DirectorEvent.PolishingStarted)
        val prompt = buildPrompt(rawTranscription)
        val llmResult = withContext(Dispatchers.IO) { llm.generate(LlmRequest(text = prompt)) }
        val polishedText = llmResult.getOrElse { error ->
            log.error("LLM polishing failed: ${error.message}. Raw transcription was: $rawTranscription.")
            emitEvent(DirectorEvent.PipelineError(PipelineStage.POLISHING, error))
            return null
        }

        return polishedText.text
    }

    private fun buildPrompt(rawTranscription: String): String {
        val context = currentOptions.customContext.ifBlank { DEFAULT_CONTEXT }
        val vocabulary = currentOptions.customVocabulary.ifEmpty { DEFAULT_VOCABULARY }.joinToString(", ")
        return PROMPT_TEMPLATE
            .replace(PROMPT_KEY_CONTEXT, context)
            .replace(PROMPT_KEY_VOCABULARY, vocabulary)
            .replace(PROMPT_KEY_LANGUAGE, currentOptions.language.name)
            .replace(PROMPT_KEY_INPUT, rawTranscription.trim())
    }

    private suspend fun handlePipelineError(e: Throwable) {
        if (isCancelled) {
            emitEvent(DirectorEvent.PipelineCancelled)
        } else {
            log.error("Pipeline error: ${e.message}")
            emitEvent(DirectorEvent.PipelineError(PipelineStage.RECORDING, e))
        }
    }

    override suspend fun cancel() {
        isCancelled = true
        if (recorder.isCurrentlyRecording()) {
            withContext(Dispatchers.IO) {
                recorder.stopRecording().onFailure { error ->
                    log.error("Failed to stop recording during cancel: ${error.message}")
                }
            }
        }

        emitEvent(DirectorEvent.PipelineCancelled)
        log.info("Pipeline cancelled.")
    }
}
