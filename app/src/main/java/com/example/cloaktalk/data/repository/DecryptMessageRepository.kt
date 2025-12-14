package com.example.cloaktalk.data.repository

            import com.example.cloaktalk.data.local.dao.DecryptMessageDao
            import com.example.cloaktalk.data.local.entity.DecryptMessageEntity
            import kotlinx.coroutines.flow.Flow

            /**
             * Repository for decrypt message data operations.
             */
            class DecryptMessageRepository(private val decryptMessageDao: DecryptMessageDao) {

                suspend fun insertDecryptMessage(decryptMessage: DecryptMessageEntity): Long {
                    return decryptMessageDao.insertDecryptMessage(decryptMessage)
                }

                suspend fun updateDecryptMessage(decryptMessage: DecryptMessageEntity) {
                    decryptMessageDao.updateDecryptMessage(decryptMessage)
                }

                suspend fun deleteDecryptMessage(decryptMessage: DecryptMessageEntity) {
                    decryptMessageDao.deleteDecryptMessage(decryptMessage)
                }

                suspend fun getDecryptMessageById(decryptId: Long): DecryptMessageEntity? {
                    return decryptMessageDao.getDecryptMessageById(decryptId)
                }

                fun getAllDecryptMessages(): Flow<List<DecryptMessageEntity>> {
                    return decryptMessageDao.getAllDecryptMessages()
                }

                suspend fun getDecryptMessagesByKeyId(keyId: Long): List<DecryptMessageEntity> {
                    return decryptMessageDao.getDecryptMessagesByKeyId(keyId)
                }

                suspend fun deleteDecryptMessageById(decryptId: Long) {
                    decryptMessageDao.deleteDecryptMessageById(decryptId)
                }
            }