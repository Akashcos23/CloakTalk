package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.components.BottomNavigation
import com.example.cloaktalk.ui.components.FilterChip
import com.example.cloaktalk.ui.models.EncryptionHistory
import com.example.cloaktalk.ui.models.sampleHistory
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for displaying encryption history.
 * Shows a searchable list of encrypted messages and their status.
 *
 * @param onNavigate Callback for navigation events
 *
 
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onNavigate: (String) -> Unit) {
    // Holds the current search query entered by the user
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("history", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of history items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // Header section with gradient background
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
                        // Title for history screen
                        Text("Encryption History", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

                        // Spacer for layout separation
                        Spacer(modifier = Modifier.height(16.dp))

                        // Search field for filtering history
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search messages...") },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = OrangeTheme.TextSecondary) },
                            trailingIcon = { Icon(Icons.Default.FilterList, null, tint = OrangeTheme.TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeTheme.Border,
                                unfocusedBorderColor = OrangeTheme.Border,
                                focusedContainerColor = OrangeTheme.Surface,
                                unfocusedContainerColor = OrangeTheme.Surface,
                                focusedTextColor = OrangeTheme.TextPrimary,
                                unfocusedTextColor = OrangeTheme.TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Row of filter chips for history categories
                LazyRow(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { FilterChip("All", true) }
                    item { FilterChip("Active Keys", false) }
                    item { FilterChip("Expired", false) }
                    item { FilterChip("This Week", false) }
                }

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Display each history item from sampleHistory
            items(sampleHistory) { item ->
                HistoryItem(item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Composable for displaying a single encryption history item.
 * Shows message, algorithm, status, date, and key expiry.
 *
 * @param item The encryption history data
 */
@Composable
fun HistoryItem(item: EncryptionHistory) {
    // Card for displaying history item details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Display the encrypted message
                    Text(item.message, color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold)
                    // Spacer for layout separation
                    Spacer(modifier = Modifier.height(4.dp))
                    // Display the algorithm used
                    Text(item.algorithm, color = OrangeTheme.TextSecondary, fontSize = 12.sp)
                }

                // Surface for showing status (Active/Expired)
                Surface(
                    color = if (item.isExpired) OrangeTheme.Error.copy(alpha = 0.2f) else OrangeTheme.Success.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (item.isExpired) OrangeTheme.Error.copy(alpha = 0.3f) else OrangeTheme.Success.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (item.isExpired) "Expired" else "Active",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = if (item.isExpired) OrangeTheme.Error else OrangeTheme.Success,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Spacer for layout separation
            Spacer(modifier = Modifier.height(12.dp))

            // Divider between message and metadata
            Divider(color = OrangeTheme.Border, thickness = 1.dp)

            // Spacer for layout separation
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon and date of encryption
                    Icon(Icons.Default.Schedule, null, tint = OrangeTheme.TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.date, color = OrangeTheme.TextSecondary, fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon and key expiry information
                    Icon(Icons.Default.Key, null, tint = OrangeTheme.TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Expires: ${item.keyExpiry}", color = OrangeTheme.TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}