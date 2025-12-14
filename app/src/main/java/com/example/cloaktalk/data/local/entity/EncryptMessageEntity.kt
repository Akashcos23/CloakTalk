package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


             /**
             * Encrypt message entity - stores encrypted messages
             */
            @Entity(
                tableName = "encrypt_message_table",
                foreignKeys = [
                    ForeignKey(
                        entity = KeyEntity::class,
                        parentColumns = ["keyId"],
                        childColumns = ["keyId"],
                        onDelete = ForeignKey.CASCADE
                    )
                ],
                indices = [Index("keyId")]
            )
            data class EncryptMessageEntity(
                @PrimaryKey(autoGenerate = true)
                val encryptId: Long = 0,
                val originalMessage: String,
                val encryptedMessage: String,
                val keyId: Long,
                val algorithmName: String,
                val createdAt: Long = System.currentTimeMillis()
            )