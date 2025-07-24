package com.example.gateway

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl

class UscisClient(
    private val baseUrl: String = "https://sandbox.api.uscis.gov",
    private val credentialsStore: UscisAuth.CredentialsStore = UscisAuth.EnvCredentialsStore()
) {
    private val client = OkHttpClient()

    /**
     * Retrieve case status for a USCIS receipt number using the official
     * USCIS Case Status API. If authentication is not configured this will
     * return a stubbed response.
     */
    fun checkCaseStatus(receipt: String): CaseStatus {
        if (receipt.isBlank()) {
            throw IllegalArgumentException("receipt number required")
        }

        val token = UscisAuth.getAccessToken(credentialsStore)
            ?: return CaseStatus(
                receiptNumber = receipt,
                status = "PENDING",
                message = "This is a stub. Connect to the USCIS API to get real data."
            )

        val url = "$baseUrl/v1/case-status/$receipt".toHttpUrl()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                ensureSuccess(response)
                val body = response.body?.string().orEmpty()
                val json = Json.parseToJsonElement(body).jsonObject
                val status = json["status"]?.jsonPrimitive?.content
                val statusCode = json["statusCode"]?.jsonPrimitive?.content
                val message = json["statusMessage"]?.jsonPrimitive?.content
                CaseStatus(
                    receiptNumber = receipt,
                    statusCode = statusCode,
                    status = status,
                    message = message,
                    raw = json
                )
            }
        } catch (e: Exception) {
            CaseStatus(
                receiptNumber = receipt,
                status = "ERROR",
                message = e.message
            )
        }
    }

    private fun ensureSuccess(response: Response) {
        if (!response.isSuccessful) {
            throw RuntimeException("USCIS API request failed: ${'$'}{response.code}")
        }
    }
}
