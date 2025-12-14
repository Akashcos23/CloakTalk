package com.example.cloaktalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.cloaktalk.data.local.entity.DesignAlgorithmEntity

/**
 * Data Access Object for Design Algorithm operations.
 * Provides methods for CRUD operations on the design_algorithm table.
 * Includes join queries for related data.
 */
@Dao
interface DesignAlgorithmDao {

    /**
     * Insert a new design algorithm into the database.
     * @param algorithm The design algorithm entity to insert
     * @return The row ID of the inserted algorithm
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesignAlgorithm(algorithm: DesignAlgorithmEntity): Long

    /**
     * Update an existing design algorithm.
     * @param algorithm The design algorithm entity to update
     */
    @Update
    suspend fun updateDesignAlgorithm(algorithm: DesignAlgorithmEntity)

    /**
     * Delete a design algorithm from the database.
     * @param algorithm The design algorithm entity to delete
     */
    @Delete
    suspend fun deleteDesignAlgorithm(algorithm: DesignAlgorithmEntity)

    /**
     * Get all design algorithms as a Flow for observing changes.
     * @return Flow emitting list of all design algorithms
     */
    @Query("SELECT * FROM design_algorithm")
    fun getAllDesignAlgorithms(): Flow<List<DesignAlgorithmEntity>>

    /**
     * Get a design algorithm by its ID.
     * @param algorithmId The algorithm ID to search for
     * @return The design algorithm entity if found, null otherwise
     */
    @Query("SELECT * FROM design_algorithm WHERE algorithmId = :algorithmId LIMIT 1")
    suspend fun getDesignAlgorithmById(algorithmId: Long): DesignAlgorithmEntity?

    /**
     * Get a design algorithm by its name.
     * @param algoName The algorithm name to search for
     * @return The design algorithm entity if found, null otherwise
     */
    @Query("SELECT * FROM design_algorithm WHERE algoName = :algoName LIMIT 1")
    suspend fun getDesignAlgorithmByName(algoName: String): DesignAlgorithmEntity?

    /**
     * Get all design algorithms created by a specific user.
     * @param userId The user ID to filter by
     * @return Flow emitting list of design algorithms for the user
     */
    @Query("SELECT * FROM design_algorithm WHERE userId = :userId")
    fun getDesignAlgorithmsByUserId(userId: Long): Flow<List<DesignAlgorithmEntity>>

    /**
     * Get all design algorithms created by a specific user as a one-time list.
     * @param userId The user ID to filter by
     * @return List of design algorithms for the user
     */
    @Query("SELECT * FROM design_algorithm WHERE userId = :userId")
    suspend fun getDesignAlgorithmsByUserIdList(userId: Long): List<DesignAlgorithmEntity>

    /**
     * Get all design algorithms based on a specific base algorithm.
     * @param baseAlgoName The base algorithm name to filter by
     * @return Flow emitting list of design algorithms using that base
     */
    @Query("SELECT * FROM design_algorithm WHERE baseAlgoName = :baseAlgoName")
    fun getDesignAlgorithmsByBaseAlgo(baseAlgoName: String): Flow<List<DesignAlgorithmEntity>>

    /**
     * Check if an algorithm name already exists for a user.
     * @param algoName The algorithm name to check
     * @param userId The user ID to check for
     * @return True if algorithm name exists for the user, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM design_algorithm WHERE algoName = :algoName AND userId = :userId)")
    suspend fun algorithmNameExistsForUser(algoName: String, userId: Long): Boolean

    /**
     * Get the count of design algorithms for a specific user.
     * @param userId The user ID to count for
     * @return The total count of design algorithms for the user
     */
    @Query("SELECT COUNT(*) FROM design_algorithm WHERE userId = :userId")
    suspend fun getDesignAlgorithmCountByUser(userId: Long): Int

    /**
     * Delete all design algorithms for a specific user.
     * @param userId The user ID whose algorithms to delete
     */
    @Query("DELETE FROM design_algorithm WHERE userId = :userId")
    suspend fun deleteAllDesignAlgorithmsByUser(userId: Long)

    /**
     * Delete a design algorithm by its ID.
     * @param algorithmId The algorithm ID to delete
     */
    @Query("DELETE FROM design_algorithm WHERE algorithmId = :algorithmId")
    suspend fun deleteDesignAlgorithmById(algorithmId: Long)

    /**
     * Join query: Get design algorithms with their base algorithm info.
     * @param userId The user ID to filter by
     * @return List of design algorithms with base algorithm names
     */
    @Query("""
        SELECT da.*, ba.baseAlgoName as baseAlgorithmName 
        FROM design_algorithm da 
        INNER JOIN base_algorithm ba ON da.baseAlgoName = ba.baseAlgoName 
        WHERE da.userId = :userId
    """)
    suspend fun getDesignAlgorithmsWithBaseInfo(userId: Long): List<DesignAlgorithmWithBase>

    /**
     * Join query: Get all design algorithms with user information.
     * @return List of design algorithms with user details
     */
    @Query("""
        SELECT da.*, u.username, u.email 
        FROM design_algorithm da 
        INNER JOIN users u ON da.userId = u.id
    """)
    suspend fun getAllDesignAlgorithmsWithUserInfo(): List<DesignAlgorithmWithUser>
}

/**
 * Data class for design algorithm with base algorithm info.
 */
data class DesignAlgorithmWithBase(
    val algorithmId: Long,
    val algoName: String,
    val baseAlgoName: String,
    val charset: String,
    val userId: Long,
    val baseAlgorithmName: String
)

/**
 * Data class for design algorithm with user info.
 */
data class DesignAlgorithmWithUser(
    val algorithmId: Long,
    val algoName: String,
    val baseAlgoName: String,
    val charset: String,
    val userId: Long,
    val username: String,
    val email: String
)