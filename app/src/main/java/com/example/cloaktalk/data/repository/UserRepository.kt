package com.example.cloaktalk.data.repository

import com.example.cloaktalk.data.local.dao.UserDao
import com.example.cloaktalk.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user data operations.
 * Acts as a single source of truth for user data.
 *
 * @property userDao The DAO for user database operations
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Register a new user.
     * @return User ID if successful, -1 if email already exists
     */
    suspend fun registerUser(email: String, password: String, username: String): Long {
        if (userDao.emailExists(email)) {
            return -1L // Email already exists
        }
        val user = UserEntity(
            email = email,
            password = password,
            username = username
        )
        return userDao.insertUser(user)
    }

    /**
     * Login user with email and password.
     * @return UserEntity if credentials are valid, null otherwise
     */
    suspend fun loginUser(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }

    /**
     * Get user by email.
     */
    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    /**
     * Get all users as Flow.
     */
    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    /**
     * Update user information.
     */
    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    /**
     * Delete a user.
     */
    suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
    }
}