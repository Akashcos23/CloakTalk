package com.example.cloaktalk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Base Algorithm entity - stores predefined cipher algorithms
 */
@Entity(tableName = "base_algorithm")
data class BaseAlgorithmEntity(
    @PrimaryKey
    val baseAlgoName: String // "Caesar Cipher", "Substitution Cipher", "Vigenère Cipher"
)