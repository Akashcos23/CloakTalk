package com.example.cloaktalk.data.local.entity

    import androidx.room.Entity
    import androidx.room.ForeignKey
    import androidx.room.Index
    import androidx.room.PrimaryKey

    /**
     * Key entity - stores encryption/decryption keys
     */
    @Entity(
        tableName = "key_table",
        foreignKeys = [
            ForeignKey(
                entity = BaseAlgorithmEntity::class,
                parentColumns = ["baseAlgoName"],
                childColumns = ["baseAlgoName"],
                onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                entity = UserEntity::class,
                parentColumns = ["id"],
                childColumns = ["user_id"],
                onDelete = ForeignKey.CASCADE
            )
        ],
        indices = [Index("baseAlgoName"), Index("key"), Index("user_id")]
    )
    data class KeyEntity(
        @PrimaryKey(autoGenerate = true)
        val keyId: Long = 0,
        val key: String,
        val baseAlgoName: String,
        val designAlgorithmId: Long? = null,
        val keyExpire: Long,
        val isActive: Boolean = true,
        val user_id: Long,
        val createdAt: Long = System.currentTimeMillis()
    )