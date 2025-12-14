package com.example.cloaktalk.data.repository

            import com.example.cloaktalk.data.local.dao.KeyDao
            import com.example.cloaktalk.data.local.entity.KeyEntity
            import kotlinx.coroutines.flow.Flow
            import java.security.SecureRandom

            /**
             * Repository for Key data operations.
             */
            class KeyRepository(private val keyDao: KeyDao) {

                /**
                 * Generate a unique random 8-digit key.
                 * Checks against existing keys to ensure uniqueness.
                 */
                suspend fun generateUniqueKey(): String {
                    val random = SecureRandom()
                    var key: String
                    do {
                        key = (1..8).map { random.nextInt(10) }.joinToString("")
                    } while (keyDao.isKeyExists(key))
                    return key
                }

                suspend fun insertKey(key: KeyEntity): Long {
                    return keyDao.insertKey(key)
                }

                suspend fun updateKey(key: KeyEntity) {
                    keyDao.updateKey(key)
                }

                suspend fun deleteKey(key: KeyEntity) {
                    keyDao.deleteKey(key)
                }

                suspend fun getKeyById(keyId: Long): KeyEntity? {
                    return keyDao.getKeyById(keyId)
                }

                suspend fun getKeyByValue(key: String): KeyEntity? {
                    return keyDao.getKeyByValue(key)
                }

                suspend fun getActiveKeyByValue(key: String): KeyEntity? {
                    // First deactivate expired keys
                    keyDao.deactivateExpiredKeys(System.currentTimeMillis())
                    return keyDao.getActiveKeyByValue(key)
                }

                suspend fun isKeyExists(key: String): Boolean {
                    return keyDao.isKeyExists(key)
                }

                fun getAllKeys(): Flow<List<KeyEntity>> {
                    return keyDao.getAllKeys()
                }

                fun getActiveKeys(): Flow<List<KeyEntity>> {
                    return keyDao.getActiveKeys()
                }

                suspend fun deactivateExpiredKeys() {
                    keyDao.deactivateExpiredKeys(System.currentTimeMillis())
                }

                suspend fun deactivateKey(keyId: Long) {
                    keyDao.deactivateKey(keyId)
                }

                suspend fun deleteKeyById(keyId: Long) {
                    keyDao.deleteKeyById(keyId)
                }

                /**
                 * Calculate expiration timestamp from hours.
                 */
                fun calculateExpirationTime(hours: Int): Long {
                    return System.currentTimeMillis() + (hours * 60 * 60 * 1000L)
                }
            }