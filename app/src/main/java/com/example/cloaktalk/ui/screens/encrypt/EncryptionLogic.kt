package com.example.cloaktalk.ui.screens.encrypt

import kotlin.random.Random

/**
 * Data class representing the result of an encryption operation.
 *
 * @property encryptedText The encrypted message
 * @property success Whether the encryption was successful
 * @property errorMessage Error message if encryption failed
 */
data class EncryptionOperationResult(
    val encryptedText: String,
    val success: Boolean = true,
    val errorMessage: String? = null
)

/**
 * Generate encryption key (shift value) based on selected algorithm.
 * Uses a single integer shift to match previewEncryption behavior.
 *
 * @param algorithm The algorithm name
 * @param charsetSize The size of the character set
 * @return A string representation of the shift amount
 */
fun generateEncryptionKey(algorithm: String, charsetSize: Int = 80): String {
    val max = if (charsetSize > 0) charsetSize else 80
    val shift = when (algorithm) {
        "Caesar Cipher", "Caesar" -> Random.nextInt(1, max)
        "Vigenère Cipher", "Vigenere" -> Random.nextInt(1, max)
        "Substitution Cipher", "Substitution" -> Random.nextInt(1, max)
        else -> Random.nextInt(1, max)
    }
    return shift.toString()
}

/**
 * Perform encryption using the specified algorithm.
 *
 * Supports:
 * - Caesar Cipher: Fixed character shift
 * - Substitution Cipher: Seeded shuffle-based substitution
 * - Vigenère Cipher: Position-based variable shift
 * - Multiple rounds: Applies final string reversal
 *
 * @param plaintext The message to encrypt
 * @param algorithm The encryption algorithm to use
 * @param key The encryption key (shift amount as string)
 * @param charset The set of allowed characters
 * @param multipleRounds Whether to apply multiple encryption rounds
 * @return EncryptionOperationResult containing encrypted text or error
 */
fun performEncryption(
    plaintext: String,
    algorithm: String,
    key: String,
    charset: Set<Char>,
    multipleRounds: Boolean
): EncryptionOperationResult {
    // Validate charset size
    if (charset.size < 2) {
        return EncryptionOperationResult(
            encryptedText = "",
            success = false,
            errorMessage = "Character set too small for encryption"
        )
    }

    val charList = charset.toList()

    // Detect character categories in charset
    val hasLowercase = charList.any { it in 'a'..'z' }
    val hasUppercase = charList.any { it in 'A'..'Z' }
    val hasNumbers = charList.any { it in '0'..'9' }
    val hasSymbols = charList.any { it in "!@#\$%^&*()_+-=[]{}|;:,.<>?/~`'\"" }
    val hasSpace = charList.contains(' ')

    // Normalize input: convert or filter characters based on charset
    val normalizedPlaintext = plaintext.mapNotNull { c ->
        when {
            c in 'a'..'z' -> when {
                hasLowercase -> c
                hasUppercase -> c.uppercaseChar()
                else -> null
            }
            c in 'A'..'Z' -> when {
                hasUppercase -> c
                hasLowercase -> c.lowercaseChar()
                else -> null
            }
            c in '0'..'9' -> if (hasNumbers) c else null
            c in "!@#\$%^&*()_+-=[]{}|;:,.<>?/~`'\"" -> if (hasSymbols) c else null
            c == ' ' -> if (hasSpace) c else null
            c in charList -> c
            else -> null
        }
    }.joinToString("")

    // Filter to only characters in charset
    val filteredPlaintext = normalizedPlaintext.filter { it in charList }

    if (filteredPlaintext.isEmpty()) {
        return EncryptionOperationResult(
            encryptedText = "",
            success = false,
            errorMessage = "No valid characters in message for selected charset"
        )
    }

    // Parse shift amount from key
    val shiftAmount = key.toIntOrNull() ?: 13

    // Perform encryption based on algorithm
    var result = when {
        algorithm.contains("Caesar", ignoreCase = true) -> {
            caesarEncrypt(filteredPlaintext, charList, shiftAmount)
        }
        algorithm.contains("Substitution", ignoreCase = true) -> {
            substitutionEncrypt(filteredPlaintext, charList, shiftAmount)
        }
        algorithm.contains("Vigenère", ignoreCase = true) ||
                algorithm.contains("Vigenere", ignoreCase = true) -> {
            vigenereEncrypt(filteredPlaintext, charList, shiftAmount)
        }
        else -> {
            // Default to Caesar-style encryption for custom algorithms
            caesarEncrypt(filteredPlaintext, charList, shiftAmount)
        }
    }

    // Apply multiple rounds if enabled (reverse the result)
    if (multipleRounds) {
        result = result.reversed()
    }

    return EncryptionOperationResult(
        encryptedText = result,
        success = true
    )
}

/**
 * Caesar cipher encryption: shifts each character by a fixed amount.
 */
private fun caesarEncrypt(text: String, charList: List<Char>, shift: Int): String {
    val size = charList.size
    val normalizedShift = ((shift % size) + size) % size
    return text.map { ch ->
        val idx = charList.indexOf(ch)
        if (idx >= 0) {
            charList[(idx + normalizedShift) % size]
        } else {
            ch
        }
    }.joinToString("")
}

/**
 * Substitution cipher encryption: uses a seeded shuffle to create substitution map.
 */
private fun substitutionEncrypt(text: String, charList: List<Char>, seed: Int): String {
    val shuffled = charList.shuffled(Random(seed.toLong()))
    return text.map { ch ->
        val idx = charList.indexOf(ch)
        if (idx >= 0) {
            shuffled[idx]
        } else {
            ch
        }
    }.joinToString("")
}

/**
 * Vigenère cipher encryption: applies position-based variable shifts.
 */
private fun vigenereEncrypt(text: String, charList: List<Char>, baseShift: Int): String {
    val size = charList.size
    val base = ((baseShift % size) + size) % size
    return text.mapIndexed { i, ch ->
        val idx = charList.indexOf(ch)
        if (idx >= 0) {
            val posShift = (base + (i % 5)) % size
            charList[(idx + posShift) % size]
        } else {
            ch
        }
    }.joinToString("")
}
