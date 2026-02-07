package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.POST_HIDE_DELAY_MS
import com.zugaldia.speedofsound.app.portals.TextUtils
import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_SECONDARY_LANGUAGE
import com.zugaldia.speedofsound.core.desktop.settings.KEY_CUSTOM_CONTEXT
import com.zugaldia.speedofsound.core.desktop.settings.KEY_CUSTOM_VOCABULARY
import com.zugaldia.speedofsound.core.desktop.settings.KEY_DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.languageFromIso2
import com.zugaldia.speedofsound.core.plugins.asr.WhisperAsr
import com.zugaldia.speedofsound.core.plugins.director.DirectorEvent
import com.zugaldia.speedofsound.core.plugins.director.SimpleDirector
import com.zugaldia.speedofsound.core.plugins.llm.GoogleLlm
import com.zugaldia.speedofsound.core.plugins.recorder.JvmRecorder
import com.zugaldia.speedofsound.core.plugins.recorder.RecorderEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.gnome.glib.GLib
import org.slf4j.LoggerFactory

@Suppress("TooManyFunctions")
class MainViewModel(
    private val settingsClient: SettingsClient,
    private val portalsClient: PortalsClient
) {
    private val logger = LoggerFactory.getLogger(MainViewModel::class.java)

    var state: MainState = MainState()
        private set

    private val recorder = JvmRecorder(settingsClient.getRecorderOptions())
    private val asr = WhisperAsr(settingsClient.getWhisperOptions())
    private val llm = GoogleLlm(settingsClient.getGoogleLlmOptions())
    private val director = SimpleDirector(recorder, asr, llm, settingsClient.getDirectorOptions())

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
        collectSettingsChanges()
        checkForPortalsRestoreToken()
        state.updateStage(AppStage.IDLE)
    }

    fun onTriggerAction() {
        logger.info("Trigger action called.")
        toggleListening()
    }

    private fun collectDirectorEvents() {
        viewModelScope.launch {
            director.events.filterIsInstance<DirectorEvent>().collect { event ->
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    when (event) {
                        is DirectorEvent.RecordingStarted -> state.updateStage(AppStage.LISTENING)
                        is DirectorEvent.TranscriptionStarted -> state.updateStage(AppStage.TRANSCRIBING)
                        is DirectorEvent.PolishingStarted -> state.updateStage(AppStage.POLISHING)
                        is DirectorEvent.PipelineCompleted -> onPipelineCompleted(event)
                        is DirectorEvent.PipelineCancelled -> onPipelineCancelled()
                        is DirectorEvent.PipelineError -> onPipelineError(event)
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

    private fun collectSettingsChanges() {
        viewModelScope.launch {
            settingsClient.settingsChanged.collect { key ->
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    refreshSettings(key)
                    false // Return false for one-shot execution
                }
            }
        }
    }

    private fun refreshSettings(key: String) {
        when (key) {
            KEY_DEFAULT_LANGUAGE -> onPrimaryLanguageSelected()
            KEY_CUSTOM_CONTEXT -> director.updateOptions(director.getOptions().copy(
                customContext = settingsClient.getCustomContext()))
            KEY_CUSTOM_VOCABULARY -> director.updateOptions(director.getOptions().copy(
                customVocabulary = settingsClient.getCustomVocabulary()))
        }
    }

    private fun checkForPortalsRestoreToken() {
        val token = settingsClient.getPortalsRestoreToken()
        if (token.isNotBlank()) {
            state.updatePortalsRestoreTokenMissing(false)
            startPortalsSession(token)
        } else {
            state.updatePortalsRestoreTokenMissing(true)
        }
    }

    fun startPortalsSession(token: String? = null) {
        val restoreToken = token?.ifBlank { null }
        viewModelScope.launch {
            val result = portalsClient.startRemoteDesktopSession(restoreToken)
            result.onSuccess { response ->
                val newToken = response.restoreToken
                if (!newToken.isNullOrBlank()) {
                    settingsClient.setPortalsRestoreToken(newToken)
                }
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    state.updatePortalsRestoreTokenMissing(newToken.isNullOrBlank())
                    false // Return false for one-shot execution
                }
            }.onFailure { error ->
                logger.error("Failed to start portals session", error)
            }
        }
    }

    fun onPrimaryLanguageSelected() {
        val language = languageFromIso2(settingsClient.getDefaultLanguage()) ?: DEFAULT_LANGUAGE
        if (language == state.currentLanguage()) return
        state.updateLanguage(language)
        asr.updateOptions(asr.getOptions().copy(language = language))
        director.updateOptions(director.getOptions().copy(language = language))
    }

    fun onSecondaryLanguageSelected() {
        val language = languageFromIso2(settingsClient.getSecondaryLanguage()) ?: DEFAULT_SECONDARY_LANGUAGE
        if (language == state.currentLanguage()) return
        state.updateLanguage(language)
        asr.updateOptions(asr.getOptions().copy(language = language))
        director.updateOptions(director.getOptions().copy(language = language))
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

    private fun onPipelineCompleted(event: DirectorEvent.PipelineCompleted) {
        logger.info("Pipeline completed: $event")
        hideAndReset()
        viewModelScope.launch {
            delay(POST_HIDE_DELAY_MS) // Wait for the main window to fully go away before typing
            val text = event.polishedText.ifBlank { event.rawTranscription }
            val finalText = if (text.isNotBlank() && !text.endsWith(" ")) { "$text " } else { text }
            TextUtils.textToKeySym(finalText)
                .onSuccess { keySyms -> portalsClient.typeText(keySyms) }
                .onFailure { error -> logger.error("Error converting text to key symbols: ${error.message}") }
        }
    }

    private fun onPipelineCancelled() {
        hideAndReset()
    }

    private fun onPipelineError(event: DirectorEvent.PipelineError) {
        logger.error("Pipeline error at ${event.stage}: ${event.error.message}")
        hideAndReset()
    }

    private fun hideAndReset() {
        state.emitPipelineCompleted() // Signals the main window to hide
        state.updateStage(AppStage.IDLE)
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
