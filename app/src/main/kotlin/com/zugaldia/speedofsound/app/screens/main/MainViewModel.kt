package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.app.isGStreamerDisabled
import com.zugaldia.speedofsound.app.POST_HIDE_DELAY_MS
import com.zugaldia.speedofsound.app.plugins.recorder.GStreamerRecorder
import com.zugaldia.speedofsound.app.portals.PortalsSessionManager
import com.zugaldia.speedofsound.app.portals.TextUtils
import com.zugaldia.speedofsound.app.settings.AsrProviderManager
import com.zugaldia.speedofsound.app.settings.LlmProviderManager
import com.zugaldia.speedofsound.core.desktop.portals.PortalsClient
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.desktop.settings.DEFAULT_SECONDARY_LANGUAGE
import com.zugaldia.speedofsound.core.desktop.settings.KEY_CREDENTIALS
import com.zugaldia.speedofsound.core.desktop.settings.KEY_CUSTOM_CONTEXT
import com.zugaldia.speedofsound.core.desktop.settings.KEY_CUSTOM_VOCABULARY
import com.zugaldia.speedofsound.core.desktop.settings.KEY_DEFAULT_LANGUAGE
import com.zugaldia.speedofsound.core.desktop.settings.KEY_SELECTED_TEXT_MODEL_PROVIDER_ID
import com.zugaldia.speedofsound.core.desktop.settings.KEY_SELECTED_VOICE_MODEL_PROVIDER_ID
import com.zugaldia.speedofsound.core.desktop.settings.KEY_TEXT_MODEL_PROVIDERS
import com.zugaldia.speedofsound.core.desktop.settings.KEY_TEXT_PROCESSING_ENABLED
import com.zugaldia.speedofsound.core.desktop.settings.KEY_VOICE_MODEL_PROVIDERS
import com.zugaldia.speedofsound.core.desktop.settings.SettingsClient
import com.zugaldia.speedofsound.core.languageFromIso2
import com.zugaldia.speedofsound.core.plugins.AppPluginCategory
import com.zugaldia.speedofsound.core.plugins.AppPluginRegistry
import com.zugaldia.speedofsound.core.plugins.director.DefaultDirector
import com.zugaldia.speedofsound.core.plugins.director.DirectorEvent
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

    private val registry = AppPluginRegistry()

    private val recorder = if (isGStreamerDisabled()) {
        JvmRecorder(settingsClient.getRecorderOptions())
    } else {
        GStreamerRecorder(settingsClient.getRecorderOptions())
    }

    private val director = DefaultDirector(registry, settingsClient.getDirectorOptions())

    private val portalsSessionManager = PortalsSessionManager(
        portalsClient = portalsClient,
        settingsClient = settingsClient,
        initialSessionDisconnected = true,
        initialRestoreTokenMissing = settingsClient.getPortalsRestoreToken().isBlank(),
    )

    private val asrProviderManager = AsrProviderManager(registry, settingsClient)
    private val llmProviderManager = LlmProviderManager(registry, settingsClient)

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private var currentPipelineJob: Job? = null

    fun start() {
        // Register and initialize all plugins
        registry.register(AppPluginCategory.RECORDER, recorder)
        asrProviderManager.registerAsrPlugins()
        llmProviderManager.registerLlmPlugins()
        registry.register(AppPluginCategory.DIRECTOR, director)

        // Set active plugins
        registry.setActiveById(AppPluginCategory.RECORDER, recorder.id)
        asrProviderManager.activateSelectedProvider()
        llmProviderManager.activateSelectedProvider()
        registry.setActiveById(AppPluginCategory.DIRECTOR, DefaultDirector.ID)

        collectDirectorEvents()
        collectRecorderEvents()
        collectSettingsChanges()
        collectPortalsSessionState()
        portalsSessionManager.initialize(viewModelScope)
        state.updateStage(AppStage.IDLE)

        // Initialize status UI labels
        onPrimaryLanguageSelected(forceUpdate = true)
        updateModelLabels()
    }

    fun onTriggerAction() {
        // Safeguard: only proceed if we're in IDLE (to start) or LISTENING (to stop) state
        val currentStage = state.currentStage()
        if (currentStage != AppStage.IDLE && currentStage != AppStage.LISTENING) {
            logger.info("Ignoring trigger action during processing stage: $currentStage")
            return
        }

        // Check if the portal session needs reconnection. This typically happens when the user locks the screen and
        // comes back. The remote desktop session is closed in those circumstances for security reasons.
        portalsSessionManager.attemptReconnect(viewModelScope)
        logger.info("Trigger action invoked.")
        toggleListening()
    }

    private fun collectDirectorEvents() {
        viewModelScope.launch {
            director.events.filterIsInstance<DirectorEvent>().collect { event ->
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    when (event) {
                        is DirectorEvent.RecordingStarted -> onRecordingStarted()
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

    private fun collectPortalsSessionState() {
        viewModelScope.launch {
            portalsSessionManager.isSessionDisconnected.collect { isDisconnected ->
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    state.setPortalsSessionDisconnected(isDisconnected)
                    false // Return false for one-shot execution
                }
            }
        }
        viewModelScope.launch {
            portalsSessionManager.isRestoreTokenMissing.collect { isMissing ->
                GLib.idleAdd(GLib.PRIORITY_DEFAULT) {
                    state.updatePortalsRestoreTokenMissing(isMissing)
                    false // Return false for one-shot execution
                }
            }
        }
    }

    private fun onRecordingStarted() {
        state.updateStage(AppStage.LISTENING)
    }

    private fun refreshSettings(key: String) {
        when (key) {
            KEY_DEFAULT_LANGUAGE -> onPrimaryLanguageSelected()
            KEY_CUSTOM_CONTEXT -> director.updateOptions(
                director.getOptions().copy(customContext = settingsClient.getCustomContext())
            )

            KEY_CUSTOM_VOCABULARY -> director.updateOptions(
                director.getOptions().copy(customVocabulary = settingsClient.getCustomVocabulary())
            )

            KEY_TEXT_PROCESSING_ENABLED -> {
                director.updateOptions(
                    director.getOptions().copy(enableTextProcessing = settingsClient.getTextProcessingEnabled())
                )
                updateModelLabels()
            }

            KEY_SELECTED_VOICE_MODEL_PROVIDER_ID -> {
                asrProviderManager.activateSelectedProvider()
                updateModelLabels()
            }

            KEY_VOICE_MODEL_PROVIDERS -> {
                asrProviderManager.refreshProviderConfiguration()
                updateModelLabels()
            }

            KEY_SELECTED_TEXT_MODEL_PROVIDER_ID -> {
                llmProviderManager.activateSelectedProvider()
                updateModelLabels()
            }

            KEY_TEXT_MODEL_PROVIDERS -> {
                llmProviderManager.refreshProviderConfiguration()
                updateModelLabels()
            }

            KEY_CREDENTIALS -> {
                asrProviderManager.refreshProviderConfiguration()
                llmProviderManager.refreshProviderConfiguration()
            }
        }
    }

    private fun updateModelLabels() {
        val asrModelName = asrProviderManager.getCurrentProviderName()
        val llmModelName = llmProviderManager.getCurrentProviderName()
        state.updateAsrModel(asrModelName)
        state.updateLlmModel(llmModelName)
    }

    fun startPortalsSession(token: String? = null) {
        portalsSessionManager.startSession(viewModelScope, token)
    }

    fun onPrimaryLanguageSelected(forceUpdate: Boolean = false) {
        val language = languageFromIso2(settingsClient.getDefaultLanguage()) ?: DEFAULT_LANGUAGE
        if (!forceUpdate && language == state.currentLanguage()) return // Force update on initialization
        state.updateLanguage(language)
        asrProviderManager.updateLanguage(language)
        director.updateOptions(director.getOptions().copy(language = language))
    }

    fun onSecondaryLanguageSelected() {
        val language = languageFromIso2(settingsClient.getSecondaryLanguage()) ?: DEFAULT_SECONDARY_LANGUAGE
        if (language == state.currentLanguage()) return
        state.updateLanguage(language)
        asrProviderManager.updateLanguage(language)
        director.updateOptions(director.getOptions().copy(language = language))
    }

    fun toggleListening() {
        if (state.currentStage() == AppStage.IDLE) {
            currentPipelineJob = viewModelScope.launch { director.start() }
        } else if (state.currentStage() == AppStage.LISTENING) {
            viewModelScope.launch { director.stop() }
        }
    }

    fun cancelListening() {
        if (state.currentStage() in listOf(AppStage.LISTENING, AppStage.TRANSCRIBING, AppStage.POLISHING)) {
            currentPipelineJob?.cancel()
            viewModelScope.launch { director.cancel() }
        }
    }

    private fun onPipelineCompleted(event: DirectorEvent.PipelineCompleted) {
        logger.info("Pipeline completed: $event")
        hideAndReset()
        if (event.finalResult.isBlank()) return
        viewModelScope.launch {
            delay(POST_HIDE_DELAY_MS) // Wait for the main window to fully go away before typing
            val finalText = event.finalResult.trim() + " " // Separate multiple results with a space
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
        portalsSessionManager.shutdown()
        registry.shutdownAll()
    }
}
