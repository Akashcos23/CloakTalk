package com.example.cloaktalk.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
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
import com.example.cloaktalk.data.local.database.CloakTalkDatabase
import com.example.cloaktalk.data.repository.UserRepository
import com.example.cloaktalk.ui.theme.OrangeTheme
import kotlinx.coroutines.launch

/**
 * LoginScreen composable - handles user authentication.
 * Validates email and password against local Room database.
 *
 * @param onLogin Callback invoked when login is successful
 * @param onSignUp Callback invoked when user wants to navigate to sign up
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLogin: (Long) -> Unit, onSignUp: () -> Unit) {
    // State for email and password input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Get current context
    val context = LocalContext.current

    // Coroutine scope for async operations
    val scope = rememberCoroutineScope()

    // Database and repository setup
    val database = remember { CloakTalkDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(database.userDao()) }

    /**
     * Attempts to login user with email and password.
     * Validates credentials against Room database.
     */
    fun tryEmailLogin() {
        when {
            email.isBlank() -> {
                Toast.makeText(context, "Enter your email", Toast.LENGTH_SHORT).show()
            }
            password.isBlank() -> {
                Toast.makeText(context, "Enter your password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                isLoading = true
                scope.launch {
                    val user = userRepository.loginUser(
                        email = email.trim(),
                        password = password
                    )

                    isLoading = false

                    if (user != null) {
                        Toast.makeText(
                            context,
                            "Welcome back, ${user.username}!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onLogin(user.id)
                    } else {
                        Toast.makeText(
                            context,
                            "Login failed. Invalid email or password.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // Main UI layout with card design
    CardLayout(
        title = "Welcome Back",
        subtitle = "Securely sign in to CloakTalk"
    ) {
        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = OrangeTheme.Primary,
                unfocusedBorderColor = OrangeTheme.Border,
                focusedLabelColor = OrangeTheme.Primary,
                unfocusedLabelColor = OrangeTheme.TextSecondary,
                cursorColor = OrangeTheme.Primary
            )
        )

        Spacer(Modifier.height(12.dp))

        // Password input field with visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    enabled = !isLoading
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = OrangeTheme.Primary,
                unfocusedBorderColor = OrangeTheme.Border,
                focusedLabelColor = OrangeTheme.Primary,
                unfocusedLabelColor = OrangeTheme.TextSecondary,
                cursorColor = OrangeTheme.Primary
            )
        )

        Spacer(Modifier.height(16.dp))

        // Sign-in button
        Button(
            onClick = { tryEmailLogin() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeTheme.Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Sign in", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))

        // Sign up navigation link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account?",
                color = OrangeTheme.TextSecondary
            )
            Text(
                text = " Sign up",
                color = OrangeTheme.Primary,
                modifier = Modifier.clickable { onSignUp() }
            )
        }
    }
}

/**
 * Reusable card layout with gradient background and centered content.
 *
 * @param title Main title text
 * @param subtitle Secondary description text
 * @param content Composable content to display inside the card
 */
@Composable
private fun CardLayout(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
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
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shield icon with gradient background
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
                        imageVector = Icons.Default.Shield,
                        contentDescription = "CloakTalk Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Title and subtitle
                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = OrangeTheme.TextSecondary
                )

                Spacer(Modifier.height(18.dp))

                // Dynamic content
                content()
            }
        }
    }
}