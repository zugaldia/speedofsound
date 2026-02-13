package com.zugaldia.speedofsound.core.plugins.director

import com.zugaldia.speedofsound.core.audio.AudioManager
import com.zugaldia.speedofsound.core.plugins.AppPluginCategory
import com.zugaldia.speedofsound.core.plugins.AppPluginRegistry
import com.zugaldia.speedofsound.core.plugins.asr.AsrPlugin
import com.zugaldia.speedofsound.core.plugins.asr.AsrRequest
import com.zugaldia.speedofsound.core.plugins.llm.LlmPlugin
import com.zugaldia.speedofsound.core.plugins.llm.LlmRequest
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderPlugin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
class DefaultDirector(
    private val registry: AppPluginRegistry,
    options: DirectorOptions = DirectorOptions(),
) : DirectorPlugin<DirectorOptions>(options) {
    override val id: String = ID

    private val pipelineMutex = Mutex()

    companion object {
        const val ID = "DIRECTOR_DEFAULT"
    }

    @Volatile
    private var isCancelled = false

    private val directorJob = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.error("Unhandled error in director coroutine: ${throwable.message}", throwable)
    }

    private val directorScope = CoroutineScope(Dispatchers.Default + directorJob + exceptionHandler)
    private var autoStopJob: Job? = null

    private lateinit var recorder: RecorderPlugin<*> // Required
    private lateinit var asr: AsrPlugin<*> // Required
    private var llm: LlmPlugin<*>? = null // Optional

    override suspend fun start() {
        pipelineMutex.withLock {
            try {
                isCancelled = false
                recorder = registry.getActive(
                    AppPluginCategory.RECORDER) as? RecorderPlugin<*> ?: error("No recorder plugin available")
                asr = registry.getActive(
                    AppPluginCategory.ASR) as? AsrPlugin<*> ?: error("No ASR plugin available")
                llm = registry.getActive(AppPluginCategory.LLM) as? LlmPlugin<*>
                emitEvent(DirectorEvent.RecordingStarted)
                startAutoStopTimer()
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
            cancelAutoStopTimer()
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

        val polishedText = if (currentOptions.enableTextProcessing) { polishWithLlm(rawTranscription) } else { null }
        val finalResult = polishedText ?: rawTranscription
        emitEvent(DirectorEvent.PipelineCompleted(rawTranscription, polishedText, finalResult))
    }

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
        val currentLlm = llm ?: return null
        emitEvent(DirectorEvent.PolishingStarted)
        val prompt = buildPrompt(rawTranscription)
        val llmResult = withContext(Dispatchers.IO) { currentLlm.generate(LlmRequest(text = prompt)) }
        return llmResult.fold(onSuccess = { it.text }, onFailure = { error ->
            log.error("LLM polishing failed: ${error.message}. Raw transcription was: $rawTranscription.")
            emitEvent(DirectorEvent.PipelineError(PipelineStage.POLISHING, error))
            null
        })
    }

    private fun buildPrompt(rawTranscription: String): String {
        val context = currentOptions.customContext.ifBlank { DEFAULT_CONTEXT }
        val vocabulary = currentOptions.customVocabulary.ifEmpty { DEFAULT_VOCABULARY }.joinToString(", ")
        return PROMPT_TEMPLATE.replace(PROMPT_KEY_CONTEXT, context)
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
        cancelAutoStopTimer()
        isCancelled = true
        if (::recorder.isInitialized && recorder.isCurrentlyRecording()) {
            withContext(Dispatchers.IO) {
                recorder.stopRecording().onFailure { error ->
                    log.error("Failed to stop recording during cancel: ${error.message}")
                }
            }
        }

        emitEvent(DirectorEvent.PipelineCancelled)
        log.info("Pipeline cancelled.")
    }

    private fun startAutoStopTimer() {
        cancelAutoStopTimer()
        autoStopJob = directorScope.launch {
            delay(currentOptions.maxRecordingDurationMs)
            log.info("Auto-stop timer triggered after ${currentOptions.maxRecordingDurationMs}ms")
            directorScope.launch { stop() } // Launch stop in a separate coroutine to avoid self-cancellation
        }
    }

    private fun cancelAutoStopTimer() {
        autoStopJob?.cancel()
        autoStopJob = null
    }

    override fun shutdown() {
        super.shutdown()
        cancelAutoStopTimer()
        directorScope.cancel()
    }
}
