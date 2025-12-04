package com.example.cloaktalk.ui.models


data class EncryptionHistory(
    val id: Int,
    val message: String,
    val algorithm: String,
    val date: String,
    val keyExpiry: String,
    val isExpired: Boolean
)

data class AccessNotification(
    val user: String,
    val time: String,
    val key: String
)

// Sample Data
val sampleHistory = listOf(
    EncryptionHistory(1, "Meeting at 5 PM", "Custom AES", "2025-11-05", "2025-12-05", false),
    EncryptionHistory(2, "Project deadline", "Caesar Plus", "2025-11-04", "2025-11-15", false),
    EncryptionHistory(3, "Password: admin123", "Triple DES", "2025-11-03", "Expired", true)
)

val sampleNotifications = listOf(
    AccessNotification("john@email.com", "2 mins ago", "Key #1234"),
    AccessNotification("jehrico@email.com", "15 mins ago", "Key #5678")
)