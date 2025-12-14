package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Decrypt Message entity - stores decrypted messages
 */
@Entity(
    tableName = "decrypt_message",
    foreignKeys = [
        ForeignKey(
            entity = EncryptMessageEntity::class,
            parentColumns = ["encryptId"],
            childColumns = ["encryptId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = KeyEntity::class,
            parentColumns = ["keyId"],
            childColumns = ["keyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("encryptId"), Index("keyId")]
)
data class DecryptMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val decryptId: Long = 0,
    val encryptId: Long,
    val keyId: Long,
    val message: String // The decrypted message
)