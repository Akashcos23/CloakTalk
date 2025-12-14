package com.example.cloaktalk.data.local.database

        import android.content.Context
        import androidx.room.Database
        import androidx.room.Room
        import androidx.room.RoomDatabase
        import androidx.sqlite.db.SupportSQLiteDatabase
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
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.launch

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
            version = 2,
            exportSchema = false
        )
        abstract class CloakTalkDatabase : RoomDatabase() {

            abstract fun userDao(): UserDao
            abstract fun baseAlgorithmDao(): BaseAlgorithmDao
            abstract fun designAlgorithmDao(): DesignAlgorithmDao
            abstract fun keyDao(): KeyDao
            abstract fun encryptMessageDao(): EncryptMessageDao
            abstract fun decryptMessageDao(): DecryptMessageDao
            abstract fun historyDao(): HistoryDao

            companion object {
                private const val DATABASE_NAME = "cloaktalk_database"

                @Volatile
                private var INSTANCE: CloakTalkDatabase? = null

                fun getInstance(context: Context): CloakTalkDatabase {
                    return INSTANCE ?: synchronized(this) {
                        val instance = Room.databaseBuilder(
                            context.applicationContext,
                            CloakTalkDatabase::class.java,
                            DATABASE_NAME
                        )
                            .fallbackToDestructiveMigration()
                            .addCallback(DatabaseCallback())
                            .build()
                        INSTANCE = instance
                        instance
                    }
                }
            }

            private class DatabaseCallback : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    INSTANCE?.let { database ->
                        CoroutineScope(Dispatchers.IO).launch {
                            populateBaseAlgorithms(database.baseAlgorithmDao())
                        }
                    }
                }

                private suspend fun populateBaseAlgorithms(baseAlgorithmDao: BaseAlgorithmDao) {
                    val algorithms = listOf(
                        BaseAlgorithmEntity("Caesar Cipher"),
                        BaseAlgorithmEntity("Substitution Cipher"),
                        BaseAlgorithmEntity("Vigenère Cipher")
                    )
                    algorithms.forEach { baseAlgorithmDao.insertBaseAlgorithm(it) }
                }
            }
        }