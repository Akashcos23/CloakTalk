package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Key entity - stores encryption/decryption keys
 */
@Entity(tableName = "key_table")
data class KeyEntity(
    @PrimaryKey(autoGenerate = true)
    val keyId: Long = 0,
    val key: String, // The actual key
    val encryptId: Long,
    val decryptIds: String = "", // Comma-separated decrypt IDs (multiple users can access)
    val keyExpire: Long, // Timestamp for expiration
    val isActive: Boolean = true // False when key expires
)