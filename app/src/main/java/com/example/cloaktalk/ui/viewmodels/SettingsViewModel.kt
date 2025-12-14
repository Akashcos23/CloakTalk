package com.example.cloaktalk.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cloaktalk.data.local.entity.KeyEntity
import com.example.cloaktalk.data.local.entity.UserEntity
import com.example.cloaktalk.data.repository.KeyRepository
import com.example.cloaktalk.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Data class representing the state of password change operation.
 */
data class PasswordChangeState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Data class representing a key item for display.
 */
data class KeyDisplayItem(
    val keyId: Long,
    val key: String,
    val algorithmName: String,
    val isActive: Boolean,
    val expiresAt: Long,
    val createdAt: Long
)

/**
 * ViewModel for managing settings screen state and operations.
 * Handles user data loading, password change, and key management functionality.
 */
class SettingsViewModel(
    private val userRepository: UserRepository,
    private val keyRepository: KeyRepository,
    private val userId: Long
) : ViewModel() {

    private val _userEmail = MutableStateFlow<String>("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _passwordChangeState = MutableStateFlow(PasswordChangeState())
    val passwordChangeState: StateFlow<PasswordChangeState> = _passwordChangeState.asStateFlow()

    private val _userKeys = MutableStateFlow<List<KeyDisplayItem>>(emptyList())
    val userKeys: StateFlow<List<KeyDisplayItem>> = _userKeys.asStateFlow()

    private val _isLoadingKeys = MutableStateFlow(false)
    val isLoadingKeys: StateFlow<Boolean> = _isLoadingKeys.asStateFlow()

    private var currentUser: UserEntity? = null

    init {
        loadUserData()
        loadUserKeys()
    }

    /**
     * Loads the current user's data from the database.
     */
    private fun loadUserData() {
        viewModelScope.launch {
            try {
                // We need to get user by ID, so we'll search through all users
                val users = userRepository.getAllUsers()
                users.collect { userList ->
                    currentUser = userList.find { it.id == userId }
                    _userEmail.value = currentUser?.email ?: ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Loads all keys belonging to the current user.
     */
    fun loadUserKeys() {
        viewModelScope.launch {
            _isLoadingKeys.value = true
            try {
                // Deactivate expired keys first
                keyRepository.deactivateExpiredKeys()
                
                // Get all keys for the user
                val keys = keyRepository.getKeysByUserId(userId)
                
                _userKeys.value = keys.map { key ->
                    KeyDisplayItem(
                        keyId = key.keyId,
                        key = key.key,
                        algorithmName = key.baseAlgoName,
                        isActive = key.isActive && key.keyExpire > System.currentTimeMillis(),
                        expiresAt = key.keyExpire,
                        createdAt = key.createdAt
                    )
                }.sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingKeys.value = false
            }
        }
    }

    /**
     * Deactivates a key, making it unusable for decryption.
     * 
     * @param keyId The ID of the key to deactivate
     */
    fun deactivateKey(keyId: Long) {
        viewModelScope.launch {
            try {
                keyRepository.deactivateKey(keyId)
                // Reload keys to reflect the change
                loadUserKeys()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Changes the user's password after validating the current password.
     * 
     * @param currentPassword The user's current password for validation
     * @param newPassword The new password to set
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _passwordChangeState.value = PasswordChangeState(isLoading = true)

            try {
                val user = currentUser
                if (user == null) {
                    _passwordChangeState.value = PasswordChangeState(
                        isLoading = false,
                        errorMessage = "User not found"
                    )
                    return@launch
                }

                // Verify current password
                if (user.password != currentPassword) {
                    _passwordChangeState.value = PasswordChangeState(
                        isLoading = false,
                        errorMessage = "Current password is incorrect"
                    )
                    return@launch
                }

                // Validate new password
                if (newPassword.isBlank()) {
                    _passwordChangeState.value = PasswordChangeState(
                        isLoading = false,
                        errorMessage = "New password cannot be empty"
                    )
                    return@launch
                }

                if (newPassword.length < 6) {
                    _passwordChangeState.value = PasswordChangeState(
                        isLoading = false,
                        errorMessage = "New password must be at least 6 characters"
                    )
                    return@launch
                }

                // Update the password
                val updatedUser = user.copy(password = newPassword)
                userRepository.updateUser(updatedUser)
                currentUser = updatedUser

                _passwordChangeState.value = PasswordChangeState(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _passwordChangeState.value = PasswordChangeState(
                    isLoading = false,
                    errorMessage = "Failed to change password: ${e.message}"
                )
            }
        }
    }

    /**
     * Resets the password change state after handling the result.
     */
    fun resetPasswordChangeState() {
        _passwordChangeState.value = PasswordChangeState()
    }
}

/**
 * Factory for creating SettingsViewModel instances.
 */
class SettingsViewModelFactory(
    private val userRepository: UserRepository,
    private val keyRepository: KeyRepository,
    private val userId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(userRepository, keyRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
