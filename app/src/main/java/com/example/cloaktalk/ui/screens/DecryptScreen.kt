package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.screens.decrypt.DecryptAlgorithmSelector
import com.example.cloaktalk.ui.screens.decrypt.DecryptedOutputCard
import com.example.cloaktalk.ui.screens.decrypt.EncryptedMessageInputCard
import com.example.cloaktalk.ui.screens.decrypt.KeyInputCard
import com.example.cloaktalk.ui.screens.decrypt.performDecryption
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for decrypting encrypted messages using the correct key.
 * Validates key matches before displaying decrypted content.
 *
 * @param onNavigate Callback for navigation events
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecryptScreen(onNavigate: (String) -> Unit) {
    // Holds the encrypted message to be decrypted
    var encryptedMessage by remember { mutableStateOf("") }

    // Holds the decryption key entered by user
    var decryptionKey by remember { mutableStateOf("") }

    // Holds the selected decryption algorithm
    var selectedAlgorithm by remember { mutableStateOf("Caesar Cipher") }

    // Holds the decrypted message result
    var decryptedMessage by remember { mutableStateOf("") }

    // Track if decryption has been attempted
    var decryptionAttempted by remember { mutableStateOf(false) }

    // Track if key is valid
    var isKeyValid by remember { mutableStateOf(false) }

    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("decrypt", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of decryption options
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
                        // Title for decrypt screen
                        Text(
                            "Decrypt Message",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        // Subtitle for decrypt screen
                        Text(
                            "Unlock your encrypted message with the correct key",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(24.dp))

                // Algorithm selection card for decryption
                DecryptAlgorithmSelector(
                    selectedAlgorithm = selectedAlgorithm,
                    onAlgorithmChange = { selectedAlgorithm = it }
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Encrypted message input card
                EncryptedMessageInputCard(
                    encryptedMessage = encryptedMessage,
                    onEncryptedMessageChange = { encryptedMessage = it }
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Decryption key input card
                KeyInputCard(
                    decryptionKey = decryptionKey,
                    onKeyChange = { decryptionKey = it }
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Decrypt button

                Button(
                    onClick = {
                        if (encryptedMessage.isNotEmpty() && decryptionKey.isNotEmpty()) {
                            val result = performDecryption(
                                encryptedMessage = encryptedMessage,
                                algorithm = selectedAlgorithm,
                                key = decryptionKey,
                                charset =  (' '..'~').toSet(),
                                multipleRounds = false // <--- ADD THIS LINE
                            )
                            decryptedMessage = result.decryptedText
                            isKeyValid = result.isValid
                            decryptionAttempted = true
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
                    enabled = encryptedMessage.isNotEmpty() && decryptionKey.isNotEmpty()
                ) {
                    Text(
                        "Decrypt Message",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Show decryption result if attempt has been made
                if (decryptionAttempted) {
                    // Decrypted output card
                    DecryptedOutputCard(
                        decryptedMessage = decryptedMessage,
                        isKeyValid = isKeyValid
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
                        // Decrypt another button
                        OutlinedButton(
                            onClick = {
                                encryptedMessage = ""
                                decryptionKey = ""
                                decryptedMessage = ""
                                decryptionAttempted = false
                                isKeyValid = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = OrangeTheme.TextSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeTheme.Border),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Text("Decrypt Another", fontWeight = FontWeight.Bold)
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