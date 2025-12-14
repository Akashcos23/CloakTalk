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

    @Query("SELECT * FROM decrypt_message ORDER BY decryptId DESC")
    fun getAllDecryptMessages(): Flow<List<DecryptMessageEntity>>

    @Query("SELECT * FROM decrypt_message WHERE decryptId = :decryptId LIMIT 1")
    suspend fun getDecryptMessageById(decryptId: Long): DecryptMessageEntity?

    @Query("SELECT * FROM decrypt_message WHERE encryptId = :encryptId ORDER BY decryptId DESC")
    fun getDecryptMessagesByEncryptId(encryptId: Long): Flow<List<DecryptMessageEntity>>

    @Query("SELECT * FROM decrypt_message WHERE encryptId = :encryptId ORDER BY decryptId DESC")
    suspend fun getDecryptMessagesByEncryptIdList(encryptId: Long): List<DecryptMessageEntity>

    @Query("SELECT * FROM decrypt_message WHERE keyId = :keyId ORDER BY decryptId DESC")
    suspend fun getDecryptMessagesByKeyId(keyId: Long): List<DecryptMessageEntity>

    @Query("SELECT COUNT(*) FROM decrypt_message WHERE encryptId = :encryptId")
    suspend fun getDecryptMessageCountByEncryptId(encryptId: Long): Int

    @Query("DELETE FROM decrypt_message WHERE decryptId = :decryptId")
    suspend fun deleteDecryptMessageById(decryptId: Long)

    @Query("DELETE FROM decrypt_message WHERE encryptId = :encryptId")
    suspend fun deleteAllDecryptMessagesByEncryptId(encryptId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM decrypt_message WHERE encryptId = :encryptId)")
    suspend fun decryptExistsForEncrypt(encryptId: Long): Boolean

    @Query("""
        SELECT 
            decrypt_message.decryptId,
            decrypt_message.encryptId,
            decrypt_message.keyId,
            decrypt_message.message,
            encrypt_message.message AS encryptedMessage,
            encrypt_message.algoName AS algoName,
            encrypt_message.userId AS encryptUserId
        FROM decrypt_message
        INNER JOIN encrypt_message ON decrypt_message.encryptId = encrypt_message.encryptId
        WHERE decrypt_message.decryptId = :decryptId
    """)
    suspend fun getDecryptWithEncryptInfo(decryptId: Long): DecryptWithEncryptInfo?

    @Query("""
        SELECT 
            decrypt_message.decryptId,
            decrypt_message.encryptId,
            decrypt_message.keyId,
            decrypt_message.message,
            encrypt_message.message AS encryptedMessage,
            encrypt_message.algoName AS algoName,
            encrypt_message.userId AS encryptUserId
        FROM decrypt_message
        INNER JOIN encrypt_message ON decrypt_message.encryptId = encrypt_message.encryptId
        ORDER BY decrypt_message.decryptId DESC
    """)
    suspend fun getAllDecryptWithEncryptInfo(): List<DecryptWithEncryptInfo>

    @Query("""
        SELECT 
            decrypt_message.decryptId,
            decrypt_message.encryptId,
            decrypt_message.keyId,
            decrypt_message.message,
            encrypt_message.message AS encryptedMessage,
            encrypt_message.algoName AS algoName,
            encrypt_message.userId AS encryptUserId,
            key_table.`key` AS actualKey,
            key_table.isActive AS keyActive,
            key_table.keyExpire AS keyExpire
        FROM decrypt_message
        INNER JOIN encrypt_message ON decrypt_message.encryptId = encrypt_message.encryptId
        INNER JOIN key_table ON decrypt_message.keyId = key_table.keyId
        WHERE decrypt_message.decryptId = :decryptId
    """)
    suspend fun getDecryptMessageComplete(decryptId: Long): DecryptMessageComplete?

    @Query("""
        SELECT 
            decrypt_message.decryptId,
            decrypt_message.encryptId,
            decrypt_message.keyId,
            decrypt_message.message,
            encrypt_message.message AS encryptedMessage,
            encrypt_message.algoName AS algoName,
            encrypt_message.userId AS encryptUserId,
            key_table.`key` AS actualKey,
            key_table.isActive AS keyActive,
            key_table.keyExpire AS keyExpire
        FROM decrypt_message
        INNER JOIN encrypt_message ON decrypt_message.encryptId = encrypt_message.encryptId
        INNER JOIN key_table ON decrypt_message.keyId = key_table.keyId
        WHERE decrypt_message.encryptId = :encryptId
        ORDER BY decrypt_message.decryptId DESC
    """)
    suspend fun getDecryptMessagesCompleteByEncryptId(encryptId: Long): List<DecryptMessageComplete>
}

data class DecryptWithEncryptInfo(
    val decryptId: Long,
    val encryptId: Long,
    val keyId: Long,
    val message: String,
    val encryptedMessage: String,
    val algoName: String,
    val encryptUserId: Long
)

data class DecryptMessageComplete(
    val decryptId: Long,
    val encryptId: Long,
    val keyId: Long,
    val message: String,
    val encryptedMessage: String,
    val algoName: String,
    val encryptUserId: Long,
    val actualKey: String,
    val keyActive: Boolean,
    val keyExpire: Long
)