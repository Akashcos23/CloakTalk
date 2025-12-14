package com.example.cloaktalk.data.repository

import com.example.cloaktalk.data.local.dao.BaseAlgorithmDao
import com.example.cloaktalk.data.local.entity.BaseAlgorithmEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Base Algorithm data operations.
 * Acts as a single source of truth for base algorithm data.
 *
 * @property baseAlgorithmDao The DAO for base algorithm database operations
 */
class BaseAlgorithmRepository(private val baseAlgorithmDao: BaseAlgorithmDao) {

    /**
     * Get all base algorithms as a Flow.
     * @return Flow emitting list of all base algorithms
     */
    fun getAllBaseAlgorithms(): Flow<List<BaseAlgorithmEntity>> {
        return baseAlgorithmDao.getAllBaseAlgorithms()
    }

    /**
     * Get all base algorithms as a one-time list.
     * @return List of all base algorithms
     */
    suspend fun getAllBaseAlgorithmsList(): List<BaseAlgorithmEntity> {
        return baseAlgorithmDao.getAllBaseAlgorithmsList()
    }

    /**
     * Get a base algorithm by its name.
     * @param name The algorithm name to search for
     * @return The base algorithm entity if found, null otherwise
     */
    suspend fun getBaseAlgorithmByName(name: String): BaseAlgorithmEntity? {
        return baseAlgorithmDao.getBaseAlgorithmByName(name)
    }

    /**
     * Check if a base algorithm exists.
     * @param name The algorithm name to check
     * @return True if algorithm exists, false otherwise
     */
    suspend fun algorithmExists(name: String): Boolean {
        return baseAlgorithmDao.algorithmExists(name)
    }

    /**
     * Insert default base algorithms.
     * Called during database creation.
     */
    suspend fun insertDefaultAlgorithms() {
        val defaultAlgorithms = listOf(
            BaseAlgorithmEntity("Caesar Cipher"),
            BaseAlgorithmEntity("Substitution Cipher"),
            BaseAlgorithmEntity("Vigenère Cipher")
        )
        baseAlgorithmDao.insertAllBaseAlgorithms(defaultAlgorithms)
    }

    /**
     * Get the count of base algorithms.
     * @return The total count of base algorithms
     */
    suspend fun getBaseAlgorithmCount(): Int {
        return baseAlgorithmDao.getBaseAlgorithmCount()
    }
}