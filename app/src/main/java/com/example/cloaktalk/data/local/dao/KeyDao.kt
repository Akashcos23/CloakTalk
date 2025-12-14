package com.example.cloaktalk.data.local.dao

import androidx.room.*
import com.example.cloaktalk.data.local.entity.KeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: KeyEntity): Long

    @Update
    suspend fun updateKey(key: KeyEntity)

    @Delete
    suspend fun deleteKey(key: KeyEntity)

    @Query("SELECT * FROM key_table WHERE keyId = :keyId")
    suspend fun getKeyById(keyId: Long): KeyEntity?

    @Query("SELECT * FROM key_table WHERE `key` = :key LIMIT 1")
    suspend fun getKeyByValue(key: String): KeyEntity?

    @Query("SELECT * FROM key_table WHERE `key` = :key AND isActive = 1 LIMIT 1")
    suspend fun getActiveKeyByValue(key: String): KeyEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM key_table WHERE `key` = :key)")
    suspend fun isKeyExists(key: String): Boolean

    @Query("SELECT * FROM key_table")
    fun getAllKeys(): Flow<List<KeyEntity>>

    @Query("SELECT * FROM key_table WHERE isActive = 1")
    fun getActiveKeys(): Flow<List<KeyEntity>>

    @Query("UPDATE key_table SET isActive = 0 WHERE keyExpire < :currentTime AND isActive = 1")
    suspend fun deactivateExpiredKeys(currentTime: Long)

    @Query("UPDATE key_table SET isActive = 0 WHERE keyId = :keyId")
    suspend fun deactivateKey(keyId: Long)

    @Query("DELETE FROM key_table WHERE keyId = :keyId")
    suspend fun deleteKeyById(keyId: Long)

    @Query("SELECT COUNT(*) FROM key_table WHERE user_id = :userId AND isActive = 1 AND keyExpire > :currentTime")
    suspend fun getActiveKeyCountByUserId(userId: Long, currentTime: Long): Int

    @Query("SELECT * FROM key_table WHERE user_id = :userId AND isActive = 1 AND keyExpire > :currentTime")
    suspend fun getActiveKeysByUserId(userId: Long, currentTime: Long): List<KeyEntity>

    @Query("SELECT * FROM key_table WHERE user_id = :userId ORDER BY createdAt DESC")
    suspend fun getKeysByUserId(userId: Long): List<KeyEntity>
}