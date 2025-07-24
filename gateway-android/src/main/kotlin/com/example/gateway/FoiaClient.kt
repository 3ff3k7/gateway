package com.example.gateway

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl

class FoiaClient(
    private val baseUrl: String = "https://sandbox.api.uscis.gov",
    private val token: String? = null
) {
    private val client = OkHttpClient()

    fun createRequest(jsonData: String): Map<String, Any> {
        if (token == null) {
            return mapOf(
                "request_id" to "DUMMY-REQUEST-ID",
                "message" to "FOIA request creation not yet implemented"
            )
        }
        val url = "$baseUrl/v1/foia-requests".toHttpUrl()
        val body = jsonData.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            ensureSuccess(response)
            val respBody = response.body?.string() ?: "{}"
            return mapOf("raw" to respBody)
        }
    }

    fun checkStatus(requestId: String): Map<String, Any> {
        if (requestId.isBlank()) {
            throw IllegalArgumentException("request_id required")
        }
        if (token == null) {
            return mapOf(
                "request_id" to requestId,
                "status" to "PENDING",
                "message" to "Connect to the USCIS FOIA API to get real status"
            )
        }
        val url = "$baseUrl/v1/foia-requests/$requestId".toHttpUrl()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()
        client.newCall(request).execute().use { response ->
            ensureSuccess(response)
            val body = response.body?.string() ?: "{}"
            return mapOf(
                "request_id" to requestId,
                "raw" to body
            )
        }
    }

    private fun ensureSuccess(response: Response) {
        if (!response.isSuccessful) {
            throw RuntimeException("FOIA API request failed: ${'$'}{response.code}")
        }
    }
}
