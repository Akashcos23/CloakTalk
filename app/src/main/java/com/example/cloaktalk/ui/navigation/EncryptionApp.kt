package com.example.cloaktalk.ui.navigation


import androidx.compose.runtime.*
import com.example.cloaktalk.ui.screens.*


/**
 * Main composable function for CloakTalk app navigation.
 * Handles switching between different screens based on the current navigation state.
 *
 * Screens supported:
 * - Login
 * - Sign Up
 * - Home
 * - History
 * - Designer
 * - Notifications
 * - Settings
 *
 
 */
@Composable
fun EncryptionApp() {
    // Holds the current screen state
    var currentScreen by remember { mutableStateOf("login") }

    // Switch between screens based on currentScreen value
    when (currentScreen) {
        "login" -> LoginScreen(
            onLogin = { currentScreen = "home" },
            onSignUp = { currentScreen = "signup" }
        )
        "signup" -> SignUpScreen(
            onSignUp = { currentScreen = "home" },
            onBackToLogin = { currentScreen = "login" }
        )
        "home" -> HomeScreen(onNavigate = { currentScreen = it })
        "history" -> HistoryScreen(onNavigate = { currentScreen = it })
        "designer" -> DesignerScreen(onNavigate = { currentScreen = it })
        "notifications" -> NotificationsScreen(onNavigate = { currentScreen = it })
        "settings" -> SettingsScreen(onNavigate = { currentScreen = it })
        "encrypt" -> EncryptScreen(onNavigate = { currentScreen = it })
        "decrypt" -> DecryptScreen(onNavigate = { currentScreen = it })

        else -> HomeScreen(onNavigate = { currentScreen = it })
    }
}