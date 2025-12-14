
package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloaktalk.data.local.database.CloakTalkDatabase
import com.example.cloaktalk.data.local.entity.DecryptMessageEntity
import com.example.cloaktalk.data.local.entity.EncryptMessageEntity
import com.example.cloaktalk.data.local.entity.KeyEntity
import com.example.cloaktalk.data.repository.DecryptMessageRepository
import com.example.cloaktalk.data.repository.EncryptMessageRepository
import com.example.cloaktalk.data.repository.HistoryRepository
import com.example.cloaktalk.data.repository.KeyRepository
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.theme.OrangeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing an encrypted message with its associated key info.
 *
 * @property encryptId Unique identifier for the encrypted message
 * @property originalMessage The original plaintext message
 * @property encryptedMessage The encrypted ciphertext
 * @property algorithmName The algorithm used for encryption
 * @property key The 8-digit decryption key
 * @property isKeyActive Whether the key is still valid/active
 * @property keyExpireTime Timestamp when the key expires
 * @property createdAt Timestamp when the message was encrypted
 */
data class EncryptedMessageWithKey(
    val encryptId: Long,
    val originalMessage: String,
    val encryptedMessage: String,
    val algorithmName: String,
    val key: String,
    val isKeyActive: Boolean,
    val keyExpireTime: Long,
    val createdAt: Long
)

/**
 * Data class representing a decrypted message record.
 *
 * @property decryptId Unique identifier for the decryption record
 * @property encryptedMessage The encrypted message that was decrypted
 * @property decryptedMessage The resulting decrypted plaintext
 * @property key The key used for decryption
 * @property createdAt Timestamp when decryption occurred
 */
data class DecryptedMessageRecord(
    val decryptId: Long,
    val encryptedMessage: String,
    val decryptedMessage: String,
    val key: String,
    val createdAt: Long
)

/**
 * ViewModel for managing history screen state and data operations.
 *
 * Responsibilities:
 * - Loading encrypted messages with their key information for the current user
 * - Loading decrypted messages (only those with active keys) for the current user
 * - Managing loading and error states
 * - Providing formatted data for the UI
 */
class HistoryViewModel(
    private val encryptMessageRepository: EncryptMessageRepository,
    private val decryptMessageRepository: DecryptMessageRepository,
    private val keyRepository: KeyRepository,
    private val historyRepository: HistoryRepository,
    private val userId: Long
) : ViewModel() {

    private val _encryptedMessages = MutableStateFlow<List<EncryptedMessageWithKey>>(emptyList())
    val encryptedMessages: StateFlow<List<EncryptedMessageWithKey>> = _encryptedMessages.asStateFlow()

    private val _decryptedMessages = MutableStateFlow<List<DecryptedMessageRecord>>(emptyList())
    val decryptedMessages: StateFlow<List<DecryptedMessageRecord>> = _decryptedMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadHistory()
    }

    /**
     * Loads all history data from the database.
     * Fetches encrypted messages with their keys and decrypted messages.
     */
    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Deactivate expired keys first
                keyRepository.deactivateExpiredKeys()

                // Load encrypted messages with key info
                loadEncryptedMessages()

                // Load decrypted messages (only with active keys)
                loadDecryptedMessages()

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads all encrypted messages and joins them with their key information.
     * Only loads messages belonging to the current user.
     */
    private suspend fun loadEncryptedMessages() {
        val encryptedList = mutableListOf<EncryptedMessageWithKey>()

        // Get history records for current user
        val userHistory = historyRepository.getHistoryByUserId(userId)
        
        // Get unique encryptIds from user's history
        val userEncryptIds = userHistory.map { it.encryptId }.toSet()

        // Get all encrypted messages from repository and filter by user's history
        val allEncrypted = encryptMessageRepository.getAllEncryptedMessages()
            .filter { it.encryptId in userEncryptIds }

        for (encrypted in allEncrypted) {
            // Get the associated key
            val keyEntity = keyRepository.getKeyById(encrypted.keyId)

            if (keyEntity != null) {
                encryptedList.add(
                    EncryptedMessageWithKey(
                        encryptId = encrypted.encryptId,
                        originalMessage = encrypted.originalMessage,
                        encryptedMessage = encrypted.encryptedMessage,
                        algorithmName = encrypted.algorithmName,
                        key = keyEntity.key,
                        isKeyActive = keyEntity.isActive && keyEntity.keyExpire > System.currentTimeMillis(),
                        keyExpireTime = keyEntity.keyExpire,
                        createdAt = encrypted.createdAt
                    )
                )
            }
        }

        // Sort by creation time (newest first)
        _encryptedMessages.value = encryptedList.sortedByDescending { it.createdAt }
    }

    /**
     * Loads decrypted messages, filtering to only show those with active keys.
     * Only loads messages belonging to the current user.
     */
    private suspend fun loadDecryptedMessages() {
        val decryptedList = mutableListOf<DecryptedMessageRecord>()

        // Get history records for current user
        val userHistory = historyRepository.getHistoryByUserId(userId)
        
        // Get unique decryptIds from user's history (filter out nulls)
        val userDecryptIds = userHistory.mapNotNull { it.decryptId }.toSet()

        // Get all decrypted messages from repository and filter by user's history
        val allDecrypted = decryptMessageRepository.getAllDecryptedMessages()
            .filter { it.decryptId in userDecryptIds }

        for (decrypted in allDecrypted) {
            // Get the associated key
            val keyEntity = keyRepository.getKeyById(decrypted.keyId)

            // Only include if key exists and is active
            if (keyEntity != null && keyEntity.isActive && keyEntity.keyExpire > System.currentTimeMillis()) {
                decryptedList.add(
                    DecryptedMessageRecord(
                        decryptId = decrypted.decryptId,
                        encryptedMessage = decrypted.encryptedMessage,
                        decryptedMessage = decrypted.decryptedMessage,
                        key = keyEntity.key,
                        createdAt = decrypted.createdAt
                    )
                )
            }
        }

        // Sort by creation time (newest first)
        _decryptedMessages.value = decryptedList.sortedByDescending { it.createdAt }
    }

    /**
     * Refreshes the history data.
     */
    fun refresh() {
        loadHistory()
    }

    /**
     * Clears any error message.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

/**
 * Factory for creating HistoryViewModel with required dependencies.
 */
class HistoryViewModelFactory(
    private val encryptMessageRepository: EncryptMessageRepository,
    private val decryptMessageRepository: DecryptMessageRepository,
    private val keyRepository: KeyRepository,
    private val historyRepository: HistoryRepository,
    private val userId: Long
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                encryptMessageRepository,
                decryptMessageRepository,
                keyRepository,
                historyRepository,
                userId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * History Screen Composable - Displays encryption and decryption history.
 *
 * This screen shows two sections:
 * 1. **Encrypted Messages**: All encrypted messages with their keys, status, and original text
 * 2. **Decrypted Messages**: Only decrypted messages where the key is still active
 *
 * @param onNavigate Navigation callback for screen transitions
 * @param userId The ID of the currently logged-in user
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigate: (String) -> Unit,
    userId: Long
) {
    val context = LocalContext.current
    val database = remember { CloakTalkDatabase.getInstance(context) }

    // Use userId as key to ensure a new ViewModel is created when user changes
    val viewModel: HistoryViewModel = viewModel(
        key = "history_viewmodel_$userId",
        factory = HistoryViewModelFactory(
            EncryptMessageRepository(database.encryptMessageDao()),
            DecryptMessageRepository(database.decryptMessageDao()),
            KeyRepository(database.keyDao()),
            HistoryRepository(database.historyDao()),
            userId
        )
    )

    val encryptedMessages by viewModel.encryptedMessages.collectAsState()
    val decryptedMessages by viewModel.decryptedMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Track which section is expanded
    var encryptedExpanded by remember { mutableStateOf(true) }
    var decryptedExpanded by remember { mutableStateOf(true) }

    // Refresh data when screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    Scaffold(
        containerColor = OrangeTheme.Background,
        bottomBar = {
            BottomNavigation("history") { destination ->
                onNavigate(destination)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header
            item {
                HistoryHeader()
            }

            // Error message display
            if (errorMessage != null) {
                item {
                    ErrorCard(
                        message = errorMessage!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }

            // Loading indicator
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangeTheme.Primary)
                    }
                }
            }

            // Encrypted Messages Section
            item {
                SectionHeader(
                    title = "Encrypted Messages",
                    count = encryptedMessages.size,
                    isExpanded = encryptedExpanded,
                    onToggle = { encryptedExpanded = !encryptedExpanded },
                    icon = Icons.Default.Lock
                )
            }

            if (encryptedExpanded) {
                if (encryptedMessages.isEmpty() && !isLoading) {
                    item {
                        EmptyStateCard(message = "No encrypted messages yet")
                    }
                } else {
                    items(encryptedMessages, key = { it.encryptId }) { message ->
                        EncryptedMessageCard(message = message)
                    }
                }
            }

            // Spacer between sections
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Decrypted Messages Section
            item {
                SectionHeader(
                    title = "Decrypted Messages",
                    count = decryptedMessages.size,
                    isExpanded = decryptedExpanded,
                    onToggle = { decryptedExpanded = !decryptedExpanded },
                    icon = Icons.Default.LockOpen
                )
            }

            if (decryptedExpanded) {
                if (decryptedMessages.isEmpty() && !isLoading) {
                    item {
                        EmptyStateCard(message = "No active decrypted messages")
                    }
                } else {
                    items(decryptedMessages, key = { it.decryptId }) { message ->
                        DecryptedMessageCard(message = message)
                    }
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Header component for the History Screen.
 */
@Composable
private fun HistoryHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(OrangeTheme.Primary, OrangeTheme.PrimaryVariant),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 300f)
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "History",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "View your encryption and decryption history",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Section header with expand/collapse functionality.
 */
@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OrangeTheme.Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OrangeTheme.TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = OrangeTheme.Primary
                ) {
                    Text(
                        text = count.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = OrangeTheme.Primary
            )
        }
    }
}

/**
 * Card displaying an encrypted message with its key information.
 */
@Composable
private fun EncryptedMessageCard(message: EncryptedMessageWithKey) {
    val clipboardManager = LocalClipboardManager.current
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with algorithm and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = OrangeTheme.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.algorithmName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = OrangeTheme.Primary
                    )
                }
                KeyStatusBadge(isActive = message.isKeyActive)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Original Message
            MessageField(
                label = "Original Message",
                value = message.originalMessage,
                onCopy = { clipboardManager.setText(AnnotatedString(message.originalMessage)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Encrypted Message
            MessageField(
                label = "Encrypted Message",
                value = message.encryptedMessage,
                onCopy = { clipboardManager.setText(AnnotatedString(message.encryptedMessage)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Key and Expiration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Key
                Column {
                    Text(
                        text = "Key",
                        fontSize = 12.sp,
                        color = OrangeTheme.TextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = message.key,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangeTheme.TextPrimary
                        )
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(message.key)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy key",
                                tint = OrangeTheme.Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Expiration
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (message.isKeyActive) "Expires" else "Expired",
                        fontSize = 12.sp,
                        color = OrangeTheme.TextSecondary
                    )
                    Text(
                        text = dateFormat.format(Date(message.keyExpireTime)),
                        fontSize = 12.sp,
                        color = if (message.isKeyActive) OrangeTheme.TextPrimary else Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Created timestamp
            Text(
                text = "Created: ${dateFormat.format(Date(message.createdAt))}",
                fontSize = 11.sp,
                color = OrangeTheme.TextSecondary
            )
        }
    }
}

/**
 * Card displaying a decrypted message record.
 */
@Composable
private fun DecryptedMessageCard(message: DecryptedMessageRecord) {
    val clipboardManager = LocalClipboardManager.current
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Decrypted",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Encrypted Message (input)
            MessageField(
                label = "Encrypted Input",
                value = message.encryptedMessage,
                onCopy = { clipboardManager.setText(AnnotatedString(message.encryptedMessage)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Decrypted Message (output)
            MessageField(
                label = "Decrypted Output",
                value = message.decryptedMessage,
                onCopy = { clipboardManager.setText(AnnotatedString(message.decryptedMessage)) },
                highlight = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Key used
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Key used: ",
                    fontSize = 12.sp,
                    color = OrangeTheme.TextSecondary
                )
                Text(
                    text = message.key,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary
                )
                IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(message.key)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy key",
                        tint = OrangeTheme.Primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Created timestamp
            Text(
                text = "Decrypted: ${dateFormat.format(Date(message.createdAt))}",
                fontSize = 11.sp,
                color = OrangeTheme.TextSecondary
            )
        }
    }
}

/**
 * Reusable message field component with copy functionality.
 */
@Composable
private fun MessageField(
    label: String,
    value: String,
    onCopy: () -> Unit,
    highlight: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = OrangeTheme.TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = if (highlight) OrangeTheme.Primary.copy(alpha = 0.1f) else OrangeTheme.Background
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = OrangeTheme.TextPrimary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = OrangeTheme.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Badge showing key active/expired status.
 */
@Composable
private fun KeyStatusBadge(isActive: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
    ) {
        Text(
            text = if (isActive) "Active" else "Expired",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) Color(0xFF4CAF50) else Color.Red,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Empty state card when no items exist.
 */
@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = OrangeTheme.TextSecondary
            )
        }
    }
}

/**
 * Error card for displaying error messages.
 */
@Composable
private fun ErrorCard(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.Red
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.Red
                )
            }
        }
    }
}
