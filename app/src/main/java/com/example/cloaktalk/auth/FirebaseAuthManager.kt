
package com.example.cloaktalk.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.auth.ktx.auth

object FirebaseAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUpWithEmail(email: String, password: String, onResult: (success: Boolean, user: FirebaseUser?, message: String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, auth.currentUser, null)
                } else {
                    onResult(false, null, task.exception?.localizedMessage)
                }
            }
    }

    fun signInWithGoogle(idToken: String, onResult: (success: Boolean, user: FirebaseUser?, message: String?) -> Unit) {
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