package com.example.cloaktalk.ui.screens

import com.example.cloaktalk.ui.screens.designer.AlgoNameCard
import com.example.cloaktalk.ui.screens.designer.BaseAlgoCard
import com.example.cloaktalk.ui.screens.designer.AlgoParametersCard
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
import com.example.cloaktalk.data.repository.DesignAlgorithmRepository
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.screens.designer.CharacterSetBuilder
import com.example.cloaktalk.ui.screens.designer.EncryptionPreviewCard
import com.example.cloaktalk.ui.screens.designer.StrengthMeterCard
import com.example.cloaktalk.ui.screens.designer.calculateStrengthScore
import com.example.cloaktalk.ui.screens.designer.previewEncryption
import com.example.cloaktalk.ui.theme.OrangeTheme
import kotlinx.coroutines.launch

/**
 * Composable screen for designing custom encryption algorithms.
 *
 * This screen allows users to:
 * - Create a custom algorithm with a unique name
 * - Select a base algorithm (Caesar, Vigenère, or Substitution)
 * - Configure algorithm parameters (shift amount, multiple rounds)
 * - Define a custom character set for encryption
 * - Preview encryption results in real-time
 * - Save the algorithm to the database
 *
 * The algorithm is saved with all configuration options including:
 * - Algorithm name (unique per user)
 * - Base algorithm type
 * - Character set
 * - Shift amount (for Caesar/Vigenère)
 * - Multiple rounds option
 *
 * @param onNavigate Callback for navigation events (e.g., "home", "encrypt")
 * @param designAlgorithmRepository Repository for saving design algorithms to database
 * @param userId The ID of the currently logged-in user (required for foreign key)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerScreen(
    onNavigate: (String) -> Unit,
    designAlgorithmRepository: DesignAlgorithmRepository,
    userId: Long
) {
    // ==================== State Management ====================

    /** Coroutine scope for database operations */
    val scope = rememberCoroutineScope()

    /** Snackbar host state for showing feedback messages */
    val snackbarHostState = remember { SnackbarHostState() }

    /** Algorithm name entered by the user (must be unique per user) */
    var algoName by remember { mutableStateOf("") }

    /** Selected base algorithm (UI display name) */
    var baseAlgo by remember { mutableStateOf("Caesar Cipher") }

    /** Shift amount for Caesar/Vigenère algorithms */
    var shiftAmount by remember { mutableStateOf("") }

    /** Test message for live encryption preview */
    var testMessage by remember { mutableStateOf("") }

    /** Whether to apply multiple encryption rounds */
    var multipleRounds by remember { mutableStateOf(false) }

    /** Set of characters allowed in the encryption */
    var selectedChars by remember { mutableStateOf(getDefaultCharacterSet()) }

    /** Live preview of encrypted text */
    var encryptedPreview by remember { mutableStateOf("") }

    /** Loading state during save operation */
    var isSaving by remember { mutableStateOf(false) }

    // ==================== Helper Functions ====================

    /**
     * Resets all form fields to their default values.
     * Called after successful algorithm creation.
     */
    fun resetForm() {
        algoName = ""
        baseAlgo = "Caesar Cipher"
        shiftAmount = ""
        testMessage = ""
        multipleRounds = false
        selectedChars = getDefaultCharacterSet()
        encryptedPreview = ""
    }

    /**
     * Converts the selected character set to a string for database storage.
     * Characters are sorted for consistent storage format.
     *
     * @return String representation of the character set
     */
    fun getCharsetString(): String {
        return selectedChars.sorted().joinToString("")
    }

    /**
     * Maps the UI base algorithm name to the database baseAlgoName.
     * These names must match exactly with the base_algorithm table entries.
     *
     * Mapping:
     * - "Caesar Cipher" -> "Caesar"
     * - "Vigenère Cipher" -> "Vigenere"
     * - "Substitution Cipher" -> "Substitution"
     *
     * @return The database-compatible base algorithm name
     */
    fun getBaseAlgoName(): String {
        return when (baseAlgo) {
            "Caesar Cipher" -> "Caesar"
            "Vigenère Cipher" -> "Vigenere"
            "Substitution Cipher" -> "Substitution"
            else -> baseAlgo
        }
    }

    /**
     * Gets the shift amount as Int or null.
     * Returns null for Substitution cipher which doesn't use shift.
     *
     * @return The parsed shift amount or null
     */
    fun getShiftAmountValue(): Int? {
        return if (baseAlgo == "Substitution Cipher") {
            null
        } else {
            shiftAmount.toIntOrNull()
        }
    }

    // ==================== Computed Values ====================

    /** Calculate strength score based on current configuration */
    val strengthScore = remember(shiftAmount, multipleRounds, selectedChars.size, baseAlgo) {
        calculateStrengthScore(
            baseAlgo = baseAlgo,
            shiftAmount = shiftAmount,
            multipleRounds = multipleRounds,
            charSetSize = selectedChars.size
        )
    }

    // ==================== Side Effects ====================

    /** Update preview when test message or parameters change */
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

    // ==================== UI Layout ====================

    Scaffold(
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("designer", onNavigate) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header section with gradient background
            item {
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
                        Text(
                            "Encryption Designer",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Create your custom algorithm",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Form content
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Strength Meter Card - shows algorithm security rating
                StrengthMeterCard(strengthScore = strengthScore)

                Spacer(modifier = Modifier.height(16.dp))

                // Card for entering algorithm name
                AlgoNameCard(algoName = algoName, onNameChange = { algoName = it })

                Spacer(modifier = Modifier.height(16.dp))

                // Card for selecting base algorithm
                BaseAlgoCard(baseAlgo = baseAlgo, onAlgoChange = { baseAlgo = it })

                Spacer(modifier = Modifier.height(16.dp))

                // Card for algorithm parameters (shift amount, multiple rounds)
                AlgoParametersCard(
                    shiftAmount = shiftAmount,
                    onShiftChange = { shiftAmount = it },
                    multipleRounds = multipleRounds,
                    onRoundsChange = { multipleRounds = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Character Set Builder Card
                CharacterSetBuilder(
                    selectedChars = selectedChars,
                    onSelectionChange = { selectedChars = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Visual Cipher Preview Card
                EncryptionPreviewCard(
                    testMessage = testMessage,
                    onTestMessageChange = { testMessage = it },
                    encryptedPreview = encryptedPreview
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons row
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
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    // Create Algorithm button - validates and saves to database
                    Button(
                        onClick = {
                            // Validate inputs before saving
                            when {
                                algoName.isBlank() -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Please enter an algorithm name",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                                selectedChars.isEmpty() -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Please select at least one character",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                                else -> {
                                    // Save algorithm to database with ALL fields
                                    isSaving = true
                                    scope.launch {
                                        try {
                                            val result = designAlgorithmRepository.createDesignAlgorithm(
                                                algoName = algoName.trim(),
                                                baseAlgoName = getBaseAlgoName(),
                                                charset = getCharsetString(),
                                                userId = userId,
                                                shiftAmount = getShiftAmountValue(),
                                                multipleRounds = multipleRounds
                                            )

                                            if (result > 0) {
                                                // Success - show message and reset form
                                                snackbarHostState.showSnackbar(
                                                    message = "Algorithm \"${algoName.trim()}\" created successfully!",
                                                    duration = SnackbarDuration.Short
                                                )
                                                resetForm()
                                            } else {
                                                // Algorithm name already exists for this user
                                                snackbarHostState.showSnackbar(
                                                    message = "Algorithm name already exists. Please choose a different name.",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        } catch (e: Exception) {
                                            // Handle foreign key or other database errors
                                            snackbarHostState.showSnackbar(
                                                message = "Error creating algorithm: ${e.message}",
                                                duration = SnackbarDuration.Long
                                            )
                                        } finally {
                                            isSaving = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeTheme.Primary
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Create Algorithm", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Get default character set (uppercase and lowercase letters).
 * This is the initial selection when the screen loads.
 *
 * @return Set of default characters for encryption (A-Z, a-z)
 */
private fun getDefaultCharacterSet(): Set<Char> {
    return (('A'..'Z') + ('a'..'z')).toSet()
}
