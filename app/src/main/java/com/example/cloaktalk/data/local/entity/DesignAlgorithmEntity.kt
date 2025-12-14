package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Design Algorithm entity - stores user-created custom algorithms
 */
@Entity(
    tableName = "design_algorithm",
    foreignKeys = [
        ForeignKey(
            entity = BaseAlgorithmEntity::class,
            parentColumns = ["baseAlgoName"],
            childColumns = ["baseAlgoName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("baseAlgoName"), Index("userId")]
)
data class DesignAlgorithmEntity(
    @PrimaryKey(autoGenerate = true)
    val algorithmId: Long = 0,
    val algoName: String,
    val baseAlgoName: String,
    val charset: String, // Stores allowed character set
    val userId: Long,
    val shiftAmount: Int?, // For Caesar and Vigenère algorithms
    val multipleRounds: Boolean
)