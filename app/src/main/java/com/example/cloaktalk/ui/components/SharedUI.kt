package com.example.cloaktalk.ui.components


import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme
import com.example.cloaktalk.ui.models.EncryptionHistory
import androidx.compose.foundation.BorderStroke



/**
 * Displays a statistics card with value, label, icon, and optional badge.
 * @param value The main value to display
 * @param label The label for the value
 * @param icon The icon to show
 * @param badge Optional badge text
 * @param modifier Modifier for layout customization
 */
@Composable
fun StatsCard(value: String, label: String, icon: ImageVector, badge: String?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(icon, null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(20.dp))
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .background(OrangeTheme.Success.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(badge, fontSize = 10.sp, color = Color(0xFFD1FAE5), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

/**
 * Displays a quick action card with label and icon, clickable for user actions.
 * @param label The action label
 * @param icon The icon to show
 * @param modifier Modifier for layout customization
 * @param onClick Callback for click event
 */
@Composable
fun QuickActionCard(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(OrangeTheme.Primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = OrangeTheme.Primary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = OrangeTheme.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

/**
 * Displays a recent activity item for encryption history.
 * @param item The encryption history data
 */
@Composable
fun RecentActivityItem(item: EncryptionHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(OrangeTheme.Primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Shield, null, tint = OrangeTheme.Primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.message, color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${item.algorithm} • ${item.date}", color = OrangeTheme.TextSecondary, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = OrangeTheme.TextSecondary)
        }
    }
}

/**
 * Displays a filter chip for selection in lists.
 * @param label The chip label
 * @param selected Whether the chip is selected
 */
@Composable
fun FilterChip(label: String, selected: Boolean) {
    Surface(
        color = if (selected) OrangeTheme.Primary else OrangeTheme.Surface,
        shape = RoundedCornerShape(20.dp),
        border = if (!selected) BorderStroke(1.dp, OrangeTheme.Border) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else OrangeTheme.TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Displays the bottom navigation bar for the app.
 * @param current The current selected screen
 * @param onNavigate Callback for navigation events
 */
@Composable
fun BottomNavigation(current: String, onNavigate: (String) -> Unit) {
    Surface(
        color = OrangeTheme.Surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(Icons.Default.Home, "Home", current == "home") { onNavigate("home") }
            BottomNavItem(Icons.Default.History, "History", current == "history") { onNavigate("history") }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(OrangeTheme.Primary, OrangeTheme.PrimaryDark)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { onNavigate("designer") }) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }

            Box {
                BottomNavItem(Icons.Default.Notifications, "Alerts", current == "notifications") { onNavigate("notifications") }
                if (current != "notifications") {
                    Box(
                        modifier = Modifier
                            .offset(x = 20.dp, y = (-4).dp)
                            .size(16.dp)
                            .background(OrangeTheme.Error, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            BottomNavItem(Icons.Default.Settings, "Settings", current == "settings") { onNavigate("settings") }
        }
    }
}

/**
 * Displays a single item in the bottom navigation bar.
 * @param icon The icon to show
 * @param label The label for the item
 * @param selected Whether the item is selected
 * @param onClick Callback for click event
 */
@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) OrangeTheme.Primary else OrangeTheme.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (selected) OrangeTheme.Primary else OrangeTheme.TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Displays a settings item with icon, title, subtitle, and click action.
 * @param icon The icon to show
 * @param title The title of the setting
 * @param subtitle The subtitle of the setting
 * @param onClick Callback for click event
 */
@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(OrangeTheme.Primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = OrangeTheme.Primary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = OrangeTheme.TextSecondary, fontSize = 12.sp)
            }

            Icon(Icons.Default.ChevronRight, null, tint = OrangeTheme.TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

/**
 * Displays a settings item with a switch for toggling options.
 * @param icon The icon to show
 * @param title The title of the setting
 * @param subtitle The subtitle of the setting
 * @param checked Whether the switch is checked
 * @param onCheckedChange Callback for switch state change
 */
@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(OrangeTheme.Primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = OrangeTheme.Primary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = OrangeTheme.TextSecondary, fontSize = 12.sp)
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = OrangeTheme.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = OrangeTheme.Border
                )
            )
        }
    }
}