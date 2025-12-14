package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity representing a user in the local Room database.
 *
 * @property id Auto-generated primary key
 * @property email User's email address (unique identifier)
 * @property password User's password (should be hashed in production)
 * @property username User's display username
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val password: String,
    val username: String
)