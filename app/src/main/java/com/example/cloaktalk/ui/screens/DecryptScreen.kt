package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloaktalk.data.local.database.CloakTalkDatabase
import com.example.cloaktalk.data.repository.*
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.screens.decrypt.DecryptedOutputCard
import com.example.cloaktalk.ui.screens.decrypt.EncryptedMessageInputCard
import com.example.cloaktalk.ui.screens.decrypt.KeyInputCard
import com.example.cloaktalk.ui.screens.decrypt.performDecryption
import com.example.cloaktalk.ui.theme.OrangeTheme
import com.example.cloaktalk.ui.viewmodel.DecryptionResult
import com.example.cloaktalk.ui.viewmodel.EncryptDecryptViewModel
import com.example.cloaktalk.ui.viewmodel.EncryptDecryptViewModelFactory

/**
 * Decrypt Screen Composable - Main screen for message decryption.
 *
 * This screen provides a complete interface for decrypting messages with the following features:
 * - Key-based decryption (algorithm is automatically determined from the key)
 * - Encrypted message input field
 * - Decryption key input field (8-digit key from encryption)
 * - Decrypt button with loading state
 * - Decrypted output display
 * - Error handling for invalid/expired keys
 * - Action buttons for decrypting another message or navigating back
 *
 * State Management:
 * - Local UI state is reset when navigating away
 * - ViewModel state is cleared when navigating back to home
 * - Fresh state is ensured on each screen visit
 *
 * @param onNavigate Callback function for navigation to other screens.
 *                   Called with screen route string (e.g., "home", "encrypt", "history")
 * @param designAlgorithmRepository Repository for accessing design algorithms
 * @param userId The ID of the currently logged-in user
 *
 * @see EncryptDecryptViewModel
 * @see EncryptedMessageInputCard
 * @see KeyInputCard
 * @see DecryptedOutputCard
 * @see performDecryption
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecryptScreen(
    onNavigate: (String) -> Unit,
    designAlgorithmRepository: DesignAlgorithmRepository,
    userId: Long
) {
    // Get context and database instance
    val context = LocalContext.current
    val database = remember { CloakTalkDatabase.getInstance(context) }

    // Initialize ViewModel with all required repositories
    // Use userId as key to ensure a new ViewModel is created when user changes
    val viewModel: EncryptDecryptViewModel = viewModel(
        key = "decrypt_viewmodel_$userId",
        factory = EncryptDecryptViewModelFactory(
            BaseAlgorithmRepository(database.baseAlgorithmDao()),
            DesignAlgorithmRepository(database.designAlgorithmDao()),
            KeyRepository(database.keyDao()),
            EncryptMessageRepository(database.encryptMessageDao()),
            DecryptMessageRepository(database.decryptMessageDao()),
            HistoryRepository(database.historyDao())
        )
    )

    // Collect state from ViewModel
    val decryptionResult by viewModel.decryptionResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Local UI state
    var encryptedMessage by remember { mutableStateOf("") }
    var decryptionKey by remember { mutableStateOf("") }

    /**
     * Resets all local UI state to default values.
     * Called when user wants to decrypt another message or navigates away.
     */
    fun resetLocalState() {
        encryptedMessage = ""
        decryptionKey = ""
    }

    /**
     * Handles navigation with state cleanup.
     * Clears both local and ViewModel state before navigating.
     *
     * @param destination The screen route to navigate to
     */
    fun navigateWithCleanup(destination: String) {
        resetLocalState()
        viewModel.clearAllState()
        onNavigate(destination)
    }

    // Clear ViewModel state and refresh algorithms when screen is first composed (fresh start)
    LaunchedEffect(Unit) {
        viewModel.clearAllState()
        viewModel.refreshAlgorithms()
    }

    Scaffold(
        containerColor = OrangeTheme.Background,
        bottomBar = {
            // Use navigateWithCleanup for bottom navigation
            BottomNavigation("decrypt") { destination ->
                navigateWithCleanup(destination)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header Section
            item {
                DecryptScreenHeader()
            }

            // Main Content
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Encrypted Message Input Card
                EncryptedMessageInputCard(
                    encryptedMessage = encryptedMessage,
                    onEncryptedMessageChange = { encryptedMessage = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Key Input Card
                KeyInputCard(
                    decryptionKey = decryptionKey,
                    onKeyChange = { decryptionKey = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Info card about key-based decryption
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Primary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ℹ️",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "The algorithm is automatically detected from your key",
                            color = OrangeTheme.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Decrypt Button
                DecryptButton(
                    enabled = encryptedMessage.isNotEmpty() &&
                            decryptionKey.isNotEmpty() &&
                            !isLoading,
                    isLoading = isLoading,
                    onClick = {
                        viewModel.decryptMessageByKey(
                            encryptedText = encryptedMessage,
                            key = decryptionKey,
                            userId = userId
                        ) { text, algoName, key ->
                            val result = performDecryption(
                                encryptedMessage = text,
                                algorithm = algoName,
                                key = key,
                                charset = (' '..'~').toSet(),
                                multipleRounds = false
                            )
                            result.decryptedText
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Decryption Result Section
                decryptionResult?.let { result ->
                    DecryptionResultSection(
                        result = result,
                        onDecryptAnother = {
                            // Reset local state and clear ViewModel result
                            resetLocalState()
                            viewModel.clearDecryptionResult()
                        },
                        onNavigateBack = {
                            // Navigate with full cleanup
                            navigateWithCleanup("home")
                        }
                    )
                }
            }
        }
    }
}

/**
 * Header section for the Decrypt Screen with gradient background.
 * Displays the screen title and subtitle with an orange gradient.
 */
@Composable
private fun DecryptScreenHeader() {
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
                text = "Decrypt Message",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unlock your encrypted message with the correct key",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Decrypt button with loading indicator.
 *
 * @param enabled Whether the button is enabled
 * @param isLoading Whether decryption is in progress
 * @param onClick Callback when button is clicked
 */
@Composable
private fun DecryptButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OrangeTheme.Primary),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Decrypt Message",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Section displaying decryption result with action buttons.
 *
 * @param result The decryption result to display
 * @param onDecryptAnother Callback to decrypt another message
 * @param onNavigateBack Callback to navigate back to home
 */
@Composable
private fun DecryptionResultSection(
    result: DecryptionResult,
    onDecryptAnother: () -> Unit,
    onNavigateBack: () -> Unit
) {
    if (result.success) {
        // Success state
        DecryptedOutputCard(
            decryptedMessage = result.decryptedMessage,
            isKeyValid = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Success info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✅",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Message decrypted successfully",
                    color = Color(0xFF2E7D32),
                    fontSize = 14.sp
                )
            }
        }
    } else {
        // Error state - Invalid or expired key
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔐",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Decryption Failed",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.errorMessage ?: "Invalid or expired key. Please check your key and try again.",
                    color = Color.Red.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Action buttons (shown for both success and error states)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDecryptAnother,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = OrangeTheme.Primary
            )
        ) {
            Text(
                text = "Decrypt Another",
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = OrangeTheme.Primary
            )
        ) {
            Text(
                text = "Back to Home",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
