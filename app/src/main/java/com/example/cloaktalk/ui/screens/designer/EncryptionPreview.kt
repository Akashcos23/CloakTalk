package com.example.cloaktalk.ui.screens.designer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable displaying live cipher preview
 */
@Composable
fun EncryptionPreviewCard(
    testMessage: String,
    onTestMessageChange: (String) -> Unit,
    encryptedPreview: String
) {
    // Check if preview is an error/warning message
    val isErrorMessage = encryptedPreview.startsWith("⚠️")
    val validEncryptedText = if (isErrorMessage) "" else encryptedPreview

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Live Cipher Preview",
                color = OrangeTheme.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = testMessage,
                onValueChange = onTestMessageChange,
                placeholder = { Text("Type to see instant encryption...", color = Color.LightGray) },
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

            AnimatedVisibility(visible = testMessage.isNotEmpty()) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = OrangeTheme.Border
                        )
                        Text(
                            "  →  ",
                            color = OrangeTheme.Primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = OrangeTheme.Border
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show error message or encrypted output
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isErrorMessage) Color(0xFFEF4444).copy(alpha = 0.1f)
                                else OrangeTheme.Primary.copy(alpha = 0.1f)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = encryptedPreview.ifEmpty { "Encrypted text appears here..." },
                            color = if (isErrorMessage) Color(0xFFEF4444) else OrangeTheme.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Character count - shows 0 for error messages
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Original: ${testMessage.length} chars",
                            color = OrangeTheme.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            "Encrypted: ${validEncryptedText.length} chars",
                            color = OrangeTheme.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Preview encryption based on current configuration
 * Implements different encryption algorithms based on baseAlgo parameter
 */
fun previewEncryption(
    plaintext: String,
    baseAlgo: String,
    shiftAmount: Int,
    multipleRounds: Boolean,
    charset: Set<Char>
): String {
    // Validation: require at least 20 characters
    if (charset.size < 20) {
        return "⚠️ Select at least 20 characters to enable encryption"
    }

    // Convert charset to list for indexing
    val charList = charset.toList()

    // Normalize input: convert to case that exists in charset
    val hasLowercase = charList.any { it in 'a'..'z' }
    val hasUppercase = charList.any { it in 'A'..'Z' }
    val hasNumbers = charList.any { it in '0'..'9' }
    val hasSymbols = charList.any { it in "!@#$%^&*()_+-=[]{}|;:,.<>?/~`" }
    val hasSpace = charList.contains(' ')

    // Smart normalization: convert letters to available case, keep numbers/symbols
    val normalizedPlaintext = plaintext.map { char ->
        when {
            // Handle letters
            char in 'a'..'z' -> {
                when {
                    hasLowercase -> char
                    hasUppercase -> char.uppercaseChar()
                    else -> null // Remove letters if no letter cases selected
                }
            }
            char in 'A'..'Z' -> {
                when {
                    hasUppercase -> char
                    hasLowercase -> char.lowercaseChar()
                    else -> null // Remove letters if no letter cases selected
                }
            }
            // Keep numbers and symbols as-is
            char in '0'..'9' -> if (hasNumbers) char else null
            char in "!@#$%^&*()_+-=[]{}|;:,.<>?/~`" -> if (hasSymbols) char else null
            char == ' ' -> if (hasSpace) char else null
            // Remove whitespace and other characters
            else -> null
        }
    }.filterNotNull().joinToString("")

    // Filter to only characters that exist in charset
    val filteredPlaintext = normalizedPlaintext.filter { it in charList }

    // If nothing remains after filtering, show message
    if (filteredPlaintext.isEmpty()) {
        return "⚠️ No valid characters in message for selected charset"
    }

    // Branch encryption logic based on selected algorithm
    var result = when (baseAlgo) {
        // Caesar Cipher: Simple shift cipher where each character shifts by fixed amount
        "Caesar Cipher" -> {
            filteredPlaintext.map { char ->
                val index = charList.indexOf(char)
                charList[(index + shiftAmount) % charList.size]
            }.joinToString("")
        }

        // Substitution Cipher: Uses character shuffling/permutation for mapping
        "Substitution Cipher" -> {
            val shuffledList = charList.shuffled(kotlin.random.Random(shiftAmount.toLong()))
            filteredPlaintext.map { char ->
                val index = charList.indexOf(char)
                shuffledList[index]
            }.joinToString("")
        }

        // Vigenère Cipher: Uses repeating key where each position uses different shift
        "Vigenère Cipher" -> {
            val keyShiftValue = shiftAmount % charList.size
            filteredPlaintext.mapIndexed { index, char ->
                val charIndex = charList.indexOf(char)
                val positionShift = (keyShiftValue + (index % 5)) % charList.size
                charList[(charIndex + positionShift) % charList.size]
            }.joinToString("")
        }

        // Default fallback to Caesar Cipher
        else -> {
            filteredPlaintext.map { char ->
                val index = charList.indexOf(char)
                charList[(index + shiftAmount) % charList.size]
            }.joinToString("")
        }
    }

    // Apply multiple rounds if enabled
    if (multipleRounds) {
        result = result.reversed()
    }

    return result
}