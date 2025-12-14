package com.example.cloaktalk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.cloaktalk.data.local.entity.BaseAlgorithmEntity

/**
 * Data Access Object for Base Algorithm operations.
 * Provides methods for CRUD operations on the base_algorithm table.
 */
@Dao
interface BaseAlgorithmDao {

    /**
     * Insert a single base algorithm.
     * @param algorithm The base algorithm entity to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBaseAlgorithm(algorithm: BaseAlgorithmEntity)

    /**
     * Insert multiple base algorithms at once.
     * Used for prepopulating default cipher algorithms.
     * @param algorithms List of base algorithm entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllBaseAlgorithms(algorithms: List<BaseAlgorithmEntity>)

    /**
     * Get all base algorithms as a Flow for observing changes.
     * @return Flow emitting list of all base algorithms
     */
    @Query("SELECT * FROM base_algorithm")
    fun getAllBaseAlgorithms(): Flow<List<BaseAlgorithmEntity>>

    /**
     * Get all base algorithms as a one-time list.
     * @return List of all base algorithms
     */
    @Query("SELECT * FROM base_algorithm")
    suspend fun getAllBaseAlgorithmsList(): List<BaseAlgorithmEntity>

    /**
     * Get a base algorithm by its name.
     * @param name The algorithm name to search for
     * @return The base algorithm entity if found, null otherwise
     */
    @Query("SELECT * FROM base_algorithm WHERE baseAlgoName = :name LIMIT 1")
    suspend fun getBaseAlgorithmByName(name: String): BaseAlgorithmEntity?

    /**
     * Check if a base algorithm exists with the given name.
     * @param name The algorithm name to check
     * @return True if algorithm exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM base_algorithm WHERE baseAlgoName = :name)")
    suspend fun algorithmExists(name: String): Boolean

    /**
     * Get the count of base algorithms.
     * @return The total count of base algorithms
     */
    @Query("SELECT COUNT(*) FROM base_algorithm")
    suspend fun getBaseAlgorithmCount(): Int

    /**
     * Delete all base algorithms from the database.
     */
    @Query("DELETE FROM base_algorithm")
    suspend fun deleteAllBaseAlgorithms()
}