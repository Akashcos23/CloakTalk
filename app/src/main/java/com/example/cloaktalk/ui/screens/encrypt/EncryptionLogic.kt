// Kotlin
package com.example.cloaktalk.ui.screens.encrypt

import kotlin.random.Random

/**
 * Generate encryption key (shift value) based on selected algorithm.
 * Uses a single integer shift to match previewEncryption behavior.
 */
fun generateEncryptionKey(algorithm: String, charsetSize: Int = 80): String {
    // Use a safe range based on charset size; fallback to 13
    val max = if (charsetSize > 0) charsetSize else 80
    val shift = when (algorithm) {
        "Caesar Cipher",
        "Vigenère Cipher",
        "Substitution Cipher" -> Random.nextInt(1, max)
        else -> 13
    }
    return shift.toString()
}

/**
 * Perform encryption using the same rules as previewEncryption:
 * - Works only on characters present in charset
 * - Caesar: fixed shift
 * - Substitution: seeded shuffle with shiftAmount
 * - Vigenère: repeating shift pattern based on shiftAmount
 * - multipleRounds: final reverse
 */
fun performEncryption(
    plaintext: String,
    algorithm: String,
    key: String,
    charset: Set<Char>,
    multipleRounds: Boolean
): String {
    if (charset.size < 20) return "⚠️ Select at least 20 characters to enable encryption"

    val charList = charset.toList()

    // Normalize input similar to preview: keep only chars present in charset
    val hasLowercase = charList.any { it in 'a'..'z' }
    val hasUppercase = charList.any { it in 'A'..'Z' }
    val hasNumbers = charList.any { it in '0'..'9' }
    val hasSymbols = charList.any { it in "!@#$%^&*()_+-=[]{}|;:,.<>?/~`" }
    val hasSpace = charList.contains(' ')

    val normalizedPlaintext = plaintext.map { c ->
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
            c in "!@#$%^&*()_+-=[]{}|;:,.<>?/~`" -> if (hasSymbols) c else null
            c == ' ' -> if (hasSpace) c else null
            else -> null
        }
    }.filterNotNull().joinToString("")

    val filteredPlaintext = normalizedPlaintext.filter { it in charList }
    if (filteredPlaintext.isEmpty()) return "⚠️ No valid characters in message for selected charset"

    val shiftAmount = key.toIntOrNull() ?: 13

    var result = when (algorithm) {
        "Caesar Cipher" -> {
            filteredPlaintext.map { ch ->
                val idx = charList.indexOf(ch)
                charList[(idx + (shiftAmount % charList.size) + charList.size) % charList.size]
            }.joinToString("")
        }
        "Substitution Cipher" -> {
            val shuffled = charList.shuffled(Random(shiftAmount.toLong()))
            filteredPlaintext.map { ch ->
                val idx = charList.indexOf(ch)
                shuffled[idx]
            }.joinToString("")
        }
        "Vigenère Cipher" -> {
            val base = shiftAmount % charList.size
            filteredPlaintext.mapIndexed { i, ch ->
                val idx = charList.indexOf(ch)
                val posShift = (base + (i % 5)) % charList.size
                charList[(idx + posShift) % charList.size]
            }.joinToString("")
        }
        else -> {
            filteredPlaintext.map { ch ->
                val idx = charList.indexOf(ch)
                charList[(idx + (shiftAmount % charList.size) + charList.size) % charList.size]
            }.joinToString("")
        }
    }

    if (multipleRounds) {
        result = result.reversed()
    }

    return result
}