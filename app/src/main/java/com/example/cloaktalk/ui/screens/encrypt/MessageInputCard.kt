package com.example.cloaktalk.ui.screens.encrypt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Card for inputting message to encrypt
 */
@Composable
fun MessageInputCard(
    plaintext: String,
    onPlaintextChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Label for message input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Message to Encrypt",
                    color = OrangeTheme.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "${plaintext.length} chars",
                    color = OrangeTheme.TextSecondary,
                    fontSize = 12.sp
                )
            }
            // Spacer for layout
            Spacer(modifier = Modifier.height(8.dp))

            // Text field for message input
            OutlinedTextField(
                value = plaintext,
                onValueChange = onPlaintextChange,
                placeholder = { Text("Enter your message here...", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
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