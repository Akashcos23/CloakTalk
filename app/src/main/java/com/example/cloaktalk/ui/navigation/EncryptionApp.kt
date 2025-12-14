package com.example.cloaktalk.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.cloaktalk.data.local.database.CloakTalkDatabase
import com.example.cloaktalk.data.repository.DesignAlgorithmRepository
import com.example.cloaktalk.ui.screens.*

/**
 * Main composable function for CloakTalk app navigation.
 * Handles switching between different screens based on the current navigation state.
 *
 * This composable manages:
 * - Navigation state between all app screens
 * - User authentication state (logged-in user ID)
 * - Database and repository instances for data operations
 *
 * Screens supported:
 * - Login: User authentication screen
 * - Sign Up: New user registration screen
 * - Home: Main dashboard screen
 * - History: View past encryption/decryption operations
 * - Designer: Create custom encryption algorithms
 * - Notifications: View app notifications
 * - Settings: App configuration options
 * - Encrypt: Encrypt messages using algorithms
 * - Decrypt: Decrypt messages using algorithms
 */
@Composable
fun EncryptionApp() {
    // ==================== State Management ====================

    /**
     * Holds the current screen state.
     * Determines which screen composable to render.
     * Default: "login" - starts at login screen
     */
    var currentScreen by remember { mutableStateOf("login") }

    /**
     * Stores the ID of the currently logged-in user.
     * Set after successful login or signup.
     * Used for user-specific database operations.
     * Null when no user is logged in.
     */
    var loggedInUserId by remember { mutableStateOf<Long?>(null) }

    // ==================== Database & Repository Setup ====================

    /**
     * Get the current Android context for database initialization.
     */
    val context = LocalContext.current

    /**
     * Singleton database instance for the CloakTalk application.
     * Contains all DAOs for data access.
     * Remembered to prevent recreation on recomposition.
     */
    val database = remember { CloakTalkDatabase.getInstance(context) }

    /**
     * Repository for design algorithm operations.
     * Provides methods for creating, reading, updating, and deleting
     * user-created custom encryption algorithms.
     */
    val designAlgorithmRepository = remember {
        DesignAlgorithmRepository(database.designAlgorithmDao())
    }

    // ==================== Screen Navigation ====================

    /**
     * Switch between screens based on currentScreen value.
     * Each screen receives appropriate callbacks and dependencies.
     */
    when (currentScreen) {
        /**
         * Login Screen
         * - Authenticates existing users
         * - On successful login: stores user ID and navigates to home
         * - Provides option to navigate to signup
         */
        "login" -> LoginScreen(
            onLogin = { userId ->
                loggedInUserId = userId
                currentScreen = "home"
            },
            onSignUp = { currentScreen = "signup" }
        )

        /**
         * Sign Up Screen
         * - Registers new users
         * - On successful signup: stores user ID and navigates to home
         * - Provides option to return to login
         */
        "signup" -> SignUpScreen(
            onSignUp = { userId ->
                loggedInUserId = userId
                currentScreen = "home"
            },
            onBackToLogin = { currentScreen = "login" }
        )

        /**
         * Home Screen
         * - Main dashboard with navigation options
         * - Entry point to all other features
         * - Requires user ID for user-specific stats and activity
         */
        "home" -> HomeScreen(
            onNavigate = { currentScreen = it },
            userId = loggedInUserId ?: 0L
        )

        /**
         * History Screen
         * - Displays past encryption/decryption operations
         * - Requires user ID to filter history by current user
         */
        "history" -> HistoryScreen(
            onNavigate = { currentScreen = it },
            userId = loggedInUserId ?: 0L
        )

        /**
         * Designer Screen
         * - Allows users to create custom encryption algorithms
         * - Requires repository for saving algorithms
         * - Requires user ID for ownership tracking
         */
        "designer" -> DesignerScreen(
            onNavigate = { currentScreen = it },
            designAlgorithmRepository = designAlgorithmRepository,
            userId = loggedInUserId ?: 0L
        )

        /**
         * Notifications Screen
         * - Displays app notifications and alerts
         */
        "notifications" -> NotificationsScreen(onNavigate = { currentScreen = it })

        /**
         * Settings Screen
         * - App configuration and user preferences
         * - Requires user ID for user-specific settings
         */
        "settings" -> SettingsScreen(
            onNavigate = { currentScreen = it },
            userId = loggedInUserId ?: 0L
        )

        /**
         * Encrypt Screen
         * - Encrypt messages using available algorithms
         * - Requires repository to access user's custom algorithms
         * - Requires user ID for filtering algorithms
         */
        "encrypt" -> EncryptScreen(
            onNavigate = { currentScreen = it },
            designAlgorithmRepository = designAlgorithmRepository,
            userId = loggedInUserId ?: 0L
        )

        /**
         * Decrypt Screen
         * - Decrypt messages using available algorithms
         * - Requires repository to access user's custom algorithms
         * - Requires user ID for filtering algorithms
         */
        "decrypt" -> DecryptScreen(
            onNavigate = { currentScreen = it },
            designAlgorithmRepository = designAlgorithmRepository,
            userId = loggedInUserId ?: 0L
        )

        /**
         * Default fallback
         * - Navigates to home screen for unknown routes
         */
        else -> HomeScreen(
            onNavigate = { currentScreen = it },
            userId = loggedInUserId ?: 0L
        )
    }
}
