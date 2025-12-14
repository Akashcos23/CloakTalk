package com.example.cloaktalk.data.local.database

              import android.content.Context
              import androidx.room.Database
              import androidx.room.Room
              import androidx.room.RoomDatabase
              import com.example.cloaktalk.data.local.dao.BaseAlgorithmDao
              import com.example.cloaktalk.data.local.dao.DecryptMessageDao
              import com.example.cloaktalk.data.local.dao.DesignAlgorithmDao
              import com.example.cloaktalk.data.local.dao.EncryptMessageDao
              import com.example.cloaktalk.data.local.dao.HistoryDao
              import com.example.cloaktalk.data.local.dao.KeyDao
              import com.example.cloaktalk.data.local.dao.UserDao
              import com.example.cloaktalk.data.local.entity.BaseAlgorithmEntity
              import com.example.cloaktalk.data.local.entity.DecryptMessageEntity
              import com.example.cloaktalk.data.local.entity.DesignAlgorithmEntity
              import com.example.cloaktalk.data.local.entity.EncryptMessageEntity
              import com.example.cloaktalk.data.local.entity.HistoryEntity
              import com.example.cloaktalk.data.local.entity.KeyEntity
              import com.example.cloaktalk.data.local.entity.UserEntity

              /**
               * Main Room database class for the CloakTalk application.
               *
               * This database manages all application data including:
               * - **Users**: User accounts for authentication and personalization
               * - **Base algorithms**: Predefined cipher algorithms (Caesar, Vigenère, Substitution)
               * - **Design algorithms**: User-created custom algorithms
               * - **Keys**: Encryption/decryption keys with expiration timestamps
               * - **Encrypted messages**: Records of encrypted messages
               * - **Decrypted messages**: Records of decrypted messages
               * - **History**: Log of all encryption/decryption operations
               *
               * The database is pre-populated with three base algorithms on first creation.
               *
               * @see DatabaseCallback for pre-population logic
               * @see UserEntity for user authentication
               *
               * Database Schema Version: 6
               */
              @Database(
                  entities = [
                      UserEntity::class,
                      BaseAlgorithmEntity::class,
                      DesignAlgorithmEntity::class,
                      KeyEntity::class,
                      EncryptMessageEntity::class,
                      DecryptMessageEntity::class,
                      HistoryEntity::class
                  ],
                  version = 8,
                  exportSchema = false
              )
              abstract class CloakTalkDatabase : RoomDatabase() {

                  /**
                   * Provides access to User table operations.
                   * Used for user registration, login, and profile management.
                   * @return DAO for user CRUD operations
                   * @see UserDao
                   * @see UserEntity
                   */
                  abstract fun userDao(): UserDao

                  /**
                   * Provides access to BaseAlgorithm table operations.
                   * @return DAO for base algorithm CRUD operations
                   */
                  abstract fun baseAlgorithmDao(): BaseAlgorithmDao

                  /**
                   * Provides access to DesignAlgorithm table operations.
                   * @return DAO for design algorithm CRUD operations
                   */
                  abstract fun designAlgorithmDao(): DesignAlgorithmDao

                  /**
                   * Provides access to Key table operations.
                   * @return DAO for key CRUD operations
                   */
                  abstract fun keyDao(): KeyDao

                  /**
                   * Provides access to EncryptMessage table operations.
                   * @return DAO for encrypted message CRUD operations
                   */
                  abstract fun encryptMessageDao(): EncryptMessageDao

                  /**
                   * Provides access to DecryptMessage table operations.
                   * @return DAO for decrypted message CRUD operations
                   */
                  abstract fun decryptMessageDao(): DecryptMessageDao

                  /**
                   * Provides access to History table operations.
                   * @return DAO for history CRUD operations
                   */
                  abstract fun historyDao(): HistoryDao

                  companion object {
                      /** Database name constant */
                      private const val DATABASE_NAME = "cloaktalk_database"

                      /** Volatile instance to ensure thread-safe singleton */
                      @Volatile
                      private var INSTANCE: CloakTalkDatabase? = null

                      /**
                       * Gets the singleton database instance.
                       * Creates the database if it doesn't exist.
                       *
                       * @param context Application context
                       * @return The singleton database instance
                       */
                      fun getInstance(context: Context): CloakTalkDatabase {
                          return INSTANCE ?: synchronized(this) {
                              INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                          }
                      }

                      /**
                       * Builds the database with callback for pre-population.
                       *
                       * @param context Application context
                       * @return The built database instance
                       */
                      private fun buildDatabase(context: Context): CloakTalkDatabase {
                          return Room.databaseBuilder(
                              context.applicationContext,
                              CloakTalkDatabase::class.java,
                              DATABASE_NAME
                          )
                              .addCallback(DatabaseCallback()) // No context needed - uses raw SQL
                              .fallbackToDestructiveMigration()
                              .build()
                      }

                      /**
                       * Clears the singleton instance.
                       * Useful for testing or when needing to recreate the database.
                       */
                      @Suppress("unused")
                      fun clearInstance() {
                          INSTANCE = null
                      }
                  }
              }