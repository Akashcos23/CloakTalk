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
import com.example.cloaktalk.ui.screens.encrypt.EncryptedOutputCard
import com.example.cloaktalk.ui.screens.encrypt.MessageInputCard
import com.example.cloaktalk.ui.screens.encrypt.performEncryption
import com.example.cloaktalk.ui.theme.OrangeTheme
import com.example.cloaktalk.ui.viewmodel.AlgorithmOption
import com.example.cloaktalk.ui.viewmodel.EncryptDecryptViewModel
import com.example.cloaktalk.ui.viewmodel.EncryptDecryptViewModelFactory

/**
 * Encrypt Screen Composable - Main screen for message encryption.
 *
 * This screen provides a complete interface for encrypting messages with the following features:
 * - Algorithm selection dropdown (Caesar, Vigenère, Playfair, and custom algorithms)
 * - Message input field for plaintext
 * - Key expiration time selection (1h to 72h)
 * - Encrypt button with loading state
 * - Encrypted output display with copy functionality
 * - Action buttons for encrypting another message or navigating back
 *
 * State Management:
 * - Local UI state is reset when navigating away using DisposableEffect
 * - ViewModel state is cleared when navigating back to home
 * - Fresh state is ensured on each screen visit
 *
 * @param onNavigate Callback function for navigation to other screens.
 *                   Called with screen route string (e.g., "home", "decrypt", "history")
 * @param designAlgorithmRepository Repository for accessing design algorithms
 * @param userId The ID of the currently logged-in user
 *
 * @see EncryptDecryptViewModel
 * @see MessageInputCard
 * @see EncryptedOutputCard
 * @see performEncryption
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptScreen(
    onNavigate: (String) -> Unit,
    designAlgorithmRepository: DesignAlgorithmRepository,
    userId: Long
) {
    // Get context and database instance
    val context = LocalContext.current
    val database = remember { CloakTalkDatabase.getInstance(context) }

    // Initialize ViewModel with all required repositories
    val viewModel: EncryptDecryptViewModel = viewModel(
        factory = EncryptDecryptViewModelFactory(
            BaseAlgorithmRepository(database.baseAlgorithmDao()),
            DesignAlgorithmRepository(database.designAlgorithmDao()),
            KeyRepository(database.keyDao()),
            EncryptMessageRepository(database.encryptMessageDao()),
            DecryptMessageRepository(database.decryptMessageDao())
        )
    )

    // Collect state from ViewModel
    val algorithms by viewModel.algorithms.collectAsState()
    val encryptionResult by viewModel.encryptionResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Local UI state
    var plaintext by remember { mutableStateOf("") }
    var selectedAlgorithm by remember { mutableStateOf<AlgorithmOption?>(null) }
    var keyExpireHours by remember { mutableStateOf(24) }
    var showAlgorithmDropdown by remember { mutableStateOf(false) }

    /**
     * Resets all local UI state to default values.
     * Called when user wants to encrypt another message or navigates away.
     */
    fun resetLocalState() {
        plaintext = ""
        selectedAlgorithm = null
        keyExpireHours = 24
        showAlgorithmDropdown = false
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

    // Clear ViewModel state when screen is first composed (fresh start)
    LaunchedEffect(Unit) {
        viewModel.clearAllState()
    }

    // Auto-select first algorithm when algorithms are loaded
    LaunchedEffect(algorithms) {
        if (algorithms.isNotEmpty() && selectedAlgorithm == null) {
            selectedAlgorithm = algorithms.first()
        }
    }

    Scaffold(
        containerColor = OrangeTheme.Background,
        bottomBar = {
            // Use navigateWithCleanup for bottom navigation
            BottomNavigation("encrypt") { destination ->
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
                EncryptScreenHeader()
            }

            // Main Content
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Algorithm Selection Card
                AlgorithmSelectionCard(
                    algorithms = algorithms,
                    selectedAlgorithm = selectedAlgorithm,
                    showDropdown = showAlgorithmDropdown,
                    onExpandedChange = { showAlgorithmDropdown = it },
                    onAlgorithmSelected = { algorithm ->
                        selectedAlgorithm = algorithm
                        showAlgorithmDropdown = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Message Input Card
                MessageInputCard(
                    plaintext = plaintext,
                    onPlaintextChange = { plaintext = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Key Expiration Card
                KeyExpirationCard(
                    selectedHours = keyExpireHours,
                    onHoursSelected = { keyExpireHours = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Encrypt Button
                EncryptButton(
                    enabled = plaintext.isNotEmpty() &&
                            selectedAlgorithm != null &&
                            !isLoading,
                    isLoading = isLoading,
                    onClick = {
                        selectedAlgorithm?.let { algorithm ->
                            viewModel.encryptMessage(
                                plaintext = plaintext,
                                selectedAlgorithm = algorithm,
                                keyExpireHours = keyExpireHours
                            ) { text, algoName, key ->
                                val result = performEncryption(
                                    plaintext = text,
                                    algorithm = algoName,
                                    key = key,
                                    charset = (' '..'~').toSet(),
                                    multipleRounds = false
                                )
                                result.encryptedText

                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Encryption Result Section
                encryptionResult?.let { result ->
                    EncryptionResultSection(
                        result = result,
                        keyExpireHours = keyExpireHours,
                        onEncryptAnother = {
                            // Reset local state and clear ViewModel result
                            resetLocalState()
                            viewModel.clearEncryptionResult()
                            // Re-select first algorithm
                            if (algorithms.isNotEmpty()) {
                                selectedAlgorithm = algorithms.first()
                            }
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
 * Header section for the Encrypt Screen with gradient background.
 * Displays the screen title and subtitle with an orange gradient.
 */
@Composable
private fun EncryptScreenHeader() {
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
                text = "Encrypt Message",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Secure your message with encryption",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Card containing the algorithm selection dropdown.
 *
 * @param algorithms List of available algorithms (base + custom)
 * @param selectedAlgorithm Currently selected algorithm
 * @param showDropdown Whether the dropdown is expanded
 * @param onExpandedChange Callback when dropdown expansion changes
 * @param onAlgorithmSelected Callback when an algorithm is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlgorithmSelectionCard(
    algorithms: List<AlgorithmOption>,
    selectedAlgorithm: AlgorithmOption?,
    showDropdown: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAlgorithmSelected: (AlgorithmOption) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Select Algorithm",
                fontWeight = FontWeight.Bold,
                color = OrangeTheme.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose an encryption algorithm",
                fontSize = 12.sp,
                color = OrangeTheme.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = showDropdown,
                onExpandedChange = onExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedAlgorithm?.name ?: "Select Algorithm",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdown)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeTheme.Primary,
                        unfocusedBorderColor = OrangeTheme.Border,
                        focusedTextColor = OrangeTheme.TextPrimary,
                        unfocusedTextColor = OrangeTheme.TextPrimary,
                        focusedTrailingIconColor = OrangeTheme.TextPrimary,
                        unfocusedTrailingIconColor = OrangeTheme.TextSecondary
                    )
                )

                ExposedDropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    if (algorithms.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No algorithms available") },
                            onClick = { },
                            enabled = false
                        )
                    } else {
                        algorithms.forEach { algorithm ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (algorithm.isDesignAlgorithm)
                                            "${algorithm.name} (Custom)"
                                        else algorithm.name
                                    )
                                },
                                onClick = { onAlgorithmSelected(algorithm) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card for selecting key expiration time.
 * Provides predefined options: 1h, 6h, 12h, 24h, 48h, 72h.
 *
 * @param selectedHours Currently selected expiration hours
 * @param onHoursSelected Callback when hours are selected
 */
@Composable
private fun KeyExpirationCard(
    selectedHours: Int,
    onHoursSelected: (Int) -> Unit
) {
    val expirationOptions = listOf(1, 6, 12, 24, 48, 72)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Key Expiration",
                fontWeight = FontWeight.Bold,
                color = OrangeTheme.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "How long should the decryption key be valid?",
                fontSize = 12.sp,
                color = OrangeTheme.TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                expirationOptions.forEach { hours ->
                    FilterChip(
                        selected = selectedHours == hours,
                        onClick = { onHoursSelected(hours) },
                        label = { Text("${hours}h") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangeTheme.Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

/**
 * Encrypt button with loading indicator.
 *
 * @param enabled Whether the button is enabled
 * @param isLoading Whether encryption is in progress
 * @param onClick Callback when button is clicked
 */
@Composable
private fun EncryptButton(
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
                text = "Encrypt Message",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Section displaying encryption result with action buttons.
 *
 * @param result The encryption result to display
 * @param keyExpireHours The key expiration time in hours
 * @param onEncryptAnother Callback to encrypt another message
 * @param onNavigateBack Callback to navigate back to home
 */
@Composable
private fun EncryptionResultSection(
    result: com.example.cloaktalk.ui.viewmodel.EncryptionResult,
    keyExpireHours: Int,
    onEncryptAnother: () -> Unit,
    onNavigateBack: () -> Unit
) {
    if (result.success) {
        // Success state - show encrypted output
        EncryptedOutputCard(
            encryptedMessage = result.encryptedMessage,
            encryptionKey = result.key
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Key expiration info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱️",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Key expires in $keyExpireHours hours",
                    color = Color(0xFFE65100),
                    fontSize = 14.sp
                )
            }
        }
    } else {
        // Error state
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
                        text = "❌",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encryption Failed",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.errorMessage ?: "An error occurred during encryption.",
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
            onClick = onEncryptAnother,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = OrangeTheme.Primary
            )
        ) {
            Text(
                text = "Encrypt Another",
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
