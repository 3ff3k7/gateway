package com.example.gateway

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl

class FoiaClient(
    private val baseUrl: String = "https://sandbox.api.uscis.gov",
    private val credentialsStore: UscisAuth.CredentialsStore = UscisAuth.EnvCredentialsStore()
) {
    private val client = OkHttpClient()

    fun createRequest(jsonData: String): FoiaResult {
        val token = UscisAuth.getAccessToken(credentialsStore) ?: return FoiaResult(
            requestId = "DUMMY-REQUEST-ID",
            message = "FOIA request creation not yet implemented"
        )
        val url = "$baseUrl/v1/foia-requests".toHttpUrl()
        val body = jsonData.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                ensureSuccess(response)
                val respBody = response.body?.string().orEmpty()
                val json = Json.parseToJsonElement(respBody).jsonObject
                val id = json["request_id"]?.jsonPrimitive?.content ?: "UNKNOWN"
                val status = json["status"]?.jsonPrimitive?.content
                val message = json["statusMessage"]?.jsonPrimitive?.content
                FoiaResult(id, status = status, message = message, raw = json)
            }
        } catch (e: Exception) {
            FoiaResult(requestId = "UNKNOWN", status = "ERROR", message = e.message)
        }
    }

    fun checkStatus(requestId: String): FoiaResult {
        if (requestId.isBlank()) {
            throw IllegalArgumentException("request_id required")
        }
        val token = UscisAuth.getAccessToken(credentialsStore) ?: return FoiaResult(
            requestId = requestId,
            status = "PENDING",
            message = "Connect to the USCIS FOIA API to get real status"
        )
        val url = "$baseUrl/v1/foia-requests/$requestId".toHttpUrl()
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
                val message = json["statusMessage"]?.jsonPrimitive?.content
                FoiaResult(requestId = requestId, status = status, message = message, raw = json)
            }
        } catch (e: Exception) {
            FoiaResult(requestId = requestId, status = "ERROR", message = e.message)
        }
    }

    private fun ensureSuccess(response: Response) {
        if (!response.isSuccessful) {
            throw RuntimeException("FOIA API request failed: ${'$'}{response.code}")
        }
    }
}
