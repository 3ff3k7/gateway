package com.example.gateway

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl

class UscisClient(
    private val baseUrl: String = "https://sandbox.api.uscis.gov",
    private val token: String? = null
) {
    private val client = OkHttpClient()

    /**
     * Retrieve case status for a USCIS receipt number using the official
     * USCIS Case Status API. If authentication is not configured this will
     * return a stubbed response.
     */
    fun checkCaseStatus(receipt: String): Map<String, Any> {
        if (receipt.isBlank()) {
            throw IllegalArgumentException("receipt number required")
        }

        if (token == null) {
            return mapOf(
                "receipt_number" to receipt,
                "status" to "PENDING",
                "message" to "This is a stub. Connect to the USCIS API to get real data."
            )
        }

        val url = "$baseUrl/v1/case-status/$receipt".toHttpUrl()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()
        client.newCall(request).execute().use { response ->
            ensureSuccess(response)
            val body = response.body?.string() ?: "{}"
            return mapOf(
                "receipt_number" to receipt,
                "raw" to body
            )
        }
    }

    private fun ensureSuccess(response: Response) {
        if (!response.isSuccessful) {
            throw RuntimeException("USCIS API request failed: ${'$'}{response.code}")
        }
    }
}
