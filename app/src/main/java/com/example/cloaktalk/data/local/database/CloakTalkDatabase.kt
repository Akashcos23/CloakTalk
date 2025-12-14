package com.example.cloaktalk.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloaktalk.data.local.dao.UserDao
import com.example.cloaktalk.data.local.entity.UserEntity

/**
 * Room database for CloakTalk application.
 * Contains the users table for local user storage.
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CloakTalkDatabase : RoomDatabase() {

    /**
     * Get the UserDao for database operations.
     */
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "cloaktalk_database"

        @Volatile
        private var INSTANCE: CloakTalkDatabase? = null

        /**
         * Get singleton instance of the database.
         * Creates the database if it doesn't exist.
         *
         * @param context Application context
         * @return CloakTalkDatabase instance
         */
        // CHANGE: Use 'android.content.Context' directly here
        fun getInstance(context: Context): CloakTalkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CloakTalkDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
