
// SettingsScreen.kt

// This file contains the SettingsScreen composable for managing user settings in CloakTalk.

package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.cloaktalk.ui.components.SettingsItem
import com.example.cloaktalk.ui.components.SettingsSwitchItem
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for managing user settings.
 * Allows users to update account, security, notifications, and view app info.
 *
 * @param onNavigate Callback for navigation events
 *

 */
@Composable
fun SettingsScreen(onNavigate: (String) -> Unit) {
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
                // Account settings items
                SettingsItem(Icons.Default.Person, "Profile", "Edit your personal information") { }
                SettingsItem(Icons.Default.Email, "Email", "user@example.com") { }
                SettingsItem(Icons.Default.Lock, "Change Password", "Update your password") { }
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
                SettingsItem(Icons.Default.Key, "Manage Keys", "View and manage encryption keys") { }
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
                    onClick = { onNavigate("login") },
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