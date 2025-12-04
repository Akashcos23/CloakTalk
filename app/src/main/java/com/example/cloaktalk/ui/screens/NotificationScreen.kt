package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.models.AccessNotification
import com.example.cloaktalk.ui.models.sampleNotifications
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for displaying key access notifications.
 * Shows real-time alerts and actions for key events.
 *
 * @param onNavigate Callback for navigation events
 *
 
 */
@Composable
fun NotificationsScreen(onNavigate: (String) -> Unit) {
    // Scaffold provides the basic layout structure
    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("notifications", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of notifications
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
                        // Title for notifications screen
                        Text("Access Notifications", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        // Subtitle for notifications screen
                        Text("Real-time key access alerts", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            // Spacer for layout separation
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Display each notification item from sampleNotifications
            items(sampleNotifications) { notif ->
                NotificationItem(notif)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Card for expiring message notification
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .border(4.dp, OrangeTheme.Primary, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(OrangeTheme.Primary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, null, tint = OrangeTheme.Primary, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            // Title and description for expiring message
                            Text("Message Expiring Soon", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold)
                            Text("\"Meeting at 5 PM\" will self-destruct in 2 hours", color = OrangeTheme.TextSecondary, fontSize = 12.sp)

                            // Spacer for layout separation
                            Spacer(modifier = Modifier.height(8.dp))

                            // Card for time remaining and progress bar
                            Card(
                                colors = CardDefaults.cardColors(containerColor = OrangeTheme.Primary.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Time Remaining", color = OrangeTheme.Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("01:47:32", color = OrangeTheme.Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = 0.75f,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = OrangeTheme.Primary,
                                        trackColor = OrangeTheme.SurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(12.dp))

                // Card for expired key notification
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .border(4.dp, OrangeTheme.Error, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(OrangeTheme.Error.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Schedule, null, tint = OrangeTheme.Error, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            // Title and description for expired key
                            Text("Key Expired", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Key #1234 has expired and is no longer valid", color = OrangeTheme.TextSecondary, fontSize = 12.sp)

                            // Spacer for layout separation
                            Spacer(modifier = Modifier.height(8.dp))

                            // Button to generate a new key
                            TextButton(
                                onClick = { },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Generate New Key", color = OrangeTheme.Primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Spacer at the end of the list
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Composable for displaying a single access notification item.
 * Shows user, key accessed, time, and action buttons.
 *
 * @param notif The access notification data
 */
@Composable
fun NotificationItem(notif: AccessNotification) {
    // Card for displaying notification details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(4.dp, OrangeTheme.Primary, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon for key access
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(OrangeTheme.Primary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Key, null, tint = OrangeTheme.Primary, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        // Display user name
                        Text(notif.user, color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        // Display key accessed
                        Text("accessed ${notif.key}", color = OrangeTheme.TextSecondary, fontSize = 12.sp)
                    }
                }

                // Display time of access
                Text(notif.time, color = OrangeTheme.TextSecondary, fontSize = 10.sp)
            }

            // Spacer for layout separation
            Spacer(modifier = Modifier.height(12.dp))

            // Row of action buttons (Approve/Revoke)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Success.copy(alpha = 0.2f),
                        contentColor = OrangeTheme.Success
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeTheme.Error.copy(alpha = 0.2f),
                        contentColor = OrangeTheme.Error
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Revoke", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}