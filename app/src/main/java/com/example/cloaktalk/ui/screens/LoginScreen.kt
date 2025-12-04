package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

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
    var email by remember { mutableStateOf("") }
    // State for password input field
    var password by remember { mutableStateOf("") }
    // State to toggle password visibility
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
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Card containing the login form
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
                // App logo in a circular gradient background
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
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Title
                Text(
                    text = "CloakTalk",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary
                )

                // App Subtitle
                Text(
                    text = "End-to-end encryption made easy",
                    fontSize = 14.sp,
                    color = OrangeTheme.TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email input field
                OutlinedTextField(
                    value = email,
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

                // Password input field with visibility toggle
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

                // Login button
                Button(
                    onClick = onLogin,
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

                // Forgot password link
                TextButton(onClick = { }) {
                    Text("Forgot password?", color = OrangeTheme.Primary)
                }

                // Sign up link
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Don't have an account? ",
                        color = OrangeTheme.TextSecondary,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = onSignUp, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            "Sign up",
                            color = OrangeTheme.Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}