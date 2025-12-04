package com.example.cloaktalk.ui.viewmodels

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for managing user sign-up operations.
 * Handles sign-up with email/password and Google authentication.
 * Maintains sign-up state for UI updates.
 *
 
 */
class SignUpViewModel : ViewModel() {
    // FirebaseAuth instance for authentication
    private val auth = FirebaseAuth.getInstance()

    // StateFlow to hold the current sign-up state
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    /**
     * Sign up a new user with email and password.
     * @param email User's email address
     * @param password User's password
     * @param confirmPassword Confirmation of user's password
     * @param fullName User's full name
     */
    fun signUpWithEmail(email: String, password: String, confirmPassword: String, fullName: String) {
        if (password != confirmPassword) {
            _signUpState.value = SignUpState.Error("Passwords don't match")
            return
        }

        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let {
                    _signUpState.value = SignUpState.Success
                } ?: run {
                    _signUpState.value = SignUpState.Error("Sign up failed")
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Sign up a new user using Google authentication.
     * @param context Android context required for credential management
     */
    fun signUpWithGoogle(context: Context) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("YOUR_WEB_CLIENT_ID") // Replace with your web client ID from Firebase
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                authResult.user?.let {
                    _signUpState.value = SignUpState.Success
                } ?: run {
                    _signUpState.value = SignUpState.Error("Google sign-in failed")
                }
            } catch (e: GetCredentialException) {
                _signUpState.value = SignUpState.Error(e.message ?: "Google sign-in cancelled")
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Reset the sign-up state to Idle.
     */
    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}

/**
 * Represents the different states of the sign-up process.
 */
sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}
