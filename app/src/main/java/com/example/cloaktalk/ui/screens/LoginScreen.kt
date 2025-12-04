package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
/**
 * Composable screen for user login.
 * Provides email and password fields, login button, and navigation to sign up.
 *
 * @param onLogin Callback for login action
 * @param onSignUp Callback for sign up navigation
 
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLogin: () -> Unit, onSignUp: () -> Unit) {
    // State for email input field
import androidx.compose.material3.*
    // State for password input field
import androidx.compose.runtime.*
    // State to toggle password visibility
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
    // Main container for the login screen UI
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLogin: () -> Unit, onSignUp: () -> Unit) {

    var email by remember { mutableStateOf("") }
        // Card containing the login form
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                // App logo in a circular gradient background
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
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                // Spacer between logo and title
                        .size(80.dp)
                        .background(
                // App title
                            Brush.horizontalGradient(
                                colors = listOf(OrangeTheme.Primary, OrangeTheme.PrimaryDark)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                // App subtitle
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                // Spacer before input fields
                }

                // Email input field
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CloakTalk",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary
                )

                Text(
                    text = "End-to-end encryption made easy",
                    fontSize = 14.sp,
                    color = OrangeTheme.TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Spacer before password field
                OutlinedTextField(
                    value = email,
                // Password input field with visibility toggle
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.LightGray) },
                    placeholder = { Text("your@email.com", color = Color.LightGray) },
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

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.LightGray) },
                    placeholder = { Text("Enter password", color = Color.LightGray) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                // Spacer before login button
                                contentDescription = null,
                                tint = OrangeTheme.TextSecondary
                // Login button
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
                // Spacer before forgot password link
                )

                // Forgot password link
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLogin,
                // Sign up link and prompt
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { }) {
                    Text("Forgot password?", color = OrangeTheme.Primary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Don't have an account? ", color = OrangeTheme.TextSecondary, fontSize = 14.sp)
                    TextButton(onClick = onSignUp, contentPadding = PaddingValues(0.dp)) {
                        Text("Sign up", color = OrangeTheme.Primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}