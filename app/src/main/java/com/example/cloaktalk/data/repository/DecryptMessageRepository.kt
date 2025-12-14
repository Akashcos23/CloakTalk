package com.example.cloaktalk.data.repository

    import com.example.cloaktalk.data.local.dao.DecryptMessageDao
    import com.example.cloaktalk.data.local.dao.DecryptMessageComplete
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

        fun getDecryptMessagesByEncryptId(encryptId: Long): Flow<List<DecryptMessageEntity>> {
            return decryptMessageDao.getDecryptMessagesByEncryptId(encryptId)
        }

        suspend fun getDecryptMessagesByEncryptIdList(encryptId: Long): List<DecryptMessageEntity> {
            return decryptMessageDao.getDecryptMessagesByEncryptIdList(encryptId)
        }

        suspend fun getDecryptMessagesByKeyId(keyId: Long): List<DecryptMessageEntity> {
            return decryptMessageDao.getDecryptMessagesByKeyId(keyId)
        }

        suspend fun getDecryptMessageComplete(decryptId: Long): DecryptMessageComplete? {
            return decryptMessageDao.getDecryptMessageComplete(decryptId)
        }

        suspend fun getDecryptMessagesCompleteByEncryptId(encryptId: Long): List<DecryptMessageComplete> {
            return decryptMessageDao.getDecryptMessagesCompleteByEncryptId(encryptId)
        }

        suspend fun deleteDecryptMessageById(decryptId: Long) {
            decryptMessageDao.deleteDecryptMessageById(decryptId)
        }

        suspend fun deleteDecryptMessagesByEncryptId(encryptId: Long) {
            decryptMessageDao.deleteAllDecryptMessagesByEncryptId(encryptId)
        }
    }