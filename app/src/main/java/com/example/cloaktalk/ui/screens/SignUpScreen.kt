// kotlin
package com.example.cloaktalk.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import com.example.cloaktalk.ui.theme.OrangeTheme
import com.example.cloaktalk.auth.FirebaseAuthManager
import com.example.cloaktalk.R
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignUp: () -> Unit, onBackToLogin: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val scope = rememberCoroutineScope()

    fun tryEmailSignUp() {
        when {
            fullName.isBlank() -> Toast.makeText(context, "Enter your full name", Toast.LENGTH_SHORT).show()
            email.isBlank() -> Toast.makeText(context, "Enter your email", Toast.LENGTH_SHORT).show()
            password.length < 6 -> Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            password != confirmPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            else -> {
                FirebaseAuthManager.signUpWithEmail(email.trim(), password) { success, _, message ->
                    if (success) {
                        Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
                        onSignUp()
                    } else {
                        Toast.makeText(context, message ?: "Sign up failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF78350F)))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(72.dp).background(Brush.horizontalGradient(listOf(OrangeTheme.Primary, OrangeTheme.PrimaryDark)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                }

                Spacer(Modifier.height(12.dp))

                Text("Create Account", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = OrangeTheme.TextPrimary)
                Text("Join CloakTalk today", fontSize = 14.sp, color = OrangeTheme.TextSecondary)

                Spacer(Modifier.height(18.dp))

                GoogleSignButton(
                    onClick = {
                        scope.launch {
                            try {
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setAutoSelectEnabled(false)
                                    .setNonce(null)
                                    .build()
                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()
                                val result = credentialManager.getCredential(request = request, context = context)
                                val credential = result.credential
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val idToken = googleIdTokenCredential.idToken
                                FirebaseAuthManager.signInWithGoogle(idToken) { success, _, message ->
                                    if (success) {
                                        Toast.makeText(context, "Signed up with Google", Toast.LENGTH_SHORT).show()
                                        onSignUp()
                                    } else {
                                        Toast.makeText(context, message ?: "Firebase Auth Failed", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: GetCredentialException) {
                                Log.e("SignUpScreen", "Google Sign-In failed: ${e.message}")
                                if (!e.message.toString().contains("cancelled")) {
                                    Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("SignUpScreen", "Unexpected error", e)
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Or divider and fields
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f).height(1.dp), color = OrangeTheme.Border)
                    Text("  OR  ", color = OrangeTheme.TextSecondary)
                    Divider(modifier = Modifier.weight(1f).height(1.dp), color = OrangeTheme.Border)
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                Spacer(Modifier.height(16.dp))

                Button(onClick = { tryEmailSignUp() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = OrangeTheme.Primary), shape = RoundedCornerShape(12.dp)) {
                    Text("Create Account", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                Row {
                    Text("Already have an account?", color = OrangeTheme.TextSecondary)
                    Text(" Login", color = OrangeTheme.Primary, modifier = Modifier.clickable { onBackToLogin() })
                }
            }
        }
    }
}