package com.example.cloaktalk.data.local.dao

    import androidx.room.Dao
    import androidx.room.Delete
    import androidx.room.Insert
    import androidx.room.OnConflictStrategy
    import androidx.room.Query
    import androidx.room.Update
    import kotlinx.coroutines.flow.Flow
    import com.example.cloaktalk.data.local.entity.DecryptMessageEntity

    @Dao
    interface DecryptMessageDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertDecryptMessage(decryptMessage: DecryptMessageEntity): Long

        @Update
        suspend fun updateDecryptMessage(decryptMessage: DecryptMessageEntity)

        @Delete
        suspend fun deleteDecryptMessage(decryptMessage: DecryptMessageEntity)

        @Query("SELECT * FROM decrypt_message_table ORDER BY decryptId DESC")
        fun getAllDecryptMessages(): Flow<List<DecryptMessageEntity>>

        @Query("SELECT * FROM decrypt_message_table WHERE decryptId = :decryptId LIMIT 1")
        suspend fun getDecryptMessageById(decryptId: Long): DecryptMessageEntity?

        @Query("SELECT * FROM decrypt_message_table WHERE keyId = :keyId ORDER BY decryptId DESC")
        suspend fun getDecryptMessagesByKeyId(keyId: Long): List<DecryptMessageEntity>

        @Query("SELECT COUNT(*) FROM decrypt_message_table WHERE keyId = :keyId")
        suspend fun getDecryptMessageCountByKeyId(keyId: Long): Int

        @Query("DELETE FROM decrypt_message_table WHERE decryptId = :decryptId")
        suspend fun deleteDecryptMessageById(decryptId: Long)

        @Query("DELETE FROM decrypt_message_table WHERE keyId = :keyId")
        suspend fun deleteAllDecryptMessagesByKeyId(keyId: Long)

        @Query("SELECT EXISTS(SELECT 1 FROM decrypt_message_table WHERE keyId = :keyId)")
        suspend fun decryptExistsForKey(keyId: Long): Boolean

        @Query("""
            SELECT 
                d.decryptId,
                d.encryptedMessage,
                d.decryptedMessage,
                d.keyId,
                d.algorithmName,
                d.isSuccessful,
                d.createdAt,
                k.`key` AS actualKey,
                k.isActive AS keyActive,
                k.keyExpire AS keyExpire
            FROM decrypt_message_table d
            INNER JOIN key_table k ON d.keyId = k.keyId
            WHERE d.decryptId = :decryptId
        """)
        suspend fun getDecryptWithKeyInfo(decryptId: Long): DecryptWithKeyInfo?

        @Query("""
            SELECT 
                d.decryptId,
                d.encryptedMessage,
                d.decryptedMessage,
                d.keyId,
                d.algorithmName,
                d.isSuccessful,
                d.createdAt,
                k.`key` AS actualKey,
                k.isActive AS keyActive,
                k.keyExpire AS keyExpire
            FROM decrypt_message_table d
            INNER JOIN key_table k ON d.keyId = k.keyId
            ORDER BY d.decryptId DESC
        """)
        suspend fun getAllDecryptWithKeyInfo(): List<DecryptWithKeyInfo>
    }

    data class DecryptWithKeyInfo(
        val decryptId: Long,
        val encryptedMessage: String,
        val decryptedMessage: String,
        val keyId: Long,
        val algorithmName: String,
        val isSuccessful: Boolean,
        val createdAt: Long,
        val actualKey: String,
        val keyActive: Boolean,
        val keyExpire: Long
    )