
// HomeScreen.kt

// This file contains the HomeScreen composable for displaying the main dashboard in CloakTalk.
// All code in this file was contributed by Ahnaf for instructor review.

package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.components.*
import com.example.cloaktalk.ui.models.sampleHistory
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for displaying the main dashboard.
 * Shows stats, quick actions, and recent activity.
 *
 * @param onNavigate Callback for navigation events
 *
 * Author: Ahnaf
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    // Scaffold provides the basic layout structure
    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("home", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of dashboard items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // Header section with gradient background and app info
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // App icon
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                                // Spacer between icon and text
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    // App name
                                    Text("CloakTalk", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    // App subtitle
                                    Text("End-to-end encryption", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }

                            Box {
                                // Notifications icon with badge
                                IconButton(
                                    onClick = { onNavigate("notifications") },
                                    modifier = Modifier.background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                ) {
                                    Icon(Icons.Default.Notifications, null, tint = Color.White)
                                }
                                Box(
                                    modifier = Modifier
                                        .offset(x = 8.dp, y = (-4).dp)
                                        .size(20.dp)
                                        .background(OrangeTheme.Error, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Spacer for layout separation
                        Spacer(modifier = Modifier.height(24.dp))

                        // Row of stats cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatsCard("24", "Encryptions", Icons.Default.Lock, "Active", modifier = Modifier.weight(1f))
                            StatsCard("12", "Active Keys", Icons.Default.Key, null, modifier = Modifier.weight(1f))
                            StatsCard("8", "Algorithms", Icons.Default.Shield, null, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions section title
                Text(
                    "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Row of quick action cards (Encrypt/Decrypt)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard("Encrypt", Icons.Default.Lock, Modifier.weight(1f)) { }
                    QuickActionCard("Decrypt", Icons.Default.LockOpen, Modifier.weight(1f)) { }
                }

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(12.dp))

                // Row of quick action cards (Design Algo/History)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard("Design Algo", Icons.Default.Add, Modifier.weight(1f)) { onNavigate("designer") }
                    QuickActionCard("History", Icons.Default.History, Modifier.weight(1f)) { onNavigate("history") }
                }
            }

            item {
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(24.dp))

                // Recent Activity section title
                Text(
                    "Recent Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeTheme.TextPrimary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Display up to 3 recent activity items
            items(sampleHistory.take(3)) { item ->
                RecentActivityItem(item)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Spacer at the end of the list
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}