package com.example.cloaktalk.ui.screens.decrypt

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
 * Card for inputting encrypted message to decrypt
 */
@Composable
fun EncryptedMessageInputCard(
    encryptedMessage: String,
    onEncryptedMessageChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Label for encrypted message input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Encrypted Message",
                    color = OrangeTheme.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "${encryptedMessage.length} chars",
                    color = OrangeTheme.TextSecondary,
                    fontSize = 12.sp
                )
            }
            // Spacer for layout
            Spacer(modifier = Modifier.height(8.dp))

            // Text field for encrypted message input
            OutlinedTextField(
                value = encryptedMessage,
                onValueChange = onEncryptedMessageChange,
                placeholder = { Text("Paste encrypted message here...", color = Color.LightGray) },
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