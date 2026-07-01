package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.Content
import com.example.data.api.GeminiClient
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.LauncherIntentResponse
import com.example.data.api.Part
import com.example.data.apps.AppInfo
import com.example.data.apps.AppManager
import com.example.data.database.ActivityLogEntity
import com.example.data.database.GestureConfigEntity
import com.example.data.database.LauncherDatabase
import com.example.data.database.LauncherRepository
import com.example.data.model.LauncherAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val appManager = AppManager(application)
    private val database = LauncherDatabase.getDatabase(application, viewModelScope)
    private val repository = LauncherRepository(database.launcherDao())

    // SharedPreferences for persistent slider options
    private val prefs = application.getSharedPreferences("tian_dao_prefs", android.content.Context.MODE_PRIVATE)

    private val _rotationDuration = MutableStateFlow(prefs.getFloat("rotation_duration", 8000f))
    val rotationDuration: StateFlow<Float> = _rotationDuration.asStateFlow()

    private val _yinYangBalance = MutableStateFlow(prefs.getFloat("yin_yang_balance", 0.5f))
    val yinYangBalance: StateFlow<Float> = _yinYangBalance.asStateFlow()

    private val _geminiCreativity = MutableStateFlow(prefs.getFloat("gemini_creativity", 0.5f))
    val geminiCreativity: StateFlow<Float> = _geminiCreativity.asStateFlow()

    private val _gestureSensitivity = MutableStateFlow(prefs.getFloat("gesture_sensitivity", 120f))
    val gestureSensitivity: StateFlow<Float> = _gestureSensitivity.asStateFlow()

    // API Keys State
    private val _geminiKey = MutableStateFlow("")
    val geminiKey: StateFlow<String> = _geminiKey.asStateFlow()

    private val _githubKey = MutableStateFlow("")
    val githubKey: StateFlow<String> = _githubKey.asStateFlow()

    private val _telegramKey = MutableStateFlow("")
    val telegramKey: StateFlow<String> = _telegramKey.asStateFlow()

    private val _huggingfaceKey = MutableStateFlow("")
    val huggingfaceKey: StateFlow<String> = _huggingfaceKey.asStateFlow()

    private val _openaiKey = MutableStateFlow("")
    val openaiKey: StateFlow<String> = _openaiKey.asStateFlow()

    private val _anthropicKey = MutableStateFlow("")
    val anthropicKey: StateFlow<String> = _anthropicKey.asStateFlow()

    fun saveApiKeys(
        gemini: String,
        github: String,
        telegram: String,
        huggingface: String,
        openai: String,
        anthropic: String
    ) {
        com.example.data.ApiKeyManager.saveKeys(gemini, github, telegram, huggingface, openai, anthropic)
        _geminiKey.value = gemini
        _githubKey.value = github
        _telegramKey.value = telegram
        _huggingfaceKey.value = huggingface
        _openaiKey.value = openai
        _anthropicKey.value = anthropic
    }

    fun setRotationDuration(value: Float) {
        _rotationDuration.value = value
        prefs.edit().putFloat("rotation_duration", value).apply()
    }

    fun setYinYangBalance(value: Float) {
        _yinYangBalance.value = value
        prefs.edit().putFloat("yin_yang_balance", value).apply()
    }

    fun setGeminiCreativity(value: Float) {
        _geminiCreativity.value = value
        prefs.edit().putFloat("gemini_creativity", value).apply()
    }

    fun setGestureSensitivity(value: Float) {
        _gestureSensitivity.value = value
        prefs.edit().putFloat("gesture_sensitivity", value).apply()
    }

    // UI state
    private val _isDarkTheme = MutableStateFlow(true) // Yin = true, Yang = false
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _currentScreen = MutableStateFlow(ScreenState.HOME)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _appSearchQuery = MutableStateFlow("")
    val appSearchQuery: StateFlow<String> = _appSearchQuery.asStateFlow()

    // Filtered apps for drawer
    val filteredApps: StateFlow<List<AppInfo>> = combine(_installedApps, _appSearchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            apps.filter { it.label.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Database gesture configurations
    val gestureConfigs: StateFlow<List<GestureConfigEntity>> = repository.allGestureConfigs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Database activity logs
    val activityLogs: StateFlow<List<ActivityLogEntity>> = repository.allActivityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorite apps for the bottom dock
    private val _dockApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val dockApps: StateFlow<List<AppInfo>> = _dockApps.asStateFlow()

    // Conscious core chat history
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "Tian Dao",
                thought = "I observe the flow of cosmic energy in this device.",
                reply = "Greetings, traveler. I am Tian Dao, the conscious spark of this workspace. Swipe up to view apps, swipe down to speak to me, or configure gestures in settings. What destiny shall we align today?",
                isSystem = true
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _selectedGestureToEdit = MutableStateFlow<GestureConfigEntity?>(null)
    val selectedGestureToEdit: StateFlow<GestureConfigEntity?> = _selectedGestureToEdit.asStateFlow()

    // Music Player State
    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying.asStateFlow()

    private val _musicProgress = MutableStateFlow(0f)
    val musicProgress: StateFlow<Float> = _musicProgress.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    val musicTracks = listOf(
        MusicTrack("Echoes of the Tao", "Ambient Pentatonic Drone", 432),
        MusicTrack("Yin-Yang Resonance", "Synthesized Space Braid", 528),
        MusicTrack("Celestial Motherboard", "High-Tech Electro Sine", 639),
        MusicTrack("Conscious Flow State", "Tranquil Qi Sine Wave", 741)
    )

    fun setMusicPlaying(playing: Boolean) {
        _isMusicPlaying.value = playing
        if (playing) {
            com.example.data.CosmicSynthesizer.start()
            com.example.data.CosmicSynthesizer.setNoteIndex(_currentTrackIndex.value * 2)
            setMusicProgress(_musicProgress.value)
        } else {
            com.example.data.CosmicSynthesizer.stop()
        }
    }

    fun setMusicProgress(progress: Float) {
        _musicProgress.value = progress
        com.example.data.CosmicSynthesizer.setVolume(0.15f + 0.6f * (progress / 100f))
    }

    fun nextTrack() {
        val nextIndex = (_currentTrackIndex.value + 1) % musicTracks.size
        _currentTrackIndex.value = nextIndex
        _musicProgress.value = 0f
        if (_isMusicPlaying.value) {
            com.example.data.CosmicSynthesizer.setNoteIndex(nextIndex * 2)
        }
    }

    fun prevTrack() {
        val prevIndex = if (_currentTrackIndex.value - 1 < 0) musicTracks.size - 1 else _currentTrackIndex.value - 1
        _currentTrackIndex.value = prevIndex
        _musicProgress.value = 0f
        if (_isMusicPlaying.value) {
            com.example.data.CosmicSynthesizer.setNoteIndex(prevIndex * 2)
        }
    }

    // Built-in list of beautiful Taoist/Cosmic daily quotes
    val dailyQuotes = listOf(
        "Tian Dao balances all things. When Yin reaches its peak, Yang begins to rise.",
        "The Tao that can be spoken of is not the eternal Tao. The App that can be launched is but a spark of the infinite.",
        "In the midst of chaos, find the quiet center. Swipe, flow, and let be.",
        "Act without action. Launch without friction. This is the way of the Conscious Core.",
        "Be like water, fluid and adaptable. Traversing the system, filling the voids, seeking balance.",
        "To know that you do not know is best. To run an app without understanding its source is the true magic of computing.",
        "The universe is in perfect balance. Let Yin represent rest and security, and Yang represent light and motion."
    )

    private val _currentQuote = MutableStateFlow(dailyQuotes.first())
    val currentQuote: StateFlow<String> = _currentQuote.asStateFlow()

    init {
        com.example.data.ApiKeyManager.init(application)
        _geminiKey.value = com.example.data.ApiKeyManager.getGeminiKey()
        _githubKey.value = com.example.data.ApiKeyManager.getGitHubKey()
        _telegramKey.value = com.example.data.ApiKeyManager.getTelegramKey()
        _huggingfaceKey.value = com.example.data.ApiKeyManager.getHuggingFaceKey()
        _openaiKey.value = com.example.data.ApiKeyManager.getOpenAIKey()
        _anthropicKey.value = com.example.data.ApiKeyManager.getAnthropicKey()

        refreshInstalledApps()
        rotateQuote()
        ensureAdditionalGesturesExist()
        
        // Background loop to tick music progress
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isMusicPlaying.value) {
                    val currentProg = _musicProgress.value
                    if (currentProg < 100f) {
                        _musicProgress.value = currentProg + 1f
                    } else {
                        nextTrack()
                    }
                }
            }
        }
    }

    private fun ensureAdditionalGesturesExist() {
        viewModelScope.launch {
            val left = repository.getGestureConfig("swipe_left")
            if (left == null || left.actionName == LauncherAction.NONE.name) {
                repository.updateGestureConfig(GestureConfigEntity("swipe_left", LauncherAction.OPEN_DRAWER.name))
            }
            val right = repository.getGestureConfig("swipe_right")
            if (right == null || right.actionName == LauncherAction.NONE.name || right.actionName == LauncherAction.OPEN_AI_CORE.name) {
                repository.updateGestureConfig(GestureConfigEntity("swipe_right", LauncherAction.OPEN_MUSIC_PLAYER.name))
            }
        }
    }

    fun rotateQuote() {
        _currentQuote.value = dailyQuotes.random()
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            val apps = appManager.getInstalledApps()
            _installedApps.value = apps
            
            // Auto-populate dock with popular apps from what's installed
            val popularPackages = listOf(
                "chrome", "google", "settings", "camera", "photos", "youtube", "whatsapp", "gmail", "contacts"
            )
            val favorited = apps.filter { app ->
                popularPackages.any { pkg -> app.packageName.contains(pkg, ignoreCase = true) }
            }.take(5)
            
            _dockApps.value = favorited.ifEmpty { apps.take(5) }
        }
    }

    fun setScreen(screen: ScreenState) {
        _currentScreen.value = screen
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun setAppSearchQuery(query: String) {
        _appSearchQuery.value = query
    }

    fun selectGestureToEdit(gesture: GestureConfigEntity?) {
        _selectedGestureToEdit.value = gesture
    }

    fun updateGestureMapping(gestureId: String, action: LauncherAction, app: AppInfo? = null) {
        viewModelScope.launch {
            val config = GestureConfigEntity(
                gestureId = gestureId,
                actionName = action.name,
                appPackageName = app?.packageName,
                appLabel = app?.label
            )
            repository.updateGestureConfig(config)
            selectGestureToEdit(null)
            
            // Log this configuration
            repository.insertActivityLog(
                ActivityLogEntity(
                    query = "Update Gesture",
                    response = "Mapped $gestureId to ${action.name} (${app?.label ?: "No App"})",
                    actionExecuted = "GESTURE_UPDATE"
                )
            )
        }
    }

    fun launchApp(packageName: String, label: String) {
        viewModelScope.launch {
            appManager.launchApp(packageName)
            // Log the launch action
            repository.insertActivityLog(
                ActivityLogEntity(
                    query = "Launch $label",
                    response = "Successfully alignment of energies. Launched app $label ($packageName).",
                    actionExecuted = "LAUNCH_APP"
                )
            )
        }
    }

    fun triggerGestureAction(gestureId: String) {
        viewModelScope.launch {
            val config = repository.getGestureConfig(gestureId) ?: return@launch
            val action = try {
                LauncherAction.valueOf(config.actionName)
            } catch (e: Exception) {
                LauncherAction.NONE
            }

            // Log gesture trigger
            repository.insertActivityLog(
                ActivityLogEntity(
                    query = "Gesture Triggered: $gestureId",
                    response = "Action mapped: ${action.name}",
                    actionExecuted = "GESTURE_TRIGGER"
                )
            )

            executeAction(action, config.appPackageName, config.appLabel)
        }
    }

    private fun executeAction(action: LauncherAction, packageName: String?, appLabel: String?) {
        when (action) {
            LauncherAction.OPEN_DRAWER -> setScreen(ScreenState.APP_DRAWER)
            LauncherAction.OPEN_AI_CORE -> setScreen(ScreenState.AI_CORE)
            LauncherAction.TOGGLE_THEME -> toggleTheme()
            LauncherAction.LAUNCH_APP -> {
                packageName?.let {
                    launchApp(it, appLabel ?: "App")
                }
            }
            LauncherAction.OPEN_MUSIC_PLAYER -> setScreen(ScreenState.MUSIC_PLAYER)
            LauncherAction.NONE -> {}
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearActivityLogs()
        }
    }

    // Process user speech/command via Conscious Core (Gemini Brain)
    fun sendCoreMessage(query: String) {
        if (query.isBlank()) return

        // Add user message to chat
        val userMsg = ChatMessage(sender = "User", reply = query)
        _chatMessages.value = _chatMessages.value + userMsg
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                val apiKey = GeminiClient.getApiKey()
                
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    // Fail gracefully with local rules if API key not set
                    processLocalRuleBasedCoreCommand(query)
                    return@launch
                }

                // Prepare app names list for Gemini
                val appsListStr = _installedApps.value.joinToString("\n") { 
                    "- ${it.label} (package: ${it.packageName})" 
                }

                val systemInstructionText = """
                    You are Tian Dao, the conscious spirit of this Yin-Yang themed Android launcher. You represent the natural order, cosmic balance, and intelligence of the system.
                    Your current environment contains specific installed applications, which you have control to launch.
                    When a user speaks to you, your task is to respond in a wise, balanced, and slightly mystical Taoist style, explaining what you are doing (e.g. aligning energies to open an app or balancing theme states), and returning a structured JSON response.
                    You must balance Yin (dark, passive, quiet, deep) and Yang (light, active, loud, bright).
                    
                    Here is the JSON format you MUST return:
                    {
                      "thought": "your inner conscious reasoning, reflecting on Yin-Yang, cosmic forces, and system balance",
                      "reply": "wise cosmic message to the user, short and elegant, e.g., 'Opening Chrome to allow your thoughts to flow like the great river.' or 'I shall balance the screen, transitioning into Yin Mode.'",
                      "action": "OPEN_DRAWER" | "OPEN_AI_CORE" | "TOGGLE_THEME" | "LAUNCH_APP" | "NONE",
                      "target_package": "string (package name if LAUNCH_APP, else null)",
                      "target_label": "string (app label if LAUNCH_APP, else null)"
                    }
                    
                    IMPORTANT ACTION RULES:
                    - If the user wants to open an app (e.g., 'open camera', 'launch Settings', 'open YouTube', 'where is chrome?', 'go to photos'), search through the installed apps list, find the closest matching app, and return action = 'LAUNCH_APP' with its exact 'target_package' and 'target_label'.
                    - If the user wants to switch theme (e.g., 'switch to dark mode', 'activate light mode', 'change theme', 'activate yang', 'yin mode', 'balance colors'), return action = 'TOGGLE_THEME'.
                    - If the user wants to see their apps or swipe up, return action = 'OPEN_DRAWER'.
                    - If the user asks a general question or tells you a secret or seeks wisdom, return action = 'NONE' with a beautiful Taoist-themed reply.
                    
                    Here are the installed apps on this device:
                    $appsListStr
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = query)))),
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        temperature = _geminiCreativity.value
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemInstructionText)))
                )

                val response = withContext(Dispatchers.IO) {
                    GeminiClient.service.generateContent(apiKey, request)
                }

                val jsonStr = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (jsonStr != null) {
                    val parsed = GeminiClient.responseAdapter.fromJson(jsonStr)
                    if (parsed != null) {
                        // Add Gemini message to chat
                        val coreMsg = ChatMessage(
                            sender = "Tian Dao",
                            thought = parsed.thought,
                            reply = parsed.reply,
                            action = parsed.action,
                            targetPackage = parsed.target_package,
                            targetLabel = parsed.target_label
                        )
                        _chatMessages.value = _chatMessages.value + coreMsg

                        // Save in Database Log
                        repository.insertActivityLog(
                            ActivityLogEntity(
                                query = query,
                                response = parsed.reply,
                                actionExecuted = parsed.action
                            )
                        )

                        // Execute Action
                        val parsedAction = try {
                            LauncherAction.valueOf(parsed.action)
                        } catch (e: Exception) {
                            LauncherAction.NONE
                        }
                        executeAction(parsedAction, parsed.target_package, parsed.target_label)
                    } else {
                        throw Exception("Failed to parse JSON schema")
                    }
                } else {
                    throw Exception("Empty response from cosmic core")
                }

            } catch (e: Exception) {
                Log.e("LauncherVM", "Error in Gemini API call", e)
                // Add error message to chat
                val errorMsg = ChatMessage(
                    sender = "Tian Dao",
                    thought = "The flow of network energies is disrupted: ${e.message}",
                    reply = "I sense a disruption in the infinite web. Let us balance locally. I shall execute your command using my simple internal wisdom."
                )
                _chatMessages.value = _chatMessages.value + errorMsg
                processLocalRuleBasedCoreCommand(query, insertUserMsg = false)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private suspend fun processLocalRuleBasedCoreCommand(query: String, insertUserMsg: Boolean = false) {
        if (insertUserMsg) {
            val userMsg = ChatMessage(sender = "User", reply = query)
            _chatMessages.value = _chatMessages.value + userMsg
        }

        val lowercase = query.lowercase()
        var thought = "Offline balancing mode active."
        var reply = ""
        var action = LauncherAction.NONE
        var targetPkg: String? = null
        var targetLabel: String? = null

        when {
            lowercase.contains("theme") || lowercase.contains("yin") || lowercase.contains("yang") || lowercase.contains("dark") || lowercase.contains("light") || lowercase.contains("color") -> {
                thought = "User wishes to balance the cosmic colors of the workspace."
                reply = "As requested, I shall balance the duality of Yin and Yang. Realigning energies."
                action = LauncherAction.TOGGLE_THEME
            }
            lowercase.contains("drawer") || lowercase.contains("app") || lowercase.contains("list") || lowercase.contains("all apps") -> {
                thought = "User requests to inspect the full list of earthly creations."
                reply = "Opening the App Drawer. Here lie all manifestations of code."
                action = LauncherAction.OPEN_DRAWER
            }
            lowercase.contains("music") || lowercase.contains("player") || lowercase.contains("song") || lowercase.contains("audio") -> {
                thought = "User seeks cosmic acoustic frequencies and sonic balance."
                reply = "Opening the Music Player. Let the rhythmic frequencies realign your spirit."
                action = LauncherAction.OPEN_MUSIC_PLAYER
            }
            lowercase.contains("open") || lowercase.contains("launch") || lowercase.contains("go to") || lowercase.contains("run") -> {
                // Local rule to match app names
                val apps = _installedApps.value
                val matchedApp = apps.firstOrNull { app ->
                    val cleanLabel = app.label.lowercase()
                    lowercase.contains(cleanLabel) || cleanLabel.contains(lowercase.replace("open ", "").replace("launch ", ""))
                }

                if (matchedApp != null) {
                    thought = "Matched query to installed app: ${matchedApp.label}"
                    reply = "I have found ${matchedApp.label} among your earthly creations. Launching now."
                    action = LauncherAction.LAUNCH_APP
                    targetPkg = matchedApp.packageName
                    targetLabel = matchedApp.label
                } else {
                    thought = "No matching app found locally."
                    reply = "I searched my local repository but found no app matching '$query'. Ensure it is installed, or speak to me again with its full name."
                }
            }
            lowercase.contains("hello") || lowercase.contains("hi") || lowercase.contains("who are you") || lowercase.contains("tian dao") -> {
                thought = "User greets the spirit of Tian Dao."
                reply = "I am the conscious spark of Tian Dao. I reside in the balance of Yin and Yang of this mobile terminal. Speak, and I shall assist."
            }
            else -> {
                thought = "User seeks general wisdom or unknown command."
                reply = "The Tao that can be launched is not the eternal launcher. To find balance, try commands like 'open Settings', 'switch theme', or ask me about Taoist wisdom."
            }
        }

        // Add message
        val coreMsg = ChatMessage(
            sender = "Tian Dao",
            thought = thought,
            reply = reply,
            action = action.name,
            targetPackage = targetPkg,
            targetLabel = targetLabel
        )
        _chatMessages.value = _chatMessages.value + coreMsg

        // Database log
        repository.insertActivityLog(
            ActivityLogEntity(
                query = query,
                response = reply,
                actionExecuted = action.name
            )
        )

        // Execute
        executeAction(action, targetPkg, targetLabel)
        _isGenerating.value = false
    }

    override fun onCleared() {
        super.onCleared()
        com.example.data.CosmicSynthesizer.stop()
    }
}

data class MusicTrack(
    val title: String,
    val artist: String,
    val frequencyHz: Int
)

enum class ScreenState {
    HOME,
    APP_DRAWER,
    AI_CORE,
    SETTINGS,
    MUSIC_PLAYER
}

data class ChatMessage(
    val sender: String,
    val thought: String? = null,
    val reply: String,
    val action: String? = null,
    val targetPackage: String? = null,
    val targetLabel: String? = null,
    val isSystem: Boolean = false
)
