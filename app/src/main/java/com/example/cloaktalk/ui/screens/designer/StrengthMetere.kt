package com.example.cloaktalk.ui.screens.designer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloaktalk.ui.theme.OrangeTheme

/**
 * Composable displaying security strength meter with animated progress bar
 */
@Composable
fun StrengthMeterCard(strengthScore: StrengthResult) {
    // Animate the progress bar filling
    val animatedProgress by animateFloatAsState(
        targetValue = strengthScore.score / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "strength_progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeTheme.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with title and info icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Security Strength",
                    color = OrangeTheme.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = OrangeTheme.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Strength level indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    strengthScore.level,
                    color = strengthScore.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "${strengthScore.score}/100",
                    color = OrangeTheme.TextSecondary,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(OrangeTheme.SurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    strengthScore.color.copy(alpha = 0.7f),
                                    strengthScore.color
                                )
                            )
                        )
                )
            }

            // Show recommendations if available
            AnimatedVisibility(visible = strengthScore.recommendations.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    strengthScore.recommendations.forEach { recommendation ->
                        Text(
                            "• $recommendation",
                            color = OrangeTheme.TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Data class holding strength score result with color and recommendations
 */
data class StrengthResult(
    val score: Int,
    val level: String,
    val color: Color,
    val recommendations: List<String>
)

/**
 * Calculate security strength score based on algorithm configuration
 */
fun calculateStrengthScore(
    baseAlgo: String,
    shiftAmount: String,
    multipleRounds: Boolean,
    charSetSize: Int
): StrengthResult {
    var score = 0
    val recommendations = mutableListOf<String>()

    // Base algorithm score (30 points max)
    score += when (baseAlgo) {
        "Caesar Cipher" -> 10
        "Substitution Cipher" -> 20
        "Vigenère Cipher" -> 15
        else -> 10
    }

    // Shift amount validation (20 points max)
    val shift = shiftAmount.toIntOrNull() ?: 0
    if (shift >= 13) {
        score += 20
    } else if (shift >= 5) {
        score += 10
        recommendations.add("Increase shift to 13+ for better security")
    } else {
        recommendations.add("Shift amount too low (use 13+)")
    }

    // Multiple rounds (20 points)
    if (multipleRounds) {
        score += 20
    } else {
        recommendations.add("Enable multiple rounds for +20% strength")
    }

    // Character set size score (30 points max)
    val charSetScore = when {
        charSetSize >= 80 -> 30 // All characters
        charSetSize >= 62 -> 25 // Letters + numbers
        charSetSize >= 52 -> 20 // All letters
        charSetSize >= 26 -> 15 // One letter case
        else -> 5
    }
    score += charSetScore

    if (charSetSize < 80) {
        recommendations.add("Add more characters for stronger encryption")
    }

    // Determine level and color based on score
    val (level, color) = when {
        score >= 80 -> "Strong" to Color(0xFF10B981)
        score >= 50 -> "Medium" to Color(0xFFF59E0B)
        else -> "Weak" to Color(0xFFEF4444)
    }

    return StrengthResult(score, level, color, recommendations)
}