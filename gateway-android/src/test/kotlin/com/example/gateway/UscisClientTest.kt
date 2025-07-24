package com.example.gateway

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class UscisClientTest {
    private class NullStore : UscisAuth.CredentialsStore {
        override fun getClientId(): String? = null
        override fun getClientSecret(): String? = null
    }

    @Test
    fun `throws for blank receipt`() {
        val client = UscisClient(credentialsStore = NullStore())
        assertThrows(IllegalArgumentException::class.java) {
            client.checkCaseStatus("")
        }
    }

    @Test
    fun `returns stub when no credentials`() {
        val client = UscisClient(credentialsStore = NullStore())
        val result = client.checkCaseStatus("ABC1234567")
        assertEquals("ABC1234567", result.receiptNumber)
        assertEquals("PENDING", result.status)
        assertEquals(
            "This is a stub. Connect to the USCIS API to get real data.",
            result.message
        )
    }
}
