package com.example.cloaktalk.data.local.dao

        import androidx.room.Dao
        import androidx.room.Delete
        import androidx.room.Insert
        import androidx.room.OnConflictStrategy
        import androidx.room.Query
        import kotlinx.coroutines.flow.Flow
        import com.example.cloaktalk.data.local.entity.HistoryEntity

        @Dao
        interface HistoryDao {

            @Insert(onConflict = OnConflictStrategy.REPLACE)
            suspend fun insertHistory(history: HistoryEntity): Long

            @Delete
            suspend fun deleteHistory(history: HistoryEntity)

            @Query("SELECT * FROM history ORDER BY historyId DESC")
            fun getAllHistory(): Flow<List<HistoryEntity>>

            @Query("SELECT * FROM history WHERE historyId = :historyId LIMIT 1")
            suspend fun getHistoryById(historyId: Long): HistoryEntity?

            @Query("SELECT * FROM history WHERE encryptId = :encryptId ORDER BY historyId DESC")
            fun getHistoryByEncryptId(encryptId: Long): Flow<List<HistoryEntity>>

            @Query("SELECT * FROM history WHERE decryptId = :decryptId ORDER BY historyId DESC")
            suspend fun getHistoryByDecryptId(decryptId: Long): List<HistoryEntity>

            @Query("SELECT * FROM history WHERE keyId = :keyId ORDER BY historyId DESC")
            suspend fun getHistoryByKeyId(keyId: Long): List<HistoryEntity>

            @Query("SELECT COUNT(*) FROM history")
            suspend fun getHistoryCount(): Int

            @Query("DELETE FROM history WHERE historyId = :historyId")
            suspend fun deleteHistoryById(historyId: Long)

            @Query("DELETE FROM history WHERE encryptId = :encryptId")
            suspend fun deleteHistoryByEncryptId(encryptId: Long)

            @Query("DELETE FROM history")
            suspend fun deleteAllHistory()

            @Query("SELECT EXISTS(SELECT 1 FROM history WHERE encryptId = :encryptId)")
            suspend fun historyExistsForEncrypt(encryptId: Long): Boolean

            @Query("""
                SELECT 
                    history.historyId,
                    history.encryptId,
                    history.decryptId,
                    history.keyId,
                    encrypt_message.message AS encryptedMessage,
                    encrypt_message.algoName AS algoName,
                    encrypt_message.userId AS encryptUserId,
                    decrypt_message.message AS decryptedMessage,
                    key_table.`key` AS actualKey,
                    key_table.isActive AS keyActive,
                    key_table.keyExpire AS keyExpire,
                    users.username AS username,
                    users.email AS email
                FROM history
                INNER JOIN encrypt_message ON history.encryptId = encrypt_message.encryptId
                LEFT JOIN decrypt_message ON history.decryptId = decrypt_message.decryptId
                INNER JOIN key_table ON history.keyId = key_table.keyId
                INNER JOIN users ON encrypt_message.userId = users.id
                WHERE history.historyId = :historyId
            """)
            suspend fun getHistoryComplete(historyId: Long): HistoryComplete?

            @Query("""
                SELECT 
                    history.historyId,
                    history.encryptId,
                    history.decryptId,
                    history.keyId,
                    encrypt_message.message AS encryptedMessage,
                    encrypt_message.algoName AS algoName,
                    encrypt_message.userId AS encryptUserId,
                    decrypt_message.message AS decryptedMessage,
                    key_table.`key` AS actualKey,
                    key_table.isActive AS keyActive,
                    key_table.keyExpire AS keyExpire,
                    users.username AS username,
                    users.email AS email
                FROM history
                INNER JOIN encrypt_message ON history.encryptId = encrypt_message.encryptId
                LEFT JOIN decrypt_message ON history.decryptId = decrypt_message.decryptId
                INNER JOIN key_table ON history.keyId = key_table.keyId
                INNER JOIN users ON encrypt_message.userId = users.id
                ORDER BY history.historyId DESC
            """)
            suspend fun getAllHistoryComplete(): List<HistoryComplete>

            @Query("""
                SELECT 
                    history.historyId,
                    history.encryptId,
                    history.decryptId,
                    history.keyId,
                    encrypt_message.message AS encryptedMessage,
                    encrypt_message.algoName AS algoName,
                    encrypt_message.userId AS encryptUserId,
                    decrypt_message.message AS decryptedMessage,
                    key_table.`key` AS actualKey,
                    key_table.isActive AS keyActive,
                    key_table.keyExpire AS keyExpire,
                    users.username AS username,
                    users.email AS email
                FROM history
                INNER JOIN encrypt_message ON history.encryptId = encrypt_message.encryptId
                LEFT JOIN decrypt_message ON history.decryptId = decrypt_message.decryptId
                INNER JOIN key_table ON history.keyId = key_table.keyId
                INNER JOIN users ON encrypt_message.userId = users.id
                WHERE encrypt_message.userId = :userId
                ORDER BY history.historyId DESC
            """)
            suspend fun getHistoryCompleteByUser(userId: Long): List<HistoryComplete>

            @Query("""
                SELECT 
                    history.encryptId,
                    COUNT(DISTINCT history.decryptId) AS decryptCount,
                    encrypt_message.message AS encryptedMessage,
                    encrypt_message.algoName AS algoName,
                    key_table.isActive AS keyActive
                FROM history
                INNER JOIN encrypt_message ON history.encryptId = encrypt_message.encryptId
                INNER JOIN key_table ON history.keyId = key_table.keyId
                WHERE history.encryptId = :encryptId
                GROUP BY history.encryptId
            """)
            suspend fun getHistorySummary(encryptId: Long): HistorySummary?
        }

        data class HistoryComplete(
            val historyId: Long,
            val encryptId: Long,
            val decryptId: Long?,
            val keyId: Long,
            val encryptedMessage: String,
            val algoName: String,
            val encryptUserId: Long,
            val decryptedMessage: String?,
            val actualKey: String,
            val keyActive: Boolean,
            val keyExpire: Long,
            val username: String,
            val email: String
        )

        data class HistorySummary(
            val encryptId: Long,
            val decryptCount: Int,
            val encryptedMessage: String,
            val algoName: String,
            val keyActive: Boolean
        )