package com.example.cloaktalk.ui.screens.designer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Card for entering algorithm name
 */
@Composable
fun AlgoNameCard(algoName: String, onNameChange: (String) -> Unit) {
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
                onValueChange = onNameChange,
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
}

/**
 * Card for selecting base algorithm
 */
@Composable
fun BaseAlgoCard(baseAlgo: String, onAlgoChange: (String) -> Unit) {
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
            val options = listOf("Caesar Cipher", "Substitution Cipher", "Vigenère Cipher")

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
                        .menuAnchor()
                        .fillMaxWidth(),
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
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onAlgoChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card for algorithm parameters
 */
@Composable
fun AlgoParametersCard(
    shiftAmount: String,
    onShiftChange: (String) -> Unit,
    multipleRounds: Boolean,
    onRoundsChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Label for algorithm parameters section
            Text("Algorithm Parameters", color = OrangeTheme.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            // Spacer for layout
            Spacer(modifier = Modifier.height(12.dp))

            // Label for shift amount parameter
            Text("Shift Amount (for Caesar)", color = OrangeTheme.TextSecondary, fontSize = 12.sp)
            // Spacer for layout
            Spacer(modifier = Modifier.height(4.dp))
            // Text field for shift amount input
            OutlinedTextField(
                value = shiftAmount,
                onValueChange = onShiftChange,
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

            // Spacer for layout
            Spacer(modifier = Modifier.height(16.dp))

            // Switch for multiple encryption rounds option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Multiple Rounds", color = OrangeTheme.TextPrimary, fontSize = 14.sp)
                Switch(
                    checked = multipleRounds,
                    onCheckedChange = onRoundsChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OrangeTheme.Primary,
                        checkedTrackColor = OrangeTheme.Primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = OrangeTheme.TextSecondary,
                        uncheckedTrackColor = OrangeTheme.SurfaceVariant
                    )
                )
            }
        }
    }
}