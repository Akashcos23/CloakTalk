// Kotlin
    package com.example.cloaktalk.ui.screens.decrypt

    import kotlin.random.Random

    data class DecryptionResult(
        val decryptedText: String,
        val isValid: Boolean
    )

    /**
     * Perform decryption mirroring previewEncryption behavior:
     * - Operates strictly over provided charset
     * - Caesar: reverse shift
     * - Substitution: inverse of seeded shuffle
     * - Vigenère: reverse repeating pattern
     * - multipleRounds: reverse first (inverse of encryption's final step)
     */
    fun performDecryption(
        encryptedMessage: String,
        algorithm: String,
        key: String,
        charset: Set<Char>,
        multipleRounds: Boolean
    ): DecryptionResult {
        if (charset.size < 20) return DecryptionResult("⚠️ Select at least 20 characters to enable decryption", false)

        val charList = charset.toList()
        val shiftAmount = key.toIntOrNull() ?: return DecryptionResult("", false)

        // If encryption used multipleRounds (final reverse), decryption must reverse first
        val message = if (multipleRounds) encryptedMessage.reversed() else encryptedMessage

        // Validate message contains only chars from charset
        val filtered = message.filter { it in charList }
        if (filtered.isEmpty()) return DecryptionResult("⚠️ No valid characters in message for selected charset", false)

        val decrypted = when (algorithm) {
            "Caesar Cipher" -> {
                filtered.map { ch ->
                    val idx = charList.indexOf(ch)
                    charList[(idx - (shiftAmount % charList.size) + charList.size) % charList.size]
                }.joinToString("")
            }
            "Substitution Cipher" -> {
                val shuffled = charList.shuffled(Random(shiftAmount.toLong()))
                // Build inverse map: shuffled[idx] -> charList[idx]
                val inverse = shuffled.mapIndexed { i, c -> c to charList[i] }.toMap()
                filtered.map { ch -> inverse[ch] ?: ch }.joinToString("")
            }
            "Vigenère Cipher" -> {
                val base = shiftAmount % charList.size
                filtered.mapIndexed { i, ch ->
                    val idx = charList.indexOf(ch)
                    val posShift = (base + (i % 5)) % charList.size
                    charList[(idx - posShift + charList.size) % charList.size]
                }.joinToString("")
            }
            else -> {
                filtered.map { ch ->
                    val idx = charList.indexOf(ch)
                    charList[(idx - (shiftAmount % charList.size) + charList.size) % charList.size]
                }.joinToString("")
            }
        }

        val isValid = decrypted.isNotEmpty()
        return DecryptionResult(decrypted, isValid)
    }