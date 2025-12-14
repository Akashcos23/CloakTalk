package com.example.cloaktalk.data.repository

    import com.example.cloaktalk.data.local.dao.HistoryComplete
    import com.example.cloaktalk.data.local.dao.HistoryDao
    import com.example.cloaktalk.data.local.entity.HistoryEntity
    import kotlinx.coroutines.flow.Flow

    /**
     * Repository for history data operations.
     */
    class HistoryRepository(private val historyDao: HistoryDao) {

        suspend fun insertHistory(history: HistoryEntity): Long {
            return historyDao.insertHistory(history)
        }

        suspend fun deleteHistory(history: HistoryEntity) {
            historyDao.deleteHistory(history)
        }

        suspend fun getHistoryById(historyId: Long): HistoryEntity? {
            return historyDao.getHistoryById(historyId)
        }

        fun getAllHistory(): Flow<List<HistoryEntity>> {
            return historyDao.getAllHistory()
        }

        fun getHistoryByEncryptId(encryptId: Long): Flow<List<HistoryEntity>> {
            return historyDao.getHistoryByEncryptId(encryptId)
        }

        suspend fun getHistoryByDecryptId(decryptId: Long): List<HistoryEntity> {
            return historyDao.getHistoryByDecryptId(decryptId)
        }

        suspend fun getHistoryByKeyId(keyId: Long): List<HistoryEntity> {
            return historyDao.getHistoryByKeyId(keyId)
        }

        suspend fun getHistoryComplete(historyId: Long): HistoryComplete? {
            return historyDao.getHistoryComplete(historyId)
        }

        suspend fun getAllHistoryComplete(): List<HistoryComplete> {
            return historyDao.getAllHistoryComplete()
        }

        suspend fun getHistoryCompleteByUser(userId: Long): List<HistoryComplete> {
            return historyDao.getHistoryCompleteByUser(userId)
        }

        suspend fun deleteHistoryById(historyId: Long) {
            historyDao.deleteHistoryById(historyId)
        }

        suspend fun deleteAllHistory() {
            historyDao.deleteAllHistory()
        }
    }