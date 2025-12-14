package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * History entity - tracks encryption/decryption history
 */
@Entity(
    tableName = "history_table",
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
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("encryptId"), Index("decryptId"), Index("keyId")]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val historyId: Long = 0,
    val encryptId: Long,
    val decryptId: Long? = null,
    val keyId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val user_id: Long
)