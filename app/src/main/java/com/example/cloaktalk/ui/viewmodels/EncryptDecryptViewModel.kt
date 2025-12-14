package com.example.cloaktalk.ui.viewmodel

                     import androidx.lifecycle.ViewModel
                     import androidx.lifecycle.ViewModelProvider
                     import androidx.lifecycle.viewModelScope
                     import com.example.cloaktalk.data.local.entity.DecryptMessageEntity
                     import com.example.cloaktalk.data.local.entity.EncryptMessageEntity
                     import com.example.cloaktalk.data.local.entity.KeyEntity
                     import com.example.cloaktalk.data.repository.BaseAlgorithmRepository
                     import com.example.cloaktalk.data.repository.DecryptMessageRepository
                     import com.example.cloaktalk.data.repository.DesignAlgorithmRepository
                     import com.example.cloaktalk.data.repository.EncryptMessageRepository
                     import com.example.cloaktalk.data.repository.KeyRepository
                     import kotlinx.coroutines.flow.MutableStateFlow
                     import kotlinx.coroutines.flow.StateFlow
                     import kotlinx.coroutines.flow.asStateFlow
                     import kotlinx.coroutines.flow.first
                     import kotlinx.coroutines.launch

                     /**
                      * Data class representing an algorithm option for encryption/decryption.
                      *
                      * @property id Unique identifier (algorithmId for design, baseAlgoName hashcode for base)
                      * @property name The display name of the algorithm
                      * @property isDesignAlgorithm Whether this is a custom-designed algorithm
                      * @property baseAlgoName The name of the base algorithm used
                      */
                     data class AlgorithmOption(
                         val id: Long,
                         val name: String,
                         val isDesignAlgorithm: Boolean,
                         val baseAlgoName: String
                     )

                     /**
                      * Data class representing the result of an encryption operation.
                      *
                      * @property encryptedMessage The encrypted text
                      * @property key The generated encryption key
                      * @property keyExpireTime The timestamp when the key expires
                      * @property success Whether the encryption was successful
                      * @property errorMessage Error message if encryption failed
                      */
                     data class EncryptionResult(
                         val encryptedMessage: String,
                         val key: String,
                         val keyExpireTime: Long,
                         val success: Boolean,
                         val errorMessage: String? = null
                     )

                     /**
                      * Data class representing the result of a decryption operation.
                      *
                      * @property decryptedMessage The decrypted text
                      * @property success Whether the decryption was successful
                      * @property errorMessage Error message if decryption failed
                      */
                     data class DecryptionResult(
                         val decryptedMessage: String,
                         val success: Boolean,
                         val errorMessage: String? = null
                     )

                     /**
                      * ViewModel for handling encryption and decryption operations.
                      *
                      * This ViewModel manages:
                      * - Loading available algorithms from both BaseAlgorithm and DesignAlgorithm tables
                      * - Performing encryption with unique key generation
                      * - Performing decryption with key validation
                      * - Storing encryption/decryption records in the database
                      *
                      * @property baseAlgorithmRepository Repository for base algorithm operations
                      * @property designAlgorithmRepository Repository for design algorithm operations
                      * @property keyRepository Repository for key operations
                      * @property encryptMessageRepository Repository for encrypted message operations
                      * @property decryptMessageRepository Repository for decrypted message operations
                      */
                     class EncryptDecryptViewModel(
                         private val baseAlgorithmRepository: BaseAlgorithmRepository,
                         private val designAlgorithmRepository: DesignAlgorithmRepository,
                         private val keyRepository: KeyRepository,
                         private val encryptMessageRepository: EncryptMessageRepository,
                         private val decryptMessageRepository: DecryptMessageRepository
                     ) : ViewModel() {

                         /** StateFlow containing the list of available algorithms */
                         private val _algorithms = MutableStateFlow<List<AlgorithmOption>>(emptyList())
                         val algorithms: StateFlow<List<AlgorithmOption>> = _algorithms.asStateFlow()

                         /** StateFlow containing the result of the last encryption operation */
                         private val _encryptionResult = MutableStateFlow<EncryptionResult?>(null)
                         val encryptionResult: StateFlow<EncryptionResult?> = _encryptionResult.asStateFlow()

                         /** StateFlow containing the result of the last decryption operation */
                         private val _decryptionResult = MutableStateFlow<DecryptionResult?>(null)
                         val decryptionResult: StateFlow<DecryptionResult?> = _decryptionResult.asStateFlow()

                         /** StateFlow indicating whether an operation is in progress */
                         private val _isLoading = MutableStateFlow(false)
                         val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

                         init {
                             loadAlgorithms()
                         }

                         /**
                          * Loads all available algorithms from both BaseAlgorithm and DesignAlgorithm tables.
                          *
                          * Base algorithms are loaded first, followed by design algorithms.
                          * Design algorithms with null or blank names are excluded.
                          */
                         private fun loadAlgorithms() {
                             viewModelScope.launch {
                                 val algorithmList = mutableListOf<AlgorithmOption>()

                                 try {
                                     // Load base algorithms using Flow
                                     // BaseAlgorithmEntity has baseAlgoName as primary key (String)
                                     val baseAlgorithms = baseAlgorithmRepository.getAllBaseAlgorithms().first()
                                     baseAlgorithms.forEach { base ->
                                         algorithmList.add(
                                             AlgorithmOption(
                                                 id = base.baseAlgoName.hashCode().toLong(),
                                                 name = base.baseAlgoName,
                                                 isDesignAlgorithm = false,
                                                 baseAlgoName = base.baseAlgoName
                                             )
                                         )
                                     }

                                     // Load design algorithms using Flow (only non-null/blank names)
                                     // DesignAlgorithmEntity has algorithmId (Long) and algoName (String)
                                     val designAlgorithms = designAlgorithmRepository.getAllDesignAlgorithms().first()
                                     designAlgorithms.forEach { design ->
                                         if (design.algoName.isNotBlank()) {
                                             algorithmList.add(
                                                 AlgorithmOption(
                                                     id = design.algorithmId,
                                                     name = design.algoName,
                                                     isDesignAlgorithm = true,
                                                     baseAlgoName = design.baseAlgoName
                                                 )
                                             )
                                         }
                                     }
                                 } catch (e: Exception) {
                                     // Log error or handle gracefully
                                     e.printStackTrace()
                                 }

                                 _algorithms.value = algorithmList
                             }
                         }

                         /**
                          * Encrypts a message using the specified algorithm and stores the result.
                          *
                          * This function:
                          * 1. Generates a unique 8-digit key
                          * 2. Calculates the key expiration time
                          * 3. Performs the encryption using the provided function
                          * 4. Stores the key in the Key table
                          * 5. Stores the encrypted message in the EncryptMessage table
                          *
                          * @param plaintext The message to encrypt
                          * @param selectedAlgorithm The algorithm to use for encryption
                          * @param keyExpireHours Number of hours until the key expires
                          * @param encryptFunction Function that performs the actual encryption (plaintext, algorithmName, key) -> encryptedText
                          */
                         fun encryptMessage(
                             plaintext: String,
                             selectedAlgorithm: AlgorithmOption,
                             keyExpireHours: Int,
                             encryptFunction: (String, String, String) -> String
                         ) {
                             viewModelScope.launch {
                                 _isLoading.value = true
                                 try {
                                     // Generate unique 8-digit key
                                     val uniqueKey = keyRepository.generateUniqueKey()

                                     // Calculate expiration time based on hours
                                     val expirationTime = keyRepository.calculateExpirationTime(keyExpireHours)

                                     // Perform encryption using the provided function
                                     val encryptedText = encryptFunction(plaintext, selectedAlgorithm.name, uniqueKey)

                                     // Create and save key entity to database
                                     // KeyEntity uses baseAlgoName (String) not baseAlgorithmId (Long)
                                     val keyEntity = KeyEntity(
                                         key = uniqueKey,
                                         baseAlgoName = selectedAlgorithm.baseAlgoName,
                                         designAlgorithmId = if (selectedAlgorithm.isDesignAlgorithm) selectedAlgorithm.id else null,
                                         keyExpire = expirationTime,
                                         isActive = true
                                     )
                                     val keyId = keyRepository.insertKey(keyEntity)

                                     // Create and save encrypted message entity
                                     val encryptMessageEntity = EncryptMessageEntity(
                                         originalMessage = plaintext,
                                         encryptedMessage = encryptedText,
                                         keyId = keyId,
                                         algorithmName = selectedAlgorithm.name
                                     )
                                     encryptMessageRepository.insertEncryptMessage(encryptMessageEntity)

                                     // Update result state with success
                                     _encryptionResult.value = EncryptionResult(
                                         encryptedMessage = encryptedText,
                                         key = uniqueKey,
                                         keyExpireTime = expirationTime,
                                         success = true
                                     )
                                 } catch (e: Exception) {
                                     // Update result state with failure
                                     _encryptionResult.value = EncryptionResult(
                                         encryptedMessage = "",
                                         key = "",
                                         keyExpireTime = 0,
                                         success = false,
                                         errorMessage = e.message ?: "Encryption failed"
                                     )
                                 } finally {
                                     _isLoading.value = false
                                 }
                             }
                         }

                         /**
                          * Decrypts a message using the specified algorithm and key.
                          *
                          * This function:
                          * 1. Validates that the key exists and is active (not expired)
                          * 2. Performs the decryption using the provided function
                          * 3. Stores the decryption record in the DecryptMessage table
                          *
                          * @param encryptedText The encrypted message to decrypt
                          * @param key The decryption key (8-digit)
                          * @param selectedAlgorithm The algorithm to use for decryption
                          * @param decryptFunction Function that performs the actual decryption (encryptedText, algorithmName, key) -> decryptedText
                          */
                         fun decryptMessage(
                             encryptedText: String,
                             key: String,
                             selectedAlgorithm: AlgorithmOption,
                             decryptFunction: (String, String, String) -> String
                         ) {
                             viewModelScope.launch {
                                 _isLoading.value = true
                                 try {
                                     // Check if key exists and is active (also deactivates expired keys)
                                     val keyEntity = keyRepository.getActiveKeyByValue(key)

                                     if (keyEntity == null) {
                                         // Key is invalid, expired, or doesn't exist
                                         _decryptionResult.value = DecryptionResult(
                                             decryptedMessage = "",
                                             success = false,
                                             errorMessage = "Invalid or expired key. Decryption not allowed."
                                         )
                                         return@launch
                                     }

                                     // Perform decryption using the provided function
                                     val decryptedText = decryptFunction(encryptedText, selectedAlgorithm.name, key)

                                     // Create and save decryption record
                                     val decryptMessageEntity = DecryptMessageEntity(
                                         encryptedMessage = encryptedText,
                                         decryptedMessage = decryptedText,
                                         keyId = keyEntity.keyId,
                                         algorithmName = selectedAlgorithm.name,
                                         isSuccessful = true
                                     )
                                     decryptMessageRepository.insertDecryptMessage(decryptMessageEntity)

                                     // Update result state with success
                                     _decryptionResult.value = DecryptionResult(
                                         decryptedMessage = decryptedText,
                                         success = true
                                     )
                                 } catch (e: Exception) {
                                     // Update result state with failure
                                     _decryptionResult.value = DecryptionResult(
                                         decryptedMessage = "",
                                         success = false,
                                         errorMessage = e.message ?: "Decryption failed"
                                     )
                                 } finally {
                                     _isLoading.value = false
                                 }
                             }
                         }

                         /**
                          * Clears the current encryption result.
                          * Call this when starting a new encryption operation.
                          */
                         fun clearEncryptionResult() {
                             _encryptionResult.value = null
                         }

                         /**
                          * Clears the current decryption result.
                          * Call this when starting a new decryption operation.
                          */
                         fun clearDecryptionResult() {
                             _decryptionResult.value = null
                         }

                         /**
                          * Refreshes the list of available algorithms.
                          * Call this when new algorithms are added to the database.
                          */
                         @Suppress("unused")
                         fun refreshAlgorithms() {
                             loadAlgorithms()
                         }
                     }

                     /**
                      * Factory for creating EncryptDecryptViewModel instances with required dependencies.
                      *
                      * @property baseAlgorithmRepository Repository for base algorithm operations
                      * @property designAlgorithmRepository Repository for design algorithm operations
                      * @property keyRepository Repository for key operations
                      * @property encryptMessageRepository Repository for encrypted message operations
                      * @property decryptMessageRepository Repository for decrypted message operations
                      */
                     class EncryptDecryptViewModelFactory(
                         private val baseAlgorithmRepository: BaseAlgorithmRepository,
                         private val designAlgorithmRepository: DesignAlgorithmRepository,
                         private val keyRepository: KeyRepository,
                         private val encryptMessageRepository: EncryptMessageRepository,
                         private val decryptMessageRepository: DecryptMessageRepository
                     ) : ViewModelProvider.Factory {
                         @Suppress("UNCHECKED_CAST")
                         override fun <T : ViewModel> create(modelClass: Class<T>): T {
                             if (modelClass.isAssignableFrom(EncryptDecryptViewModel::class.java)) {
                                 return EncryptDecryptViewModel(
                                     baseAlgorithmRepository,
                                     designAlgorithmRepository,
                                     keyRepository,
                                     encryptMessageRepository,
                                     decryptMessageRepository
                                 ) as T
                             }
                             throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                         }
                     }