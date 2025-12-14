package com.example.cloaktalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.cloaktalk.data.local.entity.EncryptMessageEntity

@Dao
interface EncryptMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncryptMessage(encryptMessage: EncryptMessageEntity): Long

    @Update
    suspend fun updateEncryptMessage(encryptMessage: EncryptMessageEntity)

    @Delete
    suspend fun deleteEncryptMessage(encryptMessage: EncryptMessageEntity)

    @Query("SELECT * FROM encrypt_message_table ORDER BY encryptId DESC")
    fun getAllEncryptMessages(): Flow<List<EncryptMessageEntity>>
    /**
     * Get all encrypted messages as a List (One-shot query).
     */
    @Query("SELECT * FROM encrypt_message_table ORDER BY encryptId DESC")
    suspend fun getAllEncryptedMessages(): List<EncryptMessageEntity>
    @Query("SELECT * FROM encrypt_message_table WHERE encryptId = :encryptId LIMIT 1")
    suspend fun getEncryptMessageById(encryptId: Long): EncryptMessageEntity?

    @Query("SELECT * FROM encrypt_message_table WHERE keyId = :keyId LIMIT 1")
    suspend fun getEncryptMessageByKeyId(keyId: Long): EncryptMessageEntity?

    @Query("SELECT * FROM encrypt_message_table WHERE algorithmName = :algorithmName ORDER BY encryptId DESC")
    fun getEncryptMessagesByAlgorithm(algorithmName: String): Flow<List<EncryptMessageEntity>>

    @Query("SELECT COUNT(*) FROM encrypt_message_table")
    suspend fun getEncryptMessageCount(): Int

    @Query("DELETE FROM encrypt_message_table WHERE encryptId = :encryptId")
    suspend fun deleteEncryptMessageById(encryptId: Long)

    @Query("DELETE FROM encrypt_message_table WHERE keyId = :keyId")
    suspend fun deleteEncryptMessageByKeyId(keyId: Long)

    @Query("""
        SELECT 
            e.encryptId,
            e.originalMessage,
            e.encryptedMessage,
            e.keyId,
            e.algorithmName,
            e.createdAt,
            k.`key` AS actualKey,
            k.isActive AS keyActive,
            k.keyExpire AS keyExpire
        FROM encrypt_message_table e
        INNER JOIN key_table k ON e.keyId = k.keyId
        WHERE e.encryptId = :encryptId
    """)
    suspend fun getEncryptMessageWithKey(encryptId: Long): EncryptMessageWithKey?

    @Query("""
        SELECT 
            e.encryptId,
            e.originalMessage,
            e.encryptedMessage,
            e.keyId,
            e.algorithmName,
            e.createdAt,
            k.`key` AS actualKey,
            k.isActive AS keyActive,
            k.keyExpire AS keyExpire
        FROM encrypt_message_table e
        INNER JOIN key_table k ON e.keyId = k.keyId
        ORDER BY e.encryptId DESC
    """)
    suspend fun getAllEncryptMessagesWithKey(): List<EncryptMessageWithKey>
}

data class EncryptMessageWithKey(
    val encryptId: Long,
    val originalMessage: String,
    val encryptedMessage: String,
    val keyId: Long,
    val algorithmName: String,
    val createdAt: Long,
    val actualKey: String,
    val keyActive: Boolean,
    val keyExpire: Long
)