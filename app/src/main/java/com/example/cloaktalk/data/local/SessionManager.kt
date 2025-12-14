package com.example.cloaktalk.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user session persistence using SharedPreferences.
 * Handles storing and retrieving the logged-in user ID to maintain
 * login state across app restarts.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "cloaktalk_session"
        private const val KEY_USER_ID = "logged_in_user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        @Volatile
        private var instance: SessionManager? = null

        /**
         * Get singleton instance of SessionManager.
         */
        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    /**
     * Save the logged-in user's ID.
     * Call this after successful login or signup.
     *
     * @param userId The ID of the logged-in user
     */
    fun saveUserSession(userId: Long) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Get the logged-in user's ID.
     *
     * @return The user ID if logged in, null otherwise
     */
    fun getLoggedInUserId(): Long? {
        return if (isLoggedIn()) {
            val userId = prefs.getLong(KEY_USER_ID, -1L)
            if (userId != -1L) userId else null
        } else {
            null
        }
    }

    /**
     * Check if a user is currently logged in.
     *
     * @return true if logged in, false otherwise
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Clear the user session (logout).
     * Call this when user explicitly logs out.
     */
    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_USER_ID)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }
}
