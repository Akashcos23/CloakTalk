
// SettingsScreen.kt

// This file contains the SettingsScreen composable for managing user settings in CloakTalk.

package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloaktalk.data.local.database.CloakTalkDatabase
import com.example.cloaktalk.data.repository.KeyRepository
import com.example.cloaktalk.data.repository.UserRepository
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.components.SettingsItem
import com.example.cloaktalk.ui.components.SettingsSwitchItem
import com.example.cloaktalk.ui.theme.OrangeTheme
import com.example.cloaktalk.ui.viewmodels.KeyDisplayItem
import com.example.cloaktalk.ui.viewmodels.SettingsViewModel
import com.example.cloaktalk.ui.viewmodels.SettingsViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable screen for managing user settings.
 * Allows users to update account, security, notifications, and view app info.
 *
 * @param onNavigate Callback for navigation events
 * @param userId The ID of the currently logged-in user
 * @param onLogout Callback when user logs out (clears session)
 *
 */
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    userId: Long,
    onLogout: () -> Unit
) {
    // Database and repository setup
    val context = LocalContext.current
    val database = remember { CloakTalkDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(database.userDao()) }
    val keyRepository = remember { KeyRepository(database.keyDao()) }

    // ViewModel with factory
    val viewModel: SettingsViewModel = viewModel(
        key = "settings_viewmodel_$userId",
        factory = SettingsViewModelFactory(userRepository, keyRepository, userId)
    )

    // Collect state from ViewModel
    val userEmail by viewModel.userEmail.collectAsState()
    val passwordChangeState by viewModel.passwordChangeState.collectAsState()
    val userKeys by viewModel.userKeys.collectAsState()
    val isLoadingKeys by viewModel.isLoadingKeys.collectAsState()

    // Dialog state for password change
    var showPasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Dialog state for manage keys
    var showManageKeysDialog by remember { mutableStateOf(false) }

    // Date formatter for display
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    // Handle password change state changes
    LaunchedEffect(passwordChangeState) {
        if (passwordChangeState.isSuccess) {
            showPasswordDialog = false
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
            viewModel.resetPasswordChangeState()
        }
    }

    // Password Change Dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordDialog = false
                currentPassword = ""
                newPassword = ""
                confirmPassword = ""
                viewModel.resetPasswordChangeState()
            },
            title = {
                Text("Change Password", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Error message
                    if (passwordChangeState.errorMessage != null) {
                        Text(
                            text = passwordChangeState.errorMessage!!,
                            color = OrangeTheme.Error,
                            fontSize = 12.sp
                        )
                    }

                    // Password mismatch warning
                    if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                        Text(
                            text = "Passwords do not match",
                            color = OrangeTheme.Error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            viewModel.changePassword(currentPassword, newPassword)
                        }
                    },
                    enabled = !passwordChangeState.isLoading &&
                            currentPassword.isNotEmpty() &&
                            newPassword.isNotEmpty() &&
                            newPassword == confirmPassword,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Primary
                    )
                ) {
                    if (passwordChangeState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Change")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPasswordDialog = false
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                        viewModel.resetPasswordChangeState()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Manage Keys Dialog
    if (showManageKeysDialog) {
        Dialog(
            onDismissRequest = { showManageKeysDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = OrangeTheme.Surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Manage Keys",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangeTheme.TextPrimary
                        )
                        IconButton(onClick = { showManageKeysDialog = false }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = OrangeTheme.TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Deactivating a key will prevent anyone from decrypting messages encrypted with that key.",
                        fontSize = 12.sp,
                        color = OrangeTheme.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoadingKeys) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangeTheme.Primary)
                        }
                    } else if (userKeys.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Key,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = OrangeTheme.TextSecondary.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No keys found",
                                    color = OrangeTheme.TextSecondary
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(userKeys) { keyItem ->
                                KeyManagementItem(
                                    keyItem = keyItem,
                                    dateFormat = dateFormat,
                                    onDeactivate = { viewModel.deactivateKey(keyItem.keyId) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Close button
                    Button(
                        onClick = { showManageKeysDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeTheme.Primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }

    // Scaffold provides the basic layout structure
    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("settings", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of settings items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // Header section with gradient background and title
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
                        // Title for settings screen
                        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        // Subtitle for settings screen
                        Text("Manage your account and preferences", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            // Spacer for layout separation
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                // Account section title
                Text(
                    "Account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Account settings items - removed Profile, showing real email
                SettingsItem(Icons.Default.Email, "Email", userEmail.ifEmpty { "Loading..." }) { }
                SettingsItem(Icons.Default.Lock, "Change Password", "Update your password") { 
                    showPasswordDialog = true 
                }
            }

            // Spacer for layout separation
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                // Security section title
                Text(
                    "Security",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // State for biometric and two-factor authentication toggles
                var biometricEnabled by remember { mutableStateOf(true) }
                var twoFactorEnabled by remember { mutableStateOf(false) }

               /* SettingsSwitchItem(
                    Icons.Default.Fingerprint,
                    "Biometric Authentication",
                    "Use fingerprint or face unlock",
                    biometricEnabled
                ) { biometricEnabled = it }

                SettingsSwitchItem(
                    Icons.Default.Security,
                    "Two-Factor Authentication",
                    "Add extra security layer",
                    twoFactorEnabled
                ) { twoFactorEnabled = it }
*/
                // Key management settings item
                SettingsItem(Icons.Default.Key, "Manage Keys", "View and manage encryption keys") {
                    viewModel.loadUserKeys()
                    showManageKeysDialog = true
                }
            }

            // Spacer for layout separation
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                // Notifications section title
                Text(
                    "Notifications",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // State for notification toggles
                var pushNotifications by remember { mutableStateOf(true) }
                var keyAccessAlerts by remember { mutableStateOf(true) }
                var expirationReminders by remember { mutableStateOf(true) }

                // Push notifications toggle
                SettingsSwitchItem(
                    Icons.Default.Notifications,
                    "Push Notifications",
                    "Receive app notifications",
                    pushNotifications
                ) { pushNotifications = it }

                // Key access alerts toggle
                SettingsSwitchItem(
                    Icons.Default.Warning,
                    "Key Access Alerts",
                    "Get notified when keys are accessed",
                    keyAccessAlerts
                ) { keyAccessAlerts = it }

                // Expiration reminders toggle
                SettingsSwitchItem(
                    Icons.Default.Schedule,
                    "Expiration Reminders",
                    "Remind me before keys expire",
                    expirationReminders
                ) { expirationReminders = it }
            }

            // Spacer for layout separation
            item { Spacer(modifier = Modifier.height(24.dp)) }

           /* item {
                Text(
                    "Appearance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                SettingsItem(Icons.Default.Palette, "Theme", "Dark Mode") { }
                SettingsItem(Icons.Default.Language, "Language", "English") { }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
*/
            item {
                // About section title
                Text(
                    "About",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // About and legal info settings items
                SettingsItem(Icons.Default.Info, "App Version", "1.0.0") { }
                SettingsItem(Icons.Default.Description, "Privacy Policy", "View privacy policy") { }
                SettingsItem(Icons.Default.Article, "Terms of Service", "Read terms and conditions") { }
                //SettingsItem(Icons.Default.Help, "Help & Support", "Get help with the app") { }
            }

            // Spacer for layout separation
            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                // Logout button
                Button(
                    onClick = { onLogout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Error.copy(alpha = 0.2f),
                        contentColor = OrangeTheme.Error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }

                // Spacer at the end of the list
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Composable for displaying a single key item in the Manage Keys dialog.
 * Shows key details and allows deactivation of active keys.
 */
@Composable
private fun KeyManagementItem(
    keyItem: KeyDisplayItem,
    dateFormat: SimpleDateFormat,
    onDeactivate: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Confirmation dialog for deactivating a key
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Deactivate Key?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to deactivate this key? Messages encrypted with this key will no longer be decryptable by anyone.",
                    color = OrangeTheme.TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeactivate()
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Error
                    )
                ) {
                    Text("Deactivate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (keyItem.isActive) 
                OrangeTheme.Primary.copy(alpha = 0.1f) 
            else 
                OrangeTheme.TextSecondary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Key value
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (keyItem.isActive) OrangeTheme.Primary else OrangeTheme.TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = keyItem.key,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = OrangeTheme.TextPrimary
                    )
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (keyItem.isActive) 
                        Color(0xFF22C55E).copy(alpha = 0.2f)
                    else 
                        OrangeTheme.Error.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (keyItem.isActive) "Active" else "Inactive",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (keyItem.isActive) Color(0xFF22C55E) else OrangeTheme.Error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Algorithm
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = OrangeTheme.TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Algorithm: ${keyItem.algorithmName}",
                    fontSize = 12.sp,
                    color = OrangeTheme.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Created date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = OrangeTheme.TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Created: ${dateFormat.format(Date(keyItem.createdAt))}",
                    fontSize = 12.sp,
                    color = OrangeTheme.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Expiration date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = if (keyItem.expiresAt < System.currentTimeMillis()) 
                        OrangeTheme.Error 
                    else 
                        OrangeTheme.TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (keyItem.expiresAt < System.currentTimeMillis())
                        "Expired: ${dateFormat.format(Date(keyItem.expiresAt))}"
                    else
                        "Expires: ${dateFormat.format(Date(keyItem.expiresAt))}",
                    fontSize = 12.sp,
                    color = if (keyItem.expiresAt < System.currentTimeMillis()) 
                        OrangeTheme.Error 
                    else 
                        OrangeTheme.TextSecondary
                )
            }

            // Deactivate button (only for active keys)
            if (keyItem.isActive) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Error.copy(alpha = 0.2f),
                        contentColor = OrangeTheme.Error
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Block,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deactivate Key", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}