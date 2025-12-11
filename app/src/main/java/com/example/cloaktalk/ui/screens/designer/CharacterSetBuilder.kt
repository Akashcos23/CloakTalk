package com.example.cloaktalk.ui.screens.designer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Interactive character set builder allowing visual selection of characters
 */
@Composable
fun CharacterSetBuilder(
    selectedChars: Set<Char>,
    onSelectionChange: (Set<Char>) -> Unit
) {
    // Character groups for organized display
    val characterGroups = remember {
        mapOf(
            "Uppercase" to ('A'..'Z').toList(),
            "Lowercase" to ('a'..'z').toList(),
            "Numbers" to ('0'..'9').toList(),
            "Symbols" to "!@#$%^&*()_+-=[]{}|;:,.<>?/~`".toList()
        )
    }

    // Track which groups are expanded
    var expandedGroups by remember { mutableStateOf(setOf("Uppercase", "Lowercase")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with title and selection count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Character Set Builder",
                    color = OrangeTheme.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedChars.size < 20) {
                        Color(0xFFEF4444).copy(alpha = 0.2f)
                    } else {
                        OrangeTheme.Primary.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        "${selectedChars.size}/20 min",
                        color = if (selectedChars.size < 20) Color(0xFFEF4444) else OrangeTheme.Primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description text with validation warning
            Text(
                if (selectedChars.size < 20) {
                    "⚠️ Select at least 20 characters for secure encryption"
                } else {
                    "Tap characters to include/exclude them from encryption"
                },
                color = if (selectedChars.size < 20) Color(0xFFEF4444) else OrangeTheme.TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Iterate through each character group
            characterGroups.forEach { (groupName, chars) ->
                val isExpanded = groupName in expandedGroups
                val groupSelection = chars.count { it in selectedChars }
                val groupCharsSet = chars.toSet()

                // Group header
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedGroups = if (isExpanded) {
                                expandedGroups - groupName
                            } else {
                                expandedGroups + groupName
                            }
                        },
                    color = OrangeTheme.SurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                groupName,
                                color = OrangeTheme.TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                "$groupSelection/${chars.size}",
                                color = OrangeTheme.TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Select All button
                            TextButton(
                                onClick = {
                                    onSelectionChange(selectedChars + chars)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "All",
                                    fontSize = 11.sp,
                                    color = OrangeTheme.Primary
                                )
                            }

                            // Clear button with minimum validation
                            TextButton(
                                onClick = {
                                    val remainingChars = selectedChars - groupCharsSet
                                    // Only allow clearing if we still have at least 20 characters
                                    if (remainingChars.size >= 20) {
                                        onSelectionChange(remainingChars)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                enabled = (selectedChars - groupCharsSet).size >= 20
                            ) {
                                Text(
                                    "Clear",
                                    fontSize = 11.sp,
                                    color = if ((selectedChars - groupCharsSet).size >= 20) {
                                        OrangeTheme.TextSecondary
                                    } else {
                                        OrangeTheme.TextSecondary.copy(alpha = 0.3f)
                                    }
                                )
                            }

                            // Expand/collapse indicator
                            Text(
                                if (isExpanded) "▼" else "▶",
                                color = OrangeTheme.TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Character grid (shown when expanded)
                AnimatedVisibility(visible = isExpanded) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 36.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(top = 8.dp),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(chars) { char ->
                            val canDeselect = (selectedChars - char).size >= 20
                            CharacterCell(
                                char = char,
                                isSelected = char in selectedChars,
                                onClick = {
                                    onSelectionChange(
                                        if (char in selectedChars) {
                                            // Only allow deselection if we still have 20+ chars
                                            if (canDeselect) selectedChars - char else selectedChars
                                        } else {
                                            selectedChars + char
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Individual character cell for the character set builder
 */
@Composable
private fun CharacterCell(
    char: Char,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) {
                    OrangeTheme.Primary.copy(alpha = 0.2f)
                } else {
                    OrangeTheme.SurfaceVariant
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) OrangeTheme.Primary else OrangeTheme.Border,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.toString(),
            color = if (isSelected) OrangeTheme.Primary else OrangeTheme.TextPrimary,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}