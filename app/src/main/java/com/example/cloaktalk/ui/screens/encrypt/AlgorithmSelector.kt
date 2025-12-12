package com.example.cloaktalk.ui.screens.encrypt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Card for selecting encryption algorithm
 */
@Composable
fun AlgorithmSelector(
    selectedAlgorithm: String,
    onAlgorithmChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Label for algorithm selection
            Text(
                "Select Algorithm",
                color = OrangeTheme.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            // Spacer for layout
            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for algorithm options
            var expanded by remember { mutableStateOf(false) }
            val options = listOf("Caesar Cipher", "Vigenère Cipher", "Substitution Cipher")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedAlgorithm,
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
                                onAlgorithmChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Description of selected algorithm
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                getAlgorithmDescription(selectedAlgorithm),
                color = OrangeTheme.TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Get description for selected algorithm
 */
private fun getAlgorithmDescription(algorithm: String): String {
    return when (algorithm) {
        "Caesar Cipher" -> "Simple shift cipher - each character shifts by a fixed amount"
        "Vigenère Cipher" -> "Polyalphabetic cipher - uses a repeating key for secure encryption"
        "Substitution Cipher" -> "Character substitution cipher - each character maps to another unique character"
        else -> "Select an algorithm to start encrypting"
    }
}