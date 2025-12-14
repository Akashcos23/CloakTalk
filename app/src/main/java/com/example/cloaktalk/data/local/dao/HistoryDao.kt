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

                @Query("SELECT * FROM history_table ORDER BY historyId DESC")
                fun getAllHistory(): Flow<List<HistoryEntity>>

                @Query("SELECT * FROM history_table WHERE historyId = :historyId LIMIT 1")
                suspend fun getHistoryById(historyId: Long): HistoryEntity?

                @Query("SELECT * FROM history_table WHERE encryptId = :encryptId ORDER BY historyId DESC")
                fun getHistoryByEncryptId(encryptId: Long): Flow<List<HistoryEntity>>

                @Query("SELECT * FROM history_table WHERE decryptId = :decryptId ORDER BY historyId DESC")
                suspend fun getHistoryByDecryptId(decryptId: Long): List<HistoryEntity>

                @Query("SELECT COUNT(*) FROM history_table")
                suspend fun getHistoryCount(): Int

                @Query("DELETE FROM history_table WHERE historyId = :historyId")
                suspend fun deleteHistoryById(historyId: Long)

                @Query("DELETE FROM history_table WHERE encryptId = :encryptId")
                suspend fun deleteHistoryByEncryptId(encryptId: Long)

                @Query("DELETE FROM history_table")
                suspend fun deleteAllHistory()

                @Query("SELECT EXISTS(SELECT 1 FROM history_table WHERE encryptId = :encryptId)")
                suspend fun historyExistsForEncrypt(encryptId: Long): Boolean

                @Query("""
                    SELECT 
                        h.historyId,
                        h.encryptId,
                        h.decryptId,
                        e.encryptedMessage,
                        e.algorithmName,
                        d.decryptedMessage,
                        h.createdAt
                    FROM history_table h
                    INNER JOIN encrypt_message_table e ON h.encryptId = e.encryptId
                    LEFT JOIN decrypt_message_table d ON h.decryptId = d.decryptId
                    WHERE h.historyId = :historyId
                """)
                suspend fun getHistoryComplete(historyId: Long): HistoryComplete?

                @Query("""
                    SELECT 
                        h.historyId,
                        h.encryptId,
                        h.decryptId,
                        e.encryptedMessage,
                        e.algorithmName,
                        d.decryptedMessage,
                        h.createdAt
                    FROM history_table h
                    INNER JOIN encrypt_message_table e ON h.encryptId = e.encryptId
                    LEFT JOIN decrypt_message_table d ON h.decryptId = d.decryptId
                    ORDER BY h.historyId DESC
                """)
                suspend fun getAllHistoryComplete(): List<HistoryComplete>
            }

            data class HistoryComplete(
                val historyId: Long,
                val encryptId: Long,
                val decryptId: Long?,
                val encryptedMessage: String,
                val algorithmName: String,
                val decryptedMessage: String?,
                val createdAt: Long
            )