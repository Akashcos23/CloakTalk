package com.example.cloaktalk.data.repository

import com.example.cloaktalk.data.local.dao.KeyDao
import com.example.cloaktalk.data.local.entity.KeyEntity
import kotlinx.coroutines.flow.Flow
import java.security.SecureRandom
import java.util.Base64

/**
 * Repository for Key data operations.
 * Acts as a single source of truth for encryption key data.
 * Handles key generation and expiration management.
 *
 * @property keyDao The DAO for key database operations
 */
class KeyRepository(private val keyDao: KeyDao) {

    /**
     * Generate a random encryption key.
     * @param length The length of the key in bytes (default 32)
     * @return A Base64 encoded random key string
     */
    fun generateRandomKey(length: Int = 32): String {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }

    /**
     * Create a new key for an encrypted message.
     * @param encryptId The encrypted message ID this key belongs to
     * @param keyExpire The expiration timestamp
     * @param customKey Optional custom key (if null, generates random)
     * @return The key ID of the created key
     */
    suspend fun createKey(
        encryptId: Long,
        keyExpire: Long,
        customKey: String? = null
    ): Long {
        val keyValue = customKey ?: generateRandomKey()
        val key = KeyEntity(
            key = keyValue,
            encryptId = encryptId,
            keyExpire = keyExpire,
            isActive = true
        )
        return keyDao.insertKey(key)
    }

    /**
     * Get a key by its ID.
     * @param keyId The key ID to search for
     * @return The key entity if found, null otherwise
     */
    suspend fun getKeyById(keyId: Long): KeyEntity? {
        return keyDao.getKeyById(keyId)
    }

    /**
     * Get a key by the encrypted message ID.
     * @param encryptId The encrypt message ID to search for
     * @return The key entity if found, null otherwise
     */
    suspend fun getKeyByEncryptId(encryptId: Long): KeyEntity? {
        return keyDao.getKeyByEncryptId(encryptId)
    }

    /**
     * Get all keys as a Flow.
     * @return Flow emitting list of all keys
     */
    fun getAllKeys(): Flow<List<KeyEntity>> {
        return keyDao.getAllKeys()
    }

    /**
     * Get all active keys as a Flow.
     * @return Flow emitting list of all active keys
     */
    fun getAllActiveKeys(): Flow<List<KeyEntity>> {
        return keyDao.getAllActiveKeys()
    }

    /**
     * Get all expired keys as a Flow.
     * @return Flow emitting list of all expired keys
     */
    fun getAllExpiredKeys(): Flow<List<KeyEntity>> {
        return keyDao.getAllExpiredKeys()
    }

    /**
     * Check if a key is active.
     * @param keyId The key ID to check
     * @return True if key is active, false otherwise
     */
    suspend fun isKeyActive(keyId: Long): Boolean {
        return keyDao.isKeyActive(keyId) ?: false
    }

    /**
     * Deactivate a specific key.
     * @param keyId The key ID to deactivate
     */
    suspend fun deactivateKey(keyId: Long) {
        keyDao.deactivateKey(keyId)
    }

    /**
     * Deactivate all expired keys based on current time.
     * @return Number of keys deactivated
     */
    suspend fun deactivateExpiredKeys(): Int {
        val currentTime = System.currentTimeMillis()
        return keyDao.deactivateExpiredKeys(currentTime)
    }

    /**
     * Update a key entity.
     * @param key The key entity to update
     */
    suspend fun updateKey(key: KeyEntity) {
        keyDao.updateKey(key)
    }

    /**
     * Add a decrypt ID to the key's list.
     * @param keyId The key ID to update
     * @param decryptId The decrypt ID to add
     */
    suspend fun addDecryptIdToKey(keyId: Long, decryptId: Long) {
        val key = keyDao.getKeyById(keyId)
        key?.let {
            val currentIds = if (it.decryptIds.isBlank()) {
                decryptId.toString()
            } else {
                "${it.decryptIds},$decryptId"
            }
            keyDao.updateDecryptIds(keyId, currentIds)
        }
    }

    /**
     * Delete a key by its ID.
     * @param keyId The key ID to delete
     */
    suspend fun deleteKeyById(keyId: Long) {
        keyDao.deleteKeyById(keyId)
    }

    /**
     * Delete all expired keys.
     */
    suspend fun deleteAllExpiredKeys() {
        keyDao.deleteAllExpiredKeys()
    }

    /**
     * Get keys expiring within a time range.
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return List of keys expiring within the range
     */
    suspend fun getKeysExpiringInRange(startTime: Long, endTime: Long): List<KeyEntity> {
        return keyDao.getKeysExpiringInRange(startTime, endTime)
    }

    /**
     * Get the count of active keys.
     * @return The total count of active keys
     */
    suspend fun getActiveKeyCount(): Int {
        return keyDao.getActiveKeyCount()
    }
}