package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.screens.encrypt.AlgorithmSelector
import com.example.cloaktalk.ui.screens.encrypt.EncryptedOutputCard
import com.example.cloaktalk.ui.screens.encrypt.MessageInputCard
import com.example.cloaktalk.ui.screens.encrypt.generateEncryptionKey
import com.example.cloaktalk.ui.screens.encrypt.performEncryption
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for encrypting messages using selected algorithms.
 * Generates random encryption keys and displays encrypted output.
 *
 * @param onNavigate Callback for navigation events
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptScreen(onNavigate: (String) -> Unit) {
    // Holds the plaintext message to be encrypted
    var plaintext by remember { mutableStateOf("") }

    // Holds the selected encryption algorithm
    var selectedAlgorithm by remember { mutableStateOf("Caesar Cipher") }

    // Holds the generated encryption key
    var encryptionKey by remember { mutableStateOf("") }

    // Holds the encrypted message result
    var encryptedMessage by remember { mutableStateOf("") }

    // Track if encryption has been performed
    var isEncrypted by remember { mutableStateOf(false) }

    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("encrypt", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of encryption options
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // Header section with gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFDC2626),
                                    Color(0xFFEA580C),
                                    Color(0xFFF97316),
                                    Color(0xFFFB923C)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(1000f, 300f)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        // Title for encrypt screen
                        Text(
                            "Encrypt Message",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        // Subtitle for encrypt screen
                        Text(
                            "Convert your message into secure cipher",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(24.dp))

                // Algorithm selection card
                AlgorithmSelector(
                    selectedAlgorithm = selectedAlgorithm,
                    onAlgorithmChange = { selectedAlgorithm = it }
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Message input card
                MessageInputCard(
                    plaintext = plaintext,
                    onPlaintextChange = { plaintext = it }
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Encrypt button
                Button(
                    onClick = {
                        if (plaintext.isNotEmpty()) {
                            encryptionKey = generateEncryptionKey(selectedAlgorithm)
                            encryptedMessage = performEncryption(
                                plaintext = plaintext,
                                algorithm = selectedAlgorithm,
                                key = encryptionKey,
                                charset = (' '..'~').toSet(),
                                multipleRounds = false
                            )
                            isEncrypted = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Primary
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    enabled = plaintext.isNotEmpty()
                ) {
                    Text(
                        "Encrypt Message",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Show encrypted output if encryption has been performed
                if (isEncrypted) {
                    // Encrypted output card
                    EncryptedOutputCard(
                        encryptedMessage = encryptedMessage,
                        encryptionKey = encryptionKey
                    )

                    // Spacer for layout separation
                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Encrypt another button
                        OutlinedButton(
                            onClick = {
                                plaintext = ""
                                encryptionKey = ""
                                encryptedMessage = ""
                                isEncrypted = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = OrangeTheme.TextSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeTheme.Border),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Text("Encrypt Another", fontWeight = FontWeight.Bold)
                        }

                        // Back button
                        OutlinedButton(
                            onClick = { onNavigate("home") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = OrangeTheme.TextSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeTheme.Border),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Text("Back", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Bottom spacer
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}