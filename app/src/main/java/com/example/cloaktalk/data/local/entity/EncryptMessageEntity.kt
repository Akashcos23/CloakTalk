package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Encrypt Message entity - stores encrypted messages
 */
@Entity(
    tableName = "encrypt_message",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = KeyEntity::class,
            parentColumns = ["keyId"],
            childColumns = ["keyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("keyId"), Index("algoId")]
)
data class EncryptMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val encryptId: Long = 0,
    val userId: Long,
    val algoName: String, // From BaseAlgorithm or DesignAlgorithm
    val algoId: Long? = null, // Nullable - from DesignAlgorithm if user created one
    val keyId: Long,
    val keyExpire: Long,
    val message: String // The encrypted message
)