package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.LauncherViewModel
import com.example.ui.ScreenState
import com.example.ui.components.GestureArea
import com.example.ui.screens.AiCoreScreen
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.MusicPlayerScreen
import com.example.ui.theme.TianDaoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val gestureSensitivity by viewModel.gestureSensitivity.collectAsState()

            TianDaoTheme(isYinMode = isDarkTheme) {
                // Back press handling for nested screens
                if (currentScreen != ScreenState.HOME) {
                    BackHandler {
                        viewModel.setScreen(ScreenState.HOME)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        label = "ScreenNavigationTransition",
                        transitionSpec = {
                            if (initialState == ScreenState.HOME && targetState == ScreenState.APP_DRAWER) {
                                // Home -> App Drawer: slide right to left
                                slideInHorizontally(animationSpec = tween(400)) { width -> width } + fadeIn(animationSpec = tween(400)) togetherWith
                                        slideOutHorizontally(animationSpec = tween(400)) { width -> -width } + fadeOut(animationSpec = tween(400))
                            } else if (initialState == ScreenState.APP_DRAWER && targetState == ScreenState.HOME) {
                                // App Drawer -> Home: slide left to right (exit)
                                slideInHorizontally(animationSpec = tween(400)) { width -> -width } + fadeIn(animationSpec = tween(400)) togetherWith
                                        slideOutHorizontally(animationSpec = tween(400)) { width -> width } + fadeOut(animationSpec = tween(400))
                            } else if (initialState == ScreenState.HOME && targetState == ScreenState.MUSIC_PLAYER) {
                                // Home -> Music Player: slide left to right (enter from left edge)
                                slideInHorizontally(animationSpec = tween(400)) { width -> -width } + fadeIn(animationSpec = tween(400)) togetherWith
                                        slideOutHorizontally(animationSpec = tween(400)) { width -> width } + fadeOut(animationSpec = tween(400))
                            } else if (initialState == ScreenState.MUSIC_PLAYER && targetState == ScreenState.HOME) {
                                // Music Player -> Home: slide right to left (exit back to left edge)
                                slideInHorizontally(animationSpec = tween(400)) { width -> width } + fadeIn(animationSpec = tween(400)) togetherWith
                                        slideOutHorizontally(animationSpec = tween(400)) { width -> -width } + fadeOut(animationSpec = tween(400))
                            } else if (targetState == ScreenState.AI_CORE) {
                                // To AI Core: slide bottom to top
                                slideInVertically(animationSpec = tween(400)) { height -> height } + fadeIn(animationSpec = tween(400)) togetherWith
                                        slideOutVertically(animationSpec = tween(400)) { height -> -height } + fadeOut(animationSpec = tween(400))
                            } else if (initialState == ScreenState.AI_CORE && targetState == ScreenState.HOME) {
                                // AI Core -> Home: slide top to bottom (exit)
                                slideInVertically(animationSpec = tween(400)) { height -> -height } + fadeIn(animationSpec = tween(400)) togetherWith
                                        slideOutVertically(animationSpec = tween(400)) { height -> height } + fadeOut(animationSpec = tween(400))
                            } else {
                                // Standard Crossfade for settings or others
                                fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(350))
                            }
                        }
                    ) { state ->
                        val screenModifier = Modifier.padding(innerPadding)
                        
                        when (state) {
                            ScreenState.HOME -> {
                                // Wrap HomeScreen in GestureArea to capture swipe up/down, left/right, double-tap, etc.
                                GestureArea(
                                    onSwipeUp = { viewModel.triggerGestureAction("swipe_up") },
                                    onSwipeDown = { viewModel.triggerGestureAction("swipe_down") },
                                    onSwipeLeft = { viewModel.triggerGestureAction("swipe_left") },
                                    onSwipeRight = { viewModel.triggerGestureAction("swipe_right") },
                                    onDoubleTap = { viewModel.triggerGestureAction("double_tap") },
                                    onLongPress = { viewModel.triggerGestureAction("long_press") },
                                    swipeThreshold = gestureSensitivity,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        modifier = screenModifier
                                    )
                                }
                            }
                            ScreenState.APP_DRAWER -> {
                                AppDrawerScreen(
                                    viewModel = viewModel,
                                    modifier = screenModifier
                                )
                            }
                            ScreenState.AI_CORE -> {
                                AiCoreScreen(
                                    viewModel = viewModel,
                                    modifier = screenModifier
                                )
                            }
                            ScreenState.SETTINGS -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    modifier = screenModifier
                                )
                            }
                            ScreenState.MUSIC_PLAYER -> {
                                MusicPlayerScreen(
                                    viewModel = viewModel,
                                    modifier = screenModifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh apps list when returning to launcher to capture newly installed/uninstalled apps
        viewModel.refreshInstalledApps()
    }
}
