package com.example.cloaktalk.data.local.entity

    import androidx.room.Entity
    import androidx.room.ForeignKey
    import androidx.room.Index
    import androidx.room.PrimaryKey

    /**
     * Decrypt message entity - stores decryption attempts
     */
    @Entity(
        tableName = "decrypt_message_table",
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
    data class DecryptMessageEntity(
        @PrimaryKey(autoGenerate = true)
        val decryptId: Long = 0,
        val encryptedMessage: String,
        val decryptedMessage: String,
        val keyId: Long,
        val algorithmName: String,
        val isSuccessful: Boolean,
        val createdAt: Long = System.currentTimeMillis()
    )