package com.example.cloaktalk.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database callback for pre-populating the base_algorithm table.
 *
 * This callback ensures that the three predefined base algorithms
 * (Caesar, Vigenere, Substitution) are always present in the database.
 * These are required as foreign key references for user-created
 * design algorithms.
 *
 * Uses onOpen() instead of onCreate() to handle:
 * - Fresh database creation
 * - Destructive migrations (fallbackToDestructiveMigration)
 * - Database recreation after app data clear
 *
 * IMPORTANT: The baseAlgoName values must match exactly with
 * DesignerScreen.getBaseAlgoName() mappings:
 * - "Caesar" (not "Caesar Cipher")
 * - "Vigenere" (not "Vigenère Cipher")
 * - "Substitution" (not "Substitution Cipher")
 */
class DatabaseCallback : RoomDatabase.Callback() {

    /**
     * Called every time the database is opened.
     *
     * Inserts the three base algorithms if they don't already exist.
     * Uses INSERT OR IGNORE to prevent duplicate key errors.
     *
     * This runs synchronously on the database thread to ensure
     * base algorithms exist before any queries attempt to
     * reference them as foreign keys.
     *
     * @param db The SQLite database instance
     */
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)

        // Insert Caesar cipher base algorithm
        // Used for simple shift-based encryption
        db.execSQL(
            """
            INSERT OR IGNORE INTO base_algorithm (baseAlgoName, description)
            VALUES ('Caesar', 'A simple substitution cipher that shifts letters by a fixed amount')
            """.trimIndent()
        )

        // Insert Vigenère cipher base algorithm
        // Used for polyalphabetic encryption with a keyword
        db.execSQL(
            """
            INSERT OR IGNORE INTO base_algorithm (baseAlgoName, description)
            VALUES ('Vigenere', 'A polyalphabetic cipher using a keyword for encryption')
            """.trimIndent()
        )

        // Insert Substitution cipher base algorithm
        // Used for character-to-character mapping
        db.execSQL(
            """
            INSERT OR IGNORE INTO base_algorithm (baseAlgoName, description)
            VALUES ('Substitution', 'A cipher that replaces each character with another character')
            """.trimIndent()
        )
    }
}
