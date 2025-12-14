package com.example.cloaktalk.data.repository

import com.example.cloaktalk.data.local.dao.DesignAlgorithmDao
import com.example.cloaktalk.data.local.dao.DesignAlgorithmWithBase
import com.example.cloaktalk.data.local.dao.DesignAlgorithmWithUser
import com.example.cloaktalk.data.local.entity.DesignAlgorithmEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Design Algorithm data operations.
 * Acts as a single source of truth for user-created algorithm data.
 *
 * This repository provides:
 * - CRUD operations for custom encryption algorithms
 * - Validation to prevent duplicate algorithm names per user
 * - Query methods for filtering algorithms by user or base algorithm
 * - Join queries for retrieving algorithms with related data
 *
 * @property designAlgorithmDao The DAO for design algorithm database operations
 */
class DesignAlgorithmRepository(private val designAlgorithmDao: DesignAlgorithmDao) {

    /**
     * Create a new design algorithm with all required fields.
     *
     * This method validates that the algorithm name doesn't already exist
     * for the specified user before inserting into the database.
     *
     * @param algoName The unique name for the algorithm (per user)
     * @param baseAlgoName The base algorithm this is built upon (must match base_algorithm table: "Caesar", "Vigenere", or "Substitution")
     * @param charset The allowed character set as a concatenated string
     * @param userId The ID of the user creating this algorithm (foreign key to users table)
     * @param shiftAmount The shift amount for Caesar/Vigenère algorithms (nullable for Substitution)
     * @param multipleRounds Whether to apply multiple encryption rounds for added security
     * @return Algorithm ID (positive Long) if successful, -1L if name already exists for user
     * @throws Exception if database operation fails (e.g., foreign key constraint violation)
     */
    suspend fun createDesignAlgorithm(
        algoName: String,
        baseAlgoName: String,
        charset: String,
        userId: Long,
        shiftAmount: Int?,
        multipleRounds: Boolean
    ): Long {
        // Check if algorithm name already exists for this user
        if (designAlgorithmDao.algorithmNameExistsForUser(algoName, userId)) {
            return -1L
        }

        // Create entity with all required fields
        val algorithm = DesignAlgorithmEntity(
            algoName = algoName,
            baseAlgoName = baseAlgoName,
            charset = charset,
            userId = userId,
            shiftAmount = shiftAmount,
            multipleRounds = multipleRounds
        )

        return designAlgorithmDao.insertDesignAlgorithm(algorithm)
    }

    /**
     * Update an existing design algorithm.
     * @param algorithm The design algorithm entity to update
     */
    suspend fun updateDesignAlgorithm(algorithm: DesignAlgorithmEntity) {
        designAlgorithmDao.updateDesignAlgorithm(algorithm)
    }

    /**
     * Delete a design algorithm.
     * @param algorithm The design algorithm entity to delete
     */
    suspend fun deleteDesignAlgorithm(algorithm: DesignAlgorithmEntity) {
        designAlgorithmDao.deleteDesignAlgorithm(algorithm)
    }

    /**
     * Delete a design algorithm by its ID.
     * @param algorithmId The algorithm ID to delete
     */
    suspend fun deleteDesignAlgorithmById(algorithmId: Long) {
        designAlgorithmDao.deleteDesignAlgorithmById(algorithmId)
    }

    /**
     * Get all design algorithms as a Flow.
     * @return Flow emitting list of all design algorithms
     */
    fun getAllDesignAlgorithms(): Flow<List<DesignAlgorithmEntity>> {
        return designAlgorithmDao.getAllDesignAlgorithms()
    }

    /**
     * Get a design algorithm by its ID.
     * @param algorithmId The algorithm ID to search for
     * @return The design algorithm entity if found, null otherwise
     */
    suspend fun getDesignAlgorithmById(algorithmId: Long): DesignAlgorithmEntity? {
        return designAlgorithmDao.getDesignAlgorithmById(algorithmId)
    }

    /**
     * Get a design algorithm by its name.
     * @param algoName The algorithm name to search for
     * @return The design algorithm entity if found, null otherwise
     */
    suspend fun getDesignAlgorithmByName(algoName: String): DesignAlgorithmEntity? {
        return designAlgorithmDao.getDesignAlgorithmByName(algoName)
    }

    /**
     * Get all design algorithms for a specific user as a Flow.
     * @param userId The user ID to filter by
     * @return Flow emitting list of design algorithms for the user
     */
    fun getDesignAlgorithmsByUserId(userId: Long): Flow<List<DesignAlgorithmEntity>> {
        return designAlgorithmDao.getDesignAlgorithmsByUserId(userId)
    }

    /**
     * Get all design algorithms for a specific user as a list.
     * @param userId The user ID to filter by
     * @return List of design algorithms for the user
     */
    suspend fun getDesignAlgorithmsByUserIdList(userId: Long): List<DesignAlgorithmEntity> {
        return designAlgorithmDao.getDesignAlgorithmsByUserIdList(userId)
    }

    /**
     * Get design algorithms based on a specific base algorithm.
     * @param baseAlgoName The base algorithm name to filter by
     * @return Flow emitting list of design algorithms using that base
     */
    fun getDesignAlgorithmsByBaseAlgo(baseAlgoName: String): Flow<List<DesignAlgorithmEntity>> {
        return designAlgorithmDao.getDesignAlgorithmsByBaseAlgo(baseAlgoName)
    }

    /**
     * Get the count of design algorithms for a user.
     * @param userId The user ID to count for
     * @return The total count of design algorithms for the user
     */
    suspend fun getDesignAlgorithmCountByUser(userId: Long): Int {
        return designAlgorithmDao.getDesignAlgorithmCountByUser(userId)
    }

    /**
     * Get design algorithms with base algorithm info.
     * @param userId The user ID to filter by
     * @return List of design algorithms with base algorithm names
     */
    suspend fun getDesignAlgorithmsWithBaseInfo(userId: Long): List<DesignAlgorithmWithBase> {
        return designAlgorithmDao.getDesignAlgorithmsWithBaseInfo(userId)
    }

    /**
     * Get all design algorithms with user info.
     * @return List of design algorithms with user details
     */
    suspend fun getAllDesignAlgorithmsWithUserInfo(): List<DesignAlgorithmWithUser> {
        return designAlgorithmDao.getAllDesignAlgorithmsWithUserInfo()
    }

    /**
     * Delete all design algorithms for a user.
     * @param userId The user ID whose algorithms to delete
     */
    suspend fun deleteAllDesignAlgorithmsByUser(userId: Long) {
        designAlgorithmDao.deleteAllDesignAlgorithmsByUser(userId)
    }
}
