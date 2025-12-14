package com.example.cloaktalk.data.repository

import com.example.cloaktalk.data.local.dao.EncryptMessageDao
import com.example.cloaktalk.data.local.dao.EncryptMessageWithKey
import com.example.cloaktalk.data.local.dao.EncryptMessageFullDetails
import com.example.cloaktalk.data.local.dao.EncryptMessageComplete
import com.example.cloaktalk.data.local.entity.EncryptMessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Encrypt Message data operations.
 * Acts as a single source of truth for encrypted message data.
 *
 * @property encryptMessageDao The DAO for encrypt message database operations
 */
class EncryptMessageRepository(private val encryptMessageDao: EncryptMessageDao) {

    /**
     * Create a new encrypted message.
     * @param userId The user ID creating the message
     * @param algoName The algorithm name used
     * @param algoId Optional design algorithm ID
     * @param keyId The key ID for this message
     * @param keyExpire The key expiration timestamp
     * @param message The encrypted message content
     * @return The encrypt message ID
     */
    suspend fun createEncryptMessage(
        userId: Long,
        algoName: String,
        algoId: Long?,
        keyId: Long,
        keyExpire: Long,
        message: String
    ): Long {
        val encryptMessage = EncryptMessageEntity(
            userId = userId,
            algoName = algoName,
            algoId = algoId,
            keyId = keyId,
            keyExpire = keyExpire,
            message = message
        )
        return encryptMessageDao.insertEncryptMessage(encryptMessage)
    }

    /**
     * Update an encrypted message.
     * @param encryptMessage The encrypt message entity to update
     */
    suspend fun updateEncryptMessage(encryptMessage: EncryptMessageEntity) {
        encryptMessageDao.updateEncryptMessage(encryptMessage)
    }

    /**
     * Delete an encrypted message.
     * @param encryptMessage The encrypt message entity to delete
     */
    suspend fun deleteEncryptMessage(encryptMessage: EncryptMessageEntity) {
        encryptMessageDao.deleteEncryptMessage(encryptMessage)
    }

    /**
     * Delete an encrypted message by its ID.
     * @param encryptId The encrypt message ID to delete
     */
    suspend fun deleteEncryptMessageById(encryptId: Long) {
        encryptMessageDao.deleteEncryptMessageById(encryptId)
    }

    /**
     * Get all encrypted messages as a Flow.
     * @return Flow emitting list of all encrypted messages
     */
    fun getAllEncryptMessages(): Flow<List<EncryptMessageEntity>> {
        return encryptMessageDao.getAllEncryptMessages()
    }

    /**
     * Get an encrypted message by its ID.
     * @param encryptId The encrypt message ID to search for
     * @return The encrypt message entity if found, null otherwise
     */
    suspend fun getEncryptMessageById(encryptId: Long): EncryptMessageEntity? {
        return encryptMessageDao.getEncryptMessageById(encryptId)
    }

    /**
     * Get all encrypted messages for a user as a Flow.
     * @param userId The user ID to filter by
     * @return Flow emitting list of encrypted messages for the user
     */
    fun getEncryptMessagesByUserId(userId: Long): Flow<List<EncryptMessageEntity>> {
        return encryptMessageDao.getEncryptMessagesByUserId(userId)
    }

    /**
     * Get all encrypted messages for a user as a list.
     * @param userId The user ID to filter by
     * @return List of encrypted messages for the user
     */
    suspend fun getEncryptMessagesByUserIdList(userId: Long): List<EncryptMessageEntity> {
        return encryptMessageDao.getEncryptMessagesByUserIdList(userId)
    }

    /**
     * Get encrypted messages by algorithm name as a Flow.
     * @param algoName The algorithm name to filter by
     * @return Flow emitting list of encrypted messages using that algorithm
     */
    fun getEncryptMessagesByAlgorithm(algoName: String): Flow<List<EncryptMessageEntity>> {
        return encryptMessageDao.getEncryptMessagesByAlgorithm(algoName)
    }

    /**
     * Get encrypted messages by design algorithm ID.
     * @param algoId The design algorithm ID to filter by
     * @return List of encrypted messages using that design algorithm
     */
    suspend fun getEncryptMessagesByDesignAlgoId(algoId: Long): List<EncryptMessageEntity> {
        return encryptMessageDao.getEncryptMessagesByDesignAlgoId(algoId)
    }

    /**
     * Get an encrypted message by its key ID.
     * @param keyId The key ID to search for
     * @return The encrypt message entity if found, null otherwise
     */
    suspend fun getEncryptMessageByKeyId(keyId: Long): EncryptMessageEntity? {
        return encryptMessageDao.getEncryptMessageByKeyId(keyId)
    }

    /**
     * Get the count of encrypted messages for a user.
     * @param userId The user ID to count for
     * @return The total count of encrypted messages for the user
     */
    suspend fun getEncryptMessageCountByUser(userId: Long): Int {
        return encryptMessageDao.getEncryptMessageCountByUser(userId)
    }

    /**
     * Get encrypted messages with expired keys.
     * @return List of encrypted messages with expired keys
     */
    suspend fun getEncryptMessagesWithExpiredKeys(): List<EncryptMessageEntity> {
        val currentTime = System.currentTimeMillis()
        return encryptMessageDao.getEncryptMessagesWithExpiredKeys(currentTime)
    }

    /**
     * Delete all encrypted messages for a user.
     * @param userId The user ID whose messages to delete
     */
    suspend fun deleteAllEncryptMessagesByUser(userId: Long) {
        encryptMessageDao.deleteAllEncryptMessagesByUser(userId)
    }

    /**
     * Get encrypted message with key details.
     * @param encryptId The encrypt message ID
     * @return Encrypted message with key information
     */
    suspend fun getEncryptMessageWithKey(encryptId: Long): EncryptMessageWithKey? {
        return encryptMessageDao.getEncryptMessageWithKey(encryptId)
    }

    /**
     * Get all encrypted messages for a user with key details.
     * @param userId The user ID to filter by
     * @return List of encrypted messages with key information
     */
    suspend fun getEncryptMessagesWithKeyByUser(userId: Long): List<EncryptMessageWithKey> {
        return encryptMessageDao.getEncryptMessagesWithKeyByUser(userId)
    }

    /**
     * Get encrypted message with full details.
     * @param encryptId The encrypt message ID
     * @return Comprehensive encrypted message details
     */
    suspend fun getEncryptMessageFullDetails(encryptId: Long): EncryptMessageFullDetails? {
        return encryptMessageDao.getEncryptMessageFullDetails(encryptId)
    }

    /**
     * Get all encrypted messages with full details for a user.
     * @param userId The user ID to filter by
     * @return List of comprehensive encrypted message details
     */
    suspend fun getEncryptMessagesWithFullDetailsByUser(userId: Long): List<EncryptMessageComplete> {
        return encryptMessageDao.getEncryptMessagesWithFullDetailsByUser(userId)
    }
}