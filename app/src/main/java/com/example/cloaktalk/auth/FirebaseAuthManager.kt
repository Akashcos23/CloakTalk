// kotlin
 package com.example.cloaktalk.auth

 import com.google.firebase.auth.FirebaseAuth
 import com.google.firebase.auth.GoogleAuthProvider
 import com.google.firebase.auth.FirebaseUser

 /**
  * Singleton object to manage Firebase authentication operations.
  */
 object FirebaseAuthManager {
     private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

     fun signInWithGoogle(
         idToken: String?,
         onResult: (success: Boolean, user: FirebaseUser?, message: String?) -> Unit
     ) {
         if (idToken == null) {
             onResult(false, null, "Missing Google ID token")
             return
         }
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

     fun signOut() {
         auth.signOut()
     }
 }