package com.example.cloaktalk.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser

/**
 * Singleton object to manage Firebase authentication operations.
 */
object FirebaseAuthManager {
    // FirebaseAuth instance used for authentication
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Sign up a new user with email and password.
     * @param email User's email address
     * @param password User's password
     * @param onResult Callback with success status, user object, and error message
     */
    fun signUpWithEmail(
        email: String,
        password: String,
        onResult: (success: Boolean, user: FirebaseUser?, message: String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, auth.currentUser, null)
                } else {
                    onResult(false, null, task.exception?.localizedMessage)
                }
            }
    }

    /**
     * Sign in a user using Google ID token.
     * @param idToken Google ID token
     * @param onResult Callback with success status, user object, and error message
     */
    fun signInWithGoogle(
        idToken: String,
        onResult: (success: Boolean, user: FirebaseUser?, message: String?) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, auth.currentUser, null)
                } else {
                    onResult(false, null, task.exception?.localizedMessage)
                }
            }
    }
}
