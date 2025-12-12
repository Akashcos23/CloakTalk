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
 * Card for inputting decryption key
 */
@Composable
fun KeyInputCard(
    decryptionKey: String,
    onKeyChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Label for decryption key input
            Text(
                "Decryption Key",
                color = OrangeTheme.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            // Spacer for layout
            Spacer(modifier = Modifier.height(8.dp))

            // Text field for key input
            OutlinedTextField(
                value = decryptionKey,
                onValueChange = onKeyChange,
                placeholder = { Text("Enter the decryption key...", color = Color.LightGray) },
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

            Spacer(modifier = Modifier.height(8.dp))

            // Hint text
            Text(
                "Enter the exact key that was used for encryption",
                color = OrangeTheme.TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}