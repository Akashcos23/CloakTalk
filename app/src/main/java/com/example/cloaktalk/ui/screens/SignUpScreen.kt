package com.example.cloaktalk.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- IMPORTS FOR CREDENTIAL MANAGER ---
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

// --- YOUR PROJECT IMPORTS ---
import com.example.cloaktalk.R
import com.example.cloaktalk.ui.theme.OrangeTheme
import com.example.cloaktalk.auth.FirebaseAuthManager

/**
 * Composable screen for user registration.
 * Provides fields for name, email, password, and Google sign-up.
 *
 * @param onSignUp Callback for successful sign up
 * @param onBackToLogin Callback for navigation to login
 * @param onGoogleSignUp Callback for Google sign up
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUp: () -> Unit,
    onBackToLogin: () -> Unit,
    onGoogleSignUp: () -> Unit = {}
) {
    // State variables
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Initialize Credential Manager and Coroutine Scope
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()

    // Helper function for Email Sign Up
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF111827),
                        Color(0xFF1F2937),
                        Color(0xFF78350F)
                    )
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
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(OrangeTheme.Primary, OrangeTheme.PrimaryDark)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Create Account",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary
                )

                Text(
                    text = "Join CloakTalk today",
                    fontSize = 14.sp,
                    color = OrangeTheme.TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // GOOGLE SIGN-UP BUTTON
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // A. Build the Google Option
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setAutoSelectEnabled(false)
                                    .setNonce(null)
                                    .build()

                                // B. Create the Request
                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                // C. Execute the Request
                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = context
                                )

                                // D. Parse the Result
                                val credential = result.credential
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val idToken = googleIdTokenCredential.idToken

                                // E. Authenticate with Firebase
                                FirebaseAuthManager.signInWithGoogle(idToken) { success, _, message ->
                                    if (success) {
                                        Toast.makeText(context, "Account created! Please log in", Toast.LENGTH_SHORT).show()
                                        // Sign out to force manual login next time (optional flow choice)
                                        //FirebaseAuthManager.signOut() //will be added later to firebase Auth
                                        onBackToLogin()
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF5F6368)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp,
                        brush = Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFFE0E0E0)))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = "Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Sign up with Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f).height(1.dp), color = OrangeTheme.Border)
                    Text(
                        text = " OR ",
                        color = OrangeTheme.TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Divider(modifier = Modifier.weight(1f).height(1.dp), color = OrangeTheme.Border)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Full Name Input
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name", color = Color.LightGray) },
                    placeholder = { Text("John Doe", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = OrangeTheme.Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeTheme.Primary,
                        unfocusedBorderColor = OrangeTheme.Border,
                        focusedContainerColor = OrangeTheme.SurfaceVariant,
                        unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                        focusedTextColor = OrangeTheme.TextPrimary,
                        unfocusedTextColor = OrangeTheme.TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.LightGray) },
                    placeholder = { Text("your@email.com", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = OrangeTheme.Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeTheme.Primary,
                        unfocusedBorderColor = OrangeTheme.Border,
                        focusedContainerColor = OrangeTheme.SurfaceVariant,
                        unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                        focusedTextColor = OrangeTheme.TextPrimary,
                        unfocusedTextColor = OrangeTheme.TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.LightGray) },
                    placeholder = { Text("Enter password", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = OrangeTheme.Primary) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = OrangeTheme.TextSecondary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeTheme.Primary,
                        unfocusedBorderColor = OrangeTheme.Border,
                        focusedContainerColor = OrangeTheme.SurfaceVariant,
                        unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                        focusedTextColor = OrangeTheme.TextPrimary,
                        unfocusedTextColor = OrangeTheme.TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Input
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password", color = Color.LightGray) },
                    placeholder = { Text("Re-enter password", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = OrangeTheme.Primary) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = OrangeTheme.TextSecondary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeTheme.Primary,
                        unfocusedBorderColor = OrangeTheme.Border,
                        focusedContainerColor = OrangeTheme.SurfaceVariant,
                        unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                        focusedTextColor = OrangeTheme.TextPrimary,
                        unfocusedTextColor = OrangeTheme.TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Create Account Button
                Button(
                    onClick = { tryEmailSignUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer: Login Link
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", color = OrangeTheme.TextSecondary, fontSize = 14.sp)
                    TextButton(onClick = onBackToLogin, contentPadding = PaddingValues(0.dp)) {
                        Text("Login", color = OrangeTheme.Primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
