package com.example.cloaktalk.ui.screens


import com.example.cloaktalk.ui.screens.designer.AlgoNameCard
import com.example.cloaktalk.ui.screens.designer.BaseAlgoCard
import com.example.cloaktalk.ui.screens.designer.AlgoParametersCard
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
import com.example.cloaktalk.ui.screens.designer.CharacterSetBuilder
import com.example.cloaktalk.ui.screens.designer.EncryptionPreviewCard
import com.example.cloaktalk.ui.screens.designer.StrengthMeterCard
import com.example.cloaktalk.ui.screens.designer.calculateStrengthScore
import com.example.cloaktalk.ui.screens.designer.previewEncryption
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for designing custom encryption algorithms.
 * Includes interactive character set builder for visual customization.
 *
 * @param onNavigate Callback for navigation events
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerScreen(onNavigate: (String) -> Unit) {
    // Holds the algorithm name entered by the user
    var algoName by remember { mutableStateOf("") }
    // Holds the selected base algorithm
    var baseAlgo by remember { mutableStateOf("Caesar Cipher") }
    // Holds the shift amount for algorithms that use it
    var shiftAmount by remember { mutableStateOf("") }
    // Holds the test message for encryption
    var testMessage by remember { mutableStateOf("") }
    // Advanced option: multiple encryption rounds
    var multipleRounds by remember { mutableStateOf(false) }

    // Character set selections
    var selectedChars by remember { mutableStateOf(getDefaultCharacterSet()) }

    // Encrypted result from live preview
    var encryptedPreview by remember { mutableStateOf("") }

    // Calculate strength score based on current configuration
    val strengthScore = remember(shiftAmount, multipleRounds, selectedChars.size, baseAlgo) {
        calculateStrengthScore(
            baseAlgo = baseAlgo,
            shiftAmount = shiftAmount,
            multipleRounds = multipleRounds,
            charSetSize = selectedChars.size
        )
    }

    // Update preview when test message or parameters change
    LaunchedEffect(testMessage, baseAlgo, shiftAmount, multipleRounds, selectedChars) {
        if (testMessage.isNotEmpty()) {
            encryptedPreview = previewEncryption(
                plaintext = testMessage,
                baseAlgo = baseAlgo,
                shiftAmount = shiftAmount.toIntOrNull() ?: 13,
                multipleRounds = multipleRounds,
                charset = selectedChars
            )
        } else {
            encryptedPreview = ""
        }
    }

    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("designer", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of designer options
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
                        // Title for designer screen
                        Text("Encryption Designer", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        // Subtitle for designer screen
                        Text("Create your custom algorithm", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            item {
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(24.dp))

                // Strength Meter Card
                StrengthMeterCard(strengthScore = strengthScore)

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Card for entering algorithm name
                AlgoNameCard(algoName = algoName, onNameChange = { algoName = it })

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Card for selecting base algorithm
                BaseAlgoCard(baseAlgo = baseAlgo, onAlgoChange = { baseAlgo = it })

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Card for algorithm parameters
                AlgoParametersCard(
                    shiftAmount = shiftAmount,
                    onShiftChange = { shiftAmount = it },
                    multipleRounds = multipleRounds,
                    onRoundsChange = { multipleRounds = it }
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Character Set Builder Card
                CharacterSetBuilder(
                    selectedChars = selectedChars,
                    onSelectionChange = { selectedChars = it }
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Visual Cipher Preview Card
                EncryptionPreviewCard(
                    testMessage = testMessage,
                    onTestMessageChange = { testMessage = it },
                    encryptedPreview = encryptedPreview
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(24.dp))

                // Row containing Cancel and Save buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel button - navigates back to home
                    OutlinedButton(
                        onClick = { onNavigate("home") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OrangeTheme.TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangeTheme.Border),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    // Save button - saves the algorithm configuration
                    Button(
                        onClick = { /* Save algorithm config */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeTheme.Primary
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Algorithm", fontWeight = FontWeight.Bold)
                    }
                }

                // Bottom spacer
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Get default character set (uppercase and lowercase letters)
 */
private fun getDefaultCharacterSet(): Set<Char> {
    return (('A'..'Z') + ('a'..'z')).toSet()
}