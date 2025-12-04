package com.example.cloaktalk.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable screen for designing custom encryption algorithms.
 * Allows users to specify algorithm name, base algorithm, key settings, advanced options, and test messages.
 *
 * @param onNavigate Callback for navigation events
 *
 
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerScreen(onNavigate: (String) -> Unit) {
    // Holds the algorithm name entered by the user
    var algoName by remember { mutableStateOf("") }
    // Holds the selected base algorithm
    var baseAlgo by remember { mutableStateOf("Caesar Cipher") }
    // Holds the shift amount for algorithms that use it
    var shiftAmount by remember { mutableStateOf("") }
    // Holds the custom key entered by the user
    var customKey by remember { mutableStateOf("") }
    // Holds the key expiration time in minutes
    var keyExpiration by remember { mutableStateOf("") }
    // Holds the test message for encryption
    var testMessage by remember { mutableStateOf("") }

    Scaffold(
        // Set the background color and bottom navigation bar
        containerColor = OrangeTheme.Background,
        bottomBar = { BottomNavigation("designer", onNavigate) }
    ) { padding ->
        // Use LazyColumn for vertical scrolling of designer options
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
                        // Title for designer screen
                        Text("Encryption Designer", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        // Subtitle for designer screen
                        Text("Create your custom algorithm", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            item {
                // Spacer for layout separation
                Spacer(modifier = Modifier.height(24.dp))

                // Card for entering algorithm name
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Label for algorithm name
                        Text("Algorithm Name", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        // Spacer for layout
                        Spacer(modifier = Modifier.height(8.dp))
                        // Text field for algorithm name input
                        OutlinedTextField(
                            value = algoName,
                            onValueChange = { algoName = it },
                            placeholder = { Text("e.g., My Super Cipher", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeTheme.Primary,
                                unfocusedBorderColor = OrangeTheme.Border,
                                focusedContainerColor = OrangeTheme.SurfaceVariant,
                                unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                                focusedTextColor = OrangeTheme.TextPrimary,
                                unfocusedTextColor = OrangeTheme.TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Spacer for layout separation
                Spacer(modifier = Modifier.height(16.dp))

                // Card for selecting base algorithm
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Label for base algorithm
                        Text("Base Algorithm", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        // Spacer for layout
                        Spacer(modifier = Modifier.height(8.dp))

                        // Dropdown for base algorithm options
                        var expanded by remember { mutableStateOf(false) }
                        val options = listOf("Caesar Cipher", "Substitution Cipher", "Vigenère Cipher", "Custom from Scratch")

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = baseAlgo,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangeTheme.Primary,
                                    unfocusedBorderColor = OrangeTheme.Border,
                                    focusedContainerColor = OrangeTheme.SurfaceVariant,
                                    unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                                    focusedTextColor = OrangeTheme.TextPrimary,
                                    unfocusedTextColor = OrangeTheme.TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { option ->
                                    // Dropdown menu item for each algorithm option
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            baseAlgo = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Key Settings", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Shift Amount", color = OrangeTheme.TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = shiftAmount,
                            onValueChange = { shiftAmount = it },
                            placeholder = { Text("13", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeTheme.Primary,
                                unfocusedBorderColor = OrangeTheme.Border,
                                focusedContainerColor = OrangeTheme.SurfaceVariant,
                                unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                                focusedTextColor = OrangeTheme.TextPrimary,
                                unfocusedTextColor = OrangeTheme.TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Custom Key", color = OrangeTheme.TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = customKey,
                            onValueChange = { customKey = it },
                            placeholder = { Text("Enter secret key", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeTheme.Primary,
                                unfocusedBorderColor = OrangeTheme.Border,
                                focusedContainerColor = OrangeTheme.SurfaceVariant,
                                unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                                focusedTextColor = OrangeTheme.TextPrimary,
                                unfocusedTextColor = OrangeTheme.TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Key Expiration (minutes)", color = OrangeTheme.TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = keyExpiration,
                            onValueChange = { keyExpiration = it },
                            placeholder = { Text("60", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeTheme.Primary,
                                unfocusedBorderColor = OrangeTheme.Border,
                                focusedContainerColor = OrangeTheme.SurfaceVariant,
                                unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                                focusedTextColor = OrangeTheme.TextPrimary,
                                unfocusedTextColor = OrangeTheme.TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Advanced Options", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        var multipleRounds by remember { mutableStateOf(false) }
                        var includeNumbers by remember { mutableStateOf(true) }
                        var includeSymbols by remember { mutableStateOf(false) }
                        var notifyAccess by remember { mutableStateOf(true) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("Multiple Rounds", color = OrangeTheme.TextPrimary, fontSize = 14.sp)
                            Switch(
                                checked = multipleRounds,
                                onCheckedChange = { multipleRounds = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrangeTheme.Primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("Include Numbers", color = OrangeTheme.TextPrimary, fontSize = 14.sp)
                            Switch(
                                checked = includeNumbers,
                                onCheckedChange = { includeNumbers = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrangeTheme.Primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("Include Symbols", color = OrangeTheme.TextPrimary, fontSize = 14.sp)
                            Switch(
                                checked = includeSymbols,
                                onCheckedChange = { includeSymbols = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrangeTheme.Primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("Notify on Key Access", color = OrangeTheme.TextPrimary, fontSize = 14.sp)
                            Switch(
                                checked = notifyAccess,
                                onCheckedChange = { notifyAccess = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OrangeTheme.Primary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Test Your Algorithm", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = testMessage,
                            onValueChange = { testMessage = it },
                            placeholder = { Text("Enter test message...", color = Color.LightGray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangeTheme.Primary,
                                unfocusedBorderColor = OrangeTheme.Border,
                                focusedContainerColor = OrangeTheme.SurfaceVariant,
                                unfocusedContainerColor = OrangeTheme.SurfaceVariant,
                                focusedTextColor = OrangeTheme.TextPrimary,
                                unfocusedTextColor = OrangeTheme.TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangeTheme.Primary.copy(alpha = 0.2f),
                                contentColor = OrangeTheme.Primary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Test Encryption", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigate("home") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OrangeTheme.TextSecondary
                        ),
                        border = BorderStroke(1.dp, OrangeTheme.Border),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeTheme.Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Algorithm", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}