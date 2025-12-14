package com.example.cloaktalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.cloaktalk.data.local.entity.KeyEntity

/**
 * Data Access Object for Key operations.
 * Provides methods for CRUD operations on the key_table.
 * Includes methods for key expiration management.
 */
@Dao
interface KeyDao {

    /**
     * Insert a new key into the database.
     * @param key The key entity to insert
     * @return The row ID of the inserted key
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: KeyEntity): Long

    /**
     * Update an existing key.
     * @param key The key entity to update
     */
    @Update
    suspend fun updateKey(key: KeyEntity)

    /**
     * Delete a key from the database.
     * @param key The key entity to delete
     */
    @Delete
    suspend fun deleteKey(key: KeyEntity)

    /**
     * Get all keys as a Flow for observing changes.
     * @return Flow emitting list of all keys
     */
    @Query("SELECT * FROM key_table")
    fun getAllKeys(): Flow<List<KeyEntity>>

    /**
     * Get a key by its ID.
     * @param keyId The key ID to search for
     * @return The key entity if found, null otherwise
     */
    @Query("SELECT * FROM key_table WHERE keyId = :keyId LIMIT 1")
    suspend fun getKeyById(keyId: Long): KeyEntity?

    /**
     * Get a key by the encrypt message ID.
     * @param encryptId The encrypt message ID to search for
     * @return The key entity if found, null otherwise
     */
    @Query("SELECT * FROM key_table WHERE encryptId = :encryptId LIMIT 1")
    suspend fun getKeyByEncryptId(encryptId: Long): KeyEntity?

    /**
     * Get all active keys.
     * @return Flow emitting list of all active keys
     */
    @Query("SELECT * FROM key_table WHERE isActive = 1")
    fun getAllActiveKeys(): Flow<List<KeyEntity>>

    /**
     * Get all expired keys.
     * @return Flow emitting list of all expired/inactive keys
     */
    @Query("SELECT * FROM key_table WHERE isActive = 0")
    fun getAllExpiredKeys(): Flow<List<KeyEntity>>

    /**
     * Get all keys that have expired based on current time.
     * @param currentTime The current timestamp to compare against
     * @return List of keys that should be expired
     */
    @Query("SELECT * FROM key_table WHERE keyExpire < :currentTime AND isActive = 1")
    suspend fun getKeysToExpire(currentTime: Long): List<KeyEntity>

    /**
     * Update key status to expired (inactive).
     * @param keyId The key ID to update
     */
    @Query("UPDATE key_table SET isActive = 0 WHERE keyId = :keyId")
    suspend fun deactivateKey(keyId: Long)

    /**
     * Batch update: Deactivate all keys that have expired.
     * @param currentTime The current timestamp to compare against
     * @return Number of keys deactivated
     */
    @Query("UPDATE key_table SET isActive = 0 WHERE keyExpire < :currentTime AND isActive = 1")
    suspend fun deactivateExpiredKeys(currentTime: Long): Int

    /**
     * Check if a key is active.
     * @param keyId The key ID to check
     * @return True if key is active, false otherwise
     */
    @Query("SELECT isActive FROM key_table WHERE keyId = :keyId")
    suspend fun isKeyActive(keyId: Long): Boolean?

    /**
     * Get the count of active keys.
     * @return The total count of active keys
     */
    @Query("SELECT COUNT(*) FROM key_table WHERE isActive = 1")
    suspend fun getActiveKeyCount(): Int

    /**
     * Delete a key by its ID.
     * @param keyId The key ID to delete
     */
    @Query("DELETE FROM key_table WHERE keyId = :keyId")
    suspend fun deleteKeyById(keyId: Long)

    /**
     * Delete all expired keys from the database.
     */
    @Query("DELETE FROM key_table WHERE isActive = 0")
    suspend fun deleteAllExpiredKeys()

    /**
     * Add a decrypt ID to the list of decrypt IDs for a key.
     * @param keyId The key ID to update
     * @param decryptIds The updated comma-separated decrypt IDs
     */
    @Query("UPDATE key_table SET decryptIds = :decryptIds WHERE keyId = :keyId")
    suspend fun updateDecryptIds(keyId: Long, decryptIds: String)

    /**
     * Get keys expiring within a time range.
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return List of keys expiring within the range
     */
    @Query("SELECT * FROM key_table WHERE keyExpire BETWEEN :startTime AND :endTime AND isActive = 1")
    suspend fun getKeysExpiringInRange(startTime: Long, endTime: Long): List<KeyEntity>
}