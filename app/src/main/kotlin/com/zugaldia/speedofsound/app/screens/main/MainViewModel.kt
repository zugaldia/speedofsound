package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.core.plugins.asr.WhisperAsr
import com.zugaldia.speedofsound.core.plugins.asr.WhisperOptions
import com.zugaldia.speedofsound.core.plugins.director.DirectorEvent
import com.zugaldia.speedofsound.core.plugins.director.SimpleDirector
import com.zugaldia.speedofsound.core.plugins.llm.ANTHROPIC_ENVIRONMENT_VARIABLE
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlm
import com.zugaldia.speedofsound.core.plugins.llm.AnthropicLlmOptions
import com.zugaldia.speedofsound.core.plugins.recorder.JvmRecorder
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderEvent
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.gnome.glib.GLib
import org.slf4j.LoggerFactory

class MainViewModel {
    private val logger = LoggerFactory.getLogger(MainViewModel::class.java)

    var state: MainState = MainState()
        private set

    private val recorder = JvmRecorder(RecorderOptions(computeVolumeLevel = true))
    private val asr = WhisperAsr(WhisperOptions())
    private val llm = AnthropicLlm(
        AnthropicLlmOptions(apiKey = System.getenv(ANTHROPIC_ENVIRONMENT_VARIABLE))
    )
    private val director = SimpleDirector(recorder, asr, llm)

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private var currentPipelineJob: Job? = null

    fun start() {
        logger.info("Starting.")
        recorder.initialize()
        recorder.enable()
        asr.initialize()
        asr.enable()
        llm.initialize()
        llm.enable()
        director.initialize()
        director.enable()
        collectDirectorEvents()
        collectRecorderEvents()
        state.updateStage(AppStage.IDLE)
    }

    private fun collectDirectorEvents() {
        viewModelScope.launch {
            director.events.filterIsInstance<DirectorEvent>().collect { event ->
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    when (event) {
                        is DirectorEvent.RecordingStarted -> state.updateStage(AppStage.LISTENING)
                        is DirectorEvent.TranscriptionStarted -> state.updateStage(AppStage.TRANSCRIBING)
                        is DirectorEvent.PolishingStarted -> state.updateStage(AppStage.POLISHING)
                        is DirectorEvent.PipelineCompleted -> {
                            val raw = event.rawTranscription
                            val polished = event.polishedText
                            logger.info("Pipeline completed: raw='$raw', polished='$polished'")
                            state.updateStage(AppStage.IDLE)
                        }
                        is DirectorEvent.PipelineError -> {
                            logger.error("Pipeline error at ${event.stage}: ${event.error.message}")
                            state.updateStage(AppStage.IDLE)
                        }
                        is DirectorEvent.PipelineCancelled -> state.updateStage(AppStage.IDLE)
                    }
                    false // Return false for one-shot execution
                }
            }
        }
    }

    private fun collectRecorderEvents() {
        viewModelScope.launch {
            recorder.events.filterIsInstance<RecorderEvent>().collect { event ->
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    when (event) {
                        is RecorderEvent.RecordingLevel -> state.updateRecordingLevel(event.level)
                    }
                    false // Return false for one-shot execution
                }
            }
        }
    }

    fun toggleListening() {
        if (state.currentStage() == AppStage.IDLE) {
            currentPipelineJob = viewModelScope.launch {
                director.start()
            }
        } else if (state.currentStage() == AppStage.LISTENING) {
            viewModelScope.launch {
                director.stop()
            }
        }
    }

    fun cancelListening() {
        if (state.currentStage() in listOf(AppStage.LISTENING, AppStage.TRANSCRIBING, AppStage.POLISHING)) {
            currentPipelineJob?.cancel()
            viewModelScope.launch {
                director.cancel()
            }
        }
    }

    fun shutdown() {
        logger.info("Shutting down.")
        viewModelScope.cancel()
        director.disable()
        director.shutdown()
        llm.disable()
        llm.shutdown()
        asr.disable()
        asr.shutdown()
        recorder.disable()
        recorder.shutdown()
    }
}
