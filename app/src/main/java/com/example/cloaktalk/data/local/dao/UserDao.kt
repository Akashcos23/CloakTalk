package com.example.cloaktalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cloaktalk.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations.
 * Provides methods for CRUD operations on the users table.
 */
@Dao
interface UserDao {

    /**
     * Insert a new user into the database.
     * @param user The user entity to insert
     * @return The row ID of the inserted user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Update an existing user in the database.
     * @param user The user entity to update
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Delete a user from the database.
     * @param user The user entity to delete
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)

    /**
     * Get a user by their email address.
     * @param email The email to search for
     * @return The user entity if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Get a user by their ID.
     * @param id The user ID to search for
     * @return The user entity if found, null otherwise
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    /**
     * Get all users as a Flow for observing changes.
     * @return Flow emitting list of all users
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * Check if a user exists with the given email and password.
     * @param email User's email
     * @param password User's password
     * @return The user entity if credentials match, null otherwise
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    /**
     * Check if email already exists in database.
     * @param email The email to check
     * @return True if email exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean

    /**
     * Delete all users from the database.
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}