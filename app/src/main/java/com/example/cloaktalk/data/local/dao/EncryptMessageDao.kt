package com.example.cloaktalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.cloaktalk.data.local.entity.EncryptMessageEntity

/**
 * Data Access Object for Encrypt Message operations.
 * Provides methods for CRUD operations on the encrypt_message table.
 * Includes join queries for comprehensive message data retrieval.
 */
@Dao
interface EncryptMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncryptMessage(encryptMessage: EncryptMessageEntity): Long

    @Update
    suspend fun updateEncryptMessage(encryptMessage: EncryptMessageEntity)

    @Delete
    suspend fun deleteEncryptMessage(encryptMessage: EncryptMessageEntity)

    @Query("SELECT * FROM encrypt_message ORDER BY encryptId DESC")
    fun getAllEncryptMessages(): Flow<List<EncryptMessageEntity>>

    @Query("SELECT * FROM encrypt_message WHERE encryptId = :encryptId LIMIT 1")
    suspend fun getEncryptMessageById(encryptId: Long): EncryptMessageEntity?

    @Query("SELECT * FROM encrypt_message WHERE userId = :userId ORDER BY encryptId DESC")
    fun getEncryptMessagesByUserId(userId: Long): Flow<List<EncryptMessageEntity>>

    @Query("SELECT * FROM encrypt_message WHERE userId = :userId ORDER BY encryptId DESC")
    suspend fun getEncryptMessagesByUserIdList(userId: Long): List<EncryptMessageEntity>

    @Query("SELECT * FROM encrypt_message WHERE algoName = :algoName ORDER BY encryptId DESC")
    fun getEncryptMessagesByAlgorithm(algoName: String): Flow<List<EncryptMessageEntity>>

    @Query("SELECT * FROM encrypt_message WHERE algoId = :algoId ORDER BY encryptId DESC")
    suspend fun getEncryptMessagesByDesignAlgoId(algoId: Long): List<EncryptMessageEntity>

    @Query("SELECT * FROM encrypt_message WHERE keyId = :keyId LIMIT 1")
    suspend fun getEncryptMessageByKeyId(keyId: Long): EncryptMessageEntity?

    @Query("SELECT COUNT(*) FROM encrypt_message WHERE userId = :userId")
    suspend fun getEncryptMessageCountByUser(userId: Long): Int

    @Query("DELETE FROM encrypt_message WHERE encryptId = :encryptId")
    suspend fun deleteEncryptMessageById(encryptId: Long)

    @Query("DELETE FROM encrypt_message WHERE userId = :userId")
    suspend fun deleteAllEncryptMessagesByUser(userId: Long)

    @Query("SELECT * FROM encrypt_message WHERE keyExpire < :currentTime")
    suspend fun getEncryptMessagesWithExpiredKeys(currentTime: Long): List<EncryptMessageEntity>

    @Query("""
        SELECT 
            encrypt_message.encryptId,
            encrypt_message.userId,
            encrypt_message.algoName,
            encrypt_message.algoId,
            encrypt_message.keyId,
            encrypt_message.keyExpire,
            encrypt_message.message,
            key_table.`key` AS actualKey,
            key_table.isActive AS keyActive
        FROM encrypt_message
        INNER JOIN key_table ON encrypt_message.keyId = key_table.keyId
        WHERE encrypt_message.encryptId = :encryptId
    """)
    suspend fun getEncryptMessageWithKey(encryptId: Long): EncryptMessageWithKey?

    @Query("""
        SELECT 
            encrypt_message.encryptId,
            encrypt_message.userId,
            encrypt_message.algoName,
            encrypt_message.algoId,
            encrypt_message.keyId,
            encrypt_message.keyExpire,
            encrypt_message.message,
            key_table.`key` AS actualKey,
            key_table.isActive AS keyActive
        FROM encrypt_message
        INNER JOIN key_table ON encrypt_message.keyId = key_table.keyId
        WHERE encrypt_message.userId = :userId
        ORDER BY encrypt_message.encryptId DESC
    """)
    suspend fun getEncryptMessagesWithKeyByUser(userId: Long): List<EncryptMessageWithKey>

    @Query("""
        SELECT 
            encrypt_message.encryptId,
            encrypt_message.userId,
            encrypt_message.algoName,
            encrypt_message.algoId,
            encrypt_message.keyId,
            encrypt_message.keyExpire,
            encrypt_message.message,
            users.username AS username,
            users.email AS email,
            design_algorithm.charset AS designCharset
        FROM encrypt_message
        INNER JOIN users ON encrypt_message.userId = users.id
        LEFT JOIN design_algorithm ON encrypt_message.algoId = design_algorithm.algorithmId
        WHERE encrypt_message.encryptId = :encryptId
    """)
    suspend fun getEncryptMessageFullDetails(encryptId: Long): EncryptMessageFullDetails?

    @Query("""
        SELECT 
            encrypt_message.encryptId,
            encrypt_message.userId,
            encrypt_message.algoName,
            encrypt_message.algoId,
            encrypt_message.keyId,
            encrypt_message.keyExpire,
            encrypt_message.message,
            users.username AS username,
            users.email AS email,
            key_table.`key` AS actualKey,
            key_table.isActive AS keyActive,
            design_algorithm.charset AS designCharset
        FROM encrypt_message
        INNER JOIN users ON encrypt_message.userId = users.id
        INNER JOIN key_table ON encrypt_message.keyId = key_table.keyId
        LEFT JOIN design_algorithm ON encrypt_message.algoId = design_algorithm.algorithmId
        WHERE encrypt_message.userId = :userId
        ORDER BY encrypt_message.encryptId DESC
    """)
    suspend fun getEncryptMessagesWithFullDetailsByUser(userId: Long): List<EncryptMessageComplete>
}

data class EncryptMessageWithKey(
    val encryptId: Long,
    val userId: Long,
    val algoName: String,
    val algoId: Long?,
    val keyId: Long,
    val keyExpire: Long,
    val message: String,
    val actualKey: String,
    val keyActive: Boolean
)

data class EncryptMessageFullDetails(
    val encryptId: Long,
    val userId: Long,
    val algoName: String,
    val algoId: Long?,
    val keyId: Long,
    val keyExpire: Long,
    val message: String,
    val username: String,
    val email: String,
    val designCharset: String?
)

data class EncryptMessageComplete(
    val encryptId: Long,
    val userId: Long,
    val algoName: String,
    val algoId: Long?,
    val keyId: Long,
    val keyExpire: Long,
    val message: String,
    val username: String,
    val email: String,
    val actualKey: String,
    val keyActive: Boolean,
    val designCharset: String?
)