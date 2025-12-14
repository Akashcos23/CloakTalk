package com.example.cloaktalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cloaktalk.data.repository.BaseAlgorithmRepository
import com.example.cloaktalk.data.repository.DesignAlgorithmRepository
import com.example.cloaktalk.data.repository.HistoryRepository
import com.example.cloaktalk.data.repository.KeyRepository
import com.example.cloaktalk.data.repository.EncryptMessageRepository
import com.example.cloaktalk.data.repository.DecryptMessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a recent activity item for display on home screen.
 */
data class RecentActivity(
    val id: Long,
    val message: String,
    val algorithm: String,
    val date: String,
    val type: ActivityType,
    val isKeyActive: Boolean
)

enum class ActivityType {
    ENCRYPT,
    DECRYPT
}

/**
 * Data class representing home screen statistics.
 */
data class HomeStats(
    val encryptionCount: Int = 0,
    val activeKeyCount: Int = 0,
    val algorithmCount: Int = 0
)

/**
 * ViewModel for the Home Screen.
 * Manages loading of user-specific statistics and recent activity.
 */
class HomeViewModel(
    private val historyRepository: HistoryRepository,
    private val keyRepository: KeyRepository,
    private val encryptMessageRepository: EncryptMessageRepository,
    private val decryptMessageRepository: DecryptMessageRepository,
    private val baseAlgorithmRepository: BaseAlgorithmRepository,
    private val designAlgorithmRepository: DesignAlgorithmRepository,
    private val userId: Long
) : ViewModel() {

    private val _homeStats = MutableStateFlow(HomeStats())
    val homeStats: StateFlow<HomeStats> = _homeStats.asStateFlow()

    private val _recentActivities = MutableStateFlow<List<RecentActivity>>(emptyList())
    val recentActivities: StateFlow<List<RecentActivity>> = _recentActivities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadHomeData()
    }

    /**
     * Loads all home screen data including stats and recent activities.
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load stats
                loadStats()
                
                // Load recent activities
                loadRecentActivities()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads statistics for the current user.
     */
    private suspend fun loadStats() {
        // Get user's history to count encryptions
        val userHistory = historyRepository.getHistoryByUserId(userId)
        
        // Count unique encryption operations (where encryptId is not 0)
        val encryptionCount = userHistory.map { it.encryptId }.filter { it > 0 }.distinct().size
        
        // Get active key count for user
        val activeKeyCount = keyRepository.getActiveKeyCountByUserId(userId)
        
        // Get total algorithm count (base + design algorithms)
        val baseAlgorithms = baseAlgorithmRepository.getAllBaseAlgorithms().first()
        val designAlgorithms = designAlgorithmRepository.getAllDesignAlgorithms().first()
        val algorithmCount = baseAlgorithms.size + designAlgorithms.filter { it.algoName.isNotBlank() }.size
        
        _homeStats.value = HomeStats(
            encryptionCount = encryptionCount,
            activeKeyCount = activeKeyCount,
            algorithmCount = algorithmCount
        )
    }

    /**
     * Loads the 3 most recent activities for the current user.
     */
    private suspend fun loadRecentActivities() {
        val activities = mutableListOf<RecentActivity>()
        
        // Get user's history sorted by most recent
        val userHistory = historyRepository.getHistoryByUserId(userId)
        
        // Process history records (take latest 3)
        for (historyRecord in userHistory.take(6)) {
            // Check if this is a decrypt record
            if (historyRecord.decryptId != null && historyRecord.decryptId > 0) {
                val decryptRecord = decryptMessageRepository.getDecryptMessageById(historyRecord.decryptId)
                if (decryptRecord != null) {
                    val keyEntity = keyRepository.getKeyById(historyRecord.keyId)
                    val isKeyActive = keyEntity?.let { 
                        it.isActive && it.keyExpire > System.currentTimeMillis() 
                    } ?: false
                    
                    activities.add(
                        RecentActivity(
                            id = historyRecord.historyId,
                            message = decryptRecord.decryptedMessage.take(30) + if (decryptRecord.decryptedMessage.length > 30) "..." else "",
                            algorithm = decryptRecord.algorithmName,
                            date = dateFormat.format(Date(historyRecord.createdAt)),
                            type = ActivityType.DECRYPT,
                            isKeyActive = isKeyActive
                        )
                    )
                }
            } else {
                // This is an encrypt record
                val encryptRecord = encryptMessageRepository.getEncryptMessageById(historyRecord.encryptId)
                if (encryptRecord != null) {
                    val keyEntity = keyRepository.getKeyById(historyRecord.keyId)
                    val isKeyActive = keyEntity?.let { 
                        it.isActive && it.keyExpire > System.currentTimeMillis() 
                    } ?: false
                    
                    activities.add(
                        RecentActivity(
                            id = historyRecord.historyId,
                            message = encryptRecord.originalMessage.take(30) + if (encryptRecord.originalMessage.length > 30) "..." else "",
                            algorithm = encryptRecord.algorithmName,
                            date = dateFormat.format(Date(historyRecord.createdAt)),
                            type = ActivityType.ENCRYPT,
                            isKeyActive = isKeyActive
                        )
                    )
                }
            }
            
            // Limit to 3 activities
            if (activities.size >= 3) break
        }
        
        _recentActivities.value = activities
    }

    /**
     * Refreshes the home data.
     */
    fun refresh() {
        loadHomeData()
    }
}

/**
 * Factory for creating HomeViewModel instances.
 */
class HomeViewModelFactory(
    private val historyRepository: HistoryRepository,
    private val keyRepository: KeyRepository,
    private val encryptMessageRepository: EncryptMessageRepository,
    private val decryptMessageRepository: DecryptMessageRepository,
    private val baseAlgorithmRepository: BaseAlgorithmRepository,
    private val designAlgorithmRepository: DesignAlgorithmRepository,
    private val userId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                historyRepository,
                keyRepository,
                encryptMessageRepository,
                decryptMessageRepository,
                baseAlgorithmRepository,
                designAlgorithmRepository,
                userId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
