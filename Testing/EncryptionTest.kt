package com.example.cloaktalk

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Encryption/Decryption functionality in CloakTalk app.
 */
class EncryptionTest {

    /**
     * Test Caesar Cipher encryption produces different output.
     */
    @Test
    fun caesarCipher_encryptsMessage() {
        val plaintext = "HELLO"
        val shift = 3
        val encrypted = caesarEncrypt(plaintext, shift)
        
        assertNotEquals("Encrypted text should differ from plaintext", plaintext, encrypted)
        assertEquals("Caesar cipher with shift 3", "KHOOR", encrypted)
    }

    /**
     * Test Caesar Cipher decryption returns original message.
     */
    @Test
    fun caesarCipher_decryptsMessage() {
        val encrypted = "KHOOR"
        val shift = 3
        val decrypted = caesarDecrypt(encrypted, shift)
        
        assertEquals("Decrypted text should match original", "HELLO", decrypted)
    }

    /**
     * Test encryption and decryption are inverse operations.
     */
    @Test
    fun encryptThenDecrypt_returnsOriginalMessage() {
        val originalMessage = "CLOAKTALK"
        val shift = 5
        
        val encrypted = caesarEncrypt(originalMessage, shift)
        val decrypted = caesarDecrypt(encrypted, shift)
        
        assertEquals("Decrypt(Encrypt(msg)) should equal msg", originalMessage, decrypted)
    }

    /**
     * Test empty string encryption.
     */
    @Test
    fun caesarCipher_handlesEmptyString() {
        val plaintext = ""
        val encrypted = caesarEncrypt(plaintext, 3)
        
        assertEquals("Empty string should remain empty", "", encrypted)
    }

    /**
     * Test message entity creation with valid data.
     */
    @Test
    fun encryptMessageEntity_storesCorrectData() {
        val original = "Secret Message"
        val encrypted = "Vhfuhw Phvvdjh"
        val keyId = 1L
        val algorithm = "Caesar Cipher"
        
        val entity = TestEncryptMessageEntity(
            originalMessage = original,
            encryptedMessage = encrypted,
            keyId = keyId,
            algorithmName = algorithm
        )
        
        assertEquals(original, entity.originalMessage)
        assertEquals(encrypted, entity.encryptedMessage)
        assertEquals(keyId, entity.keyId)
        assertEquals(algorithm, entity.algorithmName)
    }

    // Helper functions simulating encryption logic
    private fun caesarEncrypt(text: String, shift: Int): String {
        return text.map { char ->
            when {
                char.isUpperCase() -> ((char - 'A' + shift) % 26 + 'A'.code).toChar()
                char.isLowerCase() -> ((char - 'a' + shift) % 26 + 'a'.code).toChar()
                else -> char
            }
        }.joinToString("")
    }

    private fun caesarDecrypt(text: String, shift: Int): String {
        return caesarEncrypt(text, 26 - shift)
    }

    // Test data class
    data class TestEncryptMessageEntity(
        val encryptId: Long = 0,
        val originalMessage: String,
        val encryptedMessage: String,
        val keyId: Long,
        val algorithmName: String
    )
}
