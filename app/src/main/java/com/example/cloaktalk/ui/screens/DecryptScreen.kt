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
    import com.example.cloaktalk.ui.viewmodel.AlgorithmOption
    import com.example.cloaktalk.ui.viewmodel.DecryptionResult
    import com.example.cloaktalk.ui.viewmodel.EncryptDecryptViewModel
    import com.example.cloaktalk.ui.viewmodel.EncryptDecryptViewModelFactory

    /**
     * Decrypt Screen Composable - Main screen for message decryption.
     *
     * This screen provides a complete interface for decrypting messages with the following features:
     * - Algorithm selection dropdown (must match the encryption algorithm used)
     * - Encrypted message input field
     * - Decryption key input field (8-digit key from encryption)
     * - Decrypt button with loading state
     * - Decrypted output display
     * - Error handling for invalid/expired keys
     * - Action buttons for decrypting another message or navigating back
     *
     * The screen uses [EncryptDecryptViewModel] to handle:
     * - Loading available algorithms from the database
     * - Validating decryption keys
     * - Performing decryption operations
     * - Storing decryption records in the database
     * - Managing UI state (loading, results)
     *
     * @param onNavigate Callback function for navigation to other screens.
     *                   Called with screen route string (e.g., "home", "encrypt", "history")
     *
     * @see EncryptDecryptViewModel
     * @see EncryptedMessageInputCard
     * @see KeyInputCard
     * @see DecryptedOutputCard
     * @see performDecryption
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DecryptScreen(onNavigate: (String) -> Unit) {
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
        val decryptionResult by viewModel.decryptionResult.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        // Local UI state
        var encryptedMessage by remember { mutableStateOf("") }
        var decryptionKey by remember { mutableStateOf("") }
        var selectedAlgorithm by remember { mutableStateOf<AlgorithmOption?>(null) }
        var showAlgorithmDropdown by remember { mutableStateOf(false) }

        // Auto-select first algorithm when algorithms are loaded
        LaunchedEffect(algorithms) {
            if (algorithms.isNotEmpty() && selectedAlgorithm == null) {
                selectedAlgorithm = algorithms.first()
            }
        }

        Scaffold(
            containerColor = OrangeTheme.Background,
            bottomBar = { BottomNavigation("decrypt", onNavigate) }
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

                    // Algorithm Selection Card
                    DecryptAlgorithmSelectionCard(
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Decrypt Button
                    DecryptButton(
                        enabled = encryptedMessage.isNotEmpty() &&
                                decryptionKey.isNotEmpty() &&
                                selectedAlgorithm != null &&
                                !isLoading,
                        isLoading = isLoading,
                        onClick = {
                            selectedAlgorithm?.let { algorithm ->
                                viewModel.decryptMessage(
                                    encryptedText = encryptedMessage,
                                    key = decryptionKey,
                                    selectedAlgorithm = algorithm
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
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Decryption Result Section
                    decryptionResult?.let { result ->
                        DecryptionResultSection(
                            result = result,
                            onDecryptAnother = {
                                encryptedMessage = ""
                                decryptionKey = ""
                                viewModel.clearDecryptionResult()
                            },
                            onNavigateBack = { onNavigate("home") }
                        )
                    }
                }
            }
        }
    }

    /**
     * Header section for the Decrypt Screen with gradient background.
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
     * Card containing the algorithm selection dropdown for decryption.
     *
     * @param algorithms List of available algorithms
     * @param selectedAlgorithm Currently selected algorithm
     * @param showDropdown Whether the dropdown is expanded
     * @param onExpandedChange Callback when dropdown expansion changes
     * @param onAlgorithmSelected Callback when an algorithm is selected
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DecryptAlgorithmSelectionCard(
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
                    text = "Must match the encryption algorithm",
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
     * @param onNavigateBack Callback to navigate back
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