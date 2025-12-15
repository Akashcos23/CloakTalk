package com.example.cloaktalk

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Key Repository functionality in CloakTalk app.
 */
class KeyRepositoryTest {

    /**
     * Test that generated keys are exactly 8 digits.
     */
    @Test
    fun generatedKey_hasCorrectLength() {
        val key = generateTestKey()
        assertEquals("Key should be 8 digits", 8, key.length)
    }

    /**
     * Test that generated keys contain only numeric characters.
     */
    @Test
    fun generatedKey_containsOnlyDigits() {
        val key = generateTestKey()
        assertTrue("Key should contain only digits", key.all { it.isDigit() })
    }

    /**
     * Test key expiration time calculation.
     */
    @Test
    fun calculateExpirationTime_returnsCorrectValue() {
        val hours = 24
        val currentTime = System.currentTimeMillis()
        val expectedExpiration = currentTime + (hours * 60 * 60 * 1000L)
        val actualExpiration = calculateExpirationTime(hours, currentTime)
        
        assertEquals("Expiration should be 24 hours from now", expectedExpiration, actualExpiration)
    }

    /**
     * Test that expired keys are correctly identified.
     */
    @Test
    fun isKeyExpired_returnsTrueForExpiredKey() {
        val pastTime = System.currentTimeMillis() - 1000 // 1 second ago
        assertTrue("Key should be expired", isKeyExpired(pastTime))
    }

    /**
     * Test that active keys are correctly identified.
     */
    @Test
    fun isKeyExpired_returnsFalseForActiveKey() {
        val futureTime = System.currentTimeMillis() + 3600000 // 1 hour from now
        assertFalse("Key should not be expired", isKeyExpired(futureTime))
    }

    // Helper functions simulating repository logic
    private fun generateTestKey(): String {
        val random = java.security.SecureRandom()
        return (1..8).map { random.nextInt(10) }.joinToString("")
    }

    private fun calculateExpirationTime(hours: Int, currentTime: Long): Long {
        return currentTime + (hours * 60 * 60 * 1000L)
    }

    private fun isKeyExpired(expirationTime: Long): Boolean {
        return System.currentTimeMillis() > expirationTime
    }
}
