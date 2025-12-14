package com.example.cloaktalk.data.repository

            import com.example.cloaktalk.data.local.dao.EncryptMessageDao
            import com.example.cloaktalk.data.local.dao.EncryptMessageWithKey
            import com.example.cloaktalk.data.local.entity.EncryptMessageEntity
            import kotlinx.coroutines.flow.Flow

            /**
             * Repository for Encrypt Message data operations.
             * Acts as a single source of truth for encrypted message data.
             *
             * @property encryptMessageDao The DAO for encrypt message database operations
             */
            class EncryptMessageRepository(private val encryptMessageDao: EncryptMessageDao) {

                /**
                 * Insert an encrypted message.
                 * @param encryptMessage The encrypt message entity to insert
                 * @return The encrypt message ID
                 */
                suspend fun insertEncryptMessage(encryptMessage: EncryptMessageEntity): Long {
                    return encryptMessageDao.insertEncryptMessage(encryptMessage)
                }

                /**
                 * Update an encrypted message.
                 * @param encryptMessage The encrypt message entity to update
                 */
                suspend fun updateEncryptMessage(encryptMessage: EncryptMessageEntity) {
                    encryptMessageDao.updateEncryptMessage(encryptMessage)
                }

                /**
                 * Delete an encrypted message.
                 * @param encryptMessage The encrypt message entity to delete
                 */
                suspend fun deleteEncryptMessage(encryptMessage: EncryptMessageEntity) {
                    encryptMessageDao.deleteEncryptMessage(encryptMessage)
                }

                /**
                 * Delete an encrypted message by its ID.
                 * @param encryptId The encrypt message ID to delete
                 */
                suspend fun deleteEncryptMessageById(encryptId: Long) {
                    encryptMessageDao.deleteEncryptMessageById(encryptId)
                }

                /**
                 * Get all encrypted messages as a Flow.
                 * @return Flow emitting list of all encrypted messages
                 */
                fun getAllEncryptMessages(): Flow<List<EncryptMessageEntity>> {
                    return encryptMessageDao.getAllEncryptMessages()
                }

                /**
                 * Get an encrypted message by its ID.
                 * @param encryptId The encrypt message ID to search for
                 * @return The encrypt message entity if found, null otherwise
                 */
                suspend fun getEncryptMessageById(encryptId: Long): EncryptMessageEntity? {
                    return encryptMessageDao.getEncryptMessageById(encryptId)
                }

                /**
                 * Get encrypted messages by algorithm name as a Flow.
                 * @param algorithmName The algorithm name to filter by
                 * @return Flow emitting list of encrypted messages using that algorithm
                 */
                fun getEncryptMessagesByAlgorithm(algorithmName: String): Flow<List<EncryptMessageEntity>> {
                    return encryptMessageDao.getEncryptMessagesByAlgorithm(algorithmName)
                }

                /**
                 * Get an encrypted message by its key ID.
                 * @param keyId The key ID to search for
                 * @return The encrypt message entity if found, null otherwise
                 */
                suspend fun getEncryptMessageByKeyId(keyId: Long): EncryptMessageEntity? {
                    return encryptMessageDao.getEncryptMessageByKeyId(keyId)
                }

                /**
                 * Get encrypted message with key details.
                 * @param encryptId The encrypt message ID
                 * @return Encrypted message with key information
                 */
                suspend fun getEncryptMessageWithKey(encryptId: Long): EncryptMessageWithKey? {
                    return encryptMessageDao.getEncryptMessageWithKey(encryptId)
                }
            }