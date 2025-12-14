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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.cloaktalk.R
import com.example.cloaktalk.auth.FirebaseAuthManager
import com.example.cloaktalk.data.local.database.CloakTalkDatabase
import com.example.cloaktalk.data.repository.UserRepository
import com.example.cloaktalk.ui.theme.OrangeTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignUp: () -> Unit, onBackToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isGoogleSignedUp by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("") }

    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val scope = rememberCoroutineScope()

    val database = remember { CloakTalkDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(database.userDao()) }

    val auth = remember { FirebaseAuth.getInstance() }

    // White text field colors
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledTextColor = Color.White.copy(alpha = 0.5f),
        focusedBorderColor = OrangeTheme.Primary,
        unfocusedBorderColor = OrangeTheme.Border,
        disabledBorderColor = OrangeTheme.Border.copy(alpha = 0.5f),
        focusedLabelColor = OrangeTheme.Primary,
        unfocusedLabelColor = OrangeTheme.TextSecondary,
        disabledLabelColor = OrangeTheme.TextSecondary.copy(alpha = 0.5f),
        cursorColor = OrangeTheme.Primary
    )

    fun tryCompleteSignUp() {
        when {
            !isGoogleSignedUp -> {
                Toast.makeText(context, "Please sign up with Google first", Toast.LENGTH_SHORT).show()
            }
            username.isBlank() -> {
                Toast.makeText(context, "Enter your username", Toast.LENGTH_SHORT).show()
            }
            password.length < 6 -> {
                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
            password != confirmPassword -> {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else -> {
                scope.launch {
                    val result = userRepository.registerUser(
                        email = googleEmail,
                        password = password,
                        username = username
                    )

                    if (result > 0) {
                        Toast.makeText(context, "Account created successfully! Please login.", Toast.LENGTH_SHORT).show()
                        // Navigate back to login instead of logging in
                        onBackToLogin()
                    } else {
                        Toast.makeText(context, "This email is already registered", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF78350F))
                )
            ),
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(OrangeTheme.Primary, OrangeTheme.PrimaryDark)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Create Account",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary
                )
                Text(
                    text = "Join CloakTalk today",
                    fontSize = 14.sp,
                    color = OrangeTheme.TextSecondary
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Step 1: Sign up with Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = OrangeTheme.TextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(8.dp))

                if (isGoogleSignedUp) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Signed up",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Google account linked",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = googleEmail,
                                    fontSize = 12.sp,
                                    color = OrangeTheme.TextSecondary
                                )
                            }
                        }
                    }
                } else {
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

                                    val result = credentialManager.getCredential(
                                        request = request,
                                        context = context
                                    )

                                    val credential = result.credential
                                    val googleIdTokenCredential = GoogleIdTokenCredential
                                        .createFrom(credential.data)
                                    val idToken = googleIdTokenCredential.idToken

                                    val email = googleIdTokenCredential.id

                                    val existingUser = userRepository.getUserByEmail(email)
                                    if (existingUser != null) {
                                        Toast.makeText(
                                            context,
                                            "This Gmail is already used. Please login instead.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@launch
                                    }

                                    FirebaseAuthManager.signInWithGoogle(idToken) { success, _, message ->
                                        if (success) {
                                            googleEmail = email
                                            isGoogleSignedUp = true

                                            Log.d("SignUpScreen", "=== GOOGLE SIGNUP INFO ===")
                                            Log.d("SignUpScreen", "Email: $email")
                                            Log.d("SignUpScreen", "UID: ${auth.currentUser?.uid}")
                                            Log.d("SignUpScreen", "==========================")

                                            Toast.makeText(
                                                context,
                                                "Google account linked! Now complete your profile.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                message ?: "Google sign-up failed",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } catch (e: GetCredentialException) {
                                    Log.e("SignUpScreen", "Google Sign-In failed: ${e.message}")
                                    if (!e.message.toString().contains("cancelled")) {
                                        Toast.makeText(
                                            context,
                                            "Sign up failed: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e("SignUpScreen", "Unexpected error", e)
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Step 2: Complete your profile",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isGoogleSignedUp) OrangeTheme.TextPrimary else OrangeTheme.TextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    enabled = isGoogleSignedUp,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    enabled = isGoogleSignedUp,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            enabled = isGoogleSignedUp
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    enabled = isGoogleSignedUp,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { tryCompleteSignUp() },
                    enabled = isGoogleSignedUp,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Primary,
                        disabledContainerColor = OrangeTheme.Primary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Account", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                Row {
                    Text(
                        text = "Already have an account?",
                        color = OrangeTheme.TextSecondary
                    )
                    Text(
                        text = " Login",
                        color = OrangeTheme.Primary,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleSignButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = OrangeTheme.TextPrimary
        ),
        border = BorderStroke(1.dp, OrangeTheme.Border)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "G",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDB4437)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Continue with Google",
                fontWeight = FontWeight.Medium
            )
        }
    }
}