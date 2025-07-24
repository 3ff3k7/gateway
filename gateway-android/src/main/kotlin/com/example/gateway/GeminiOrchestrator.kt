package com.example.gateway

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object GeminiOrchestrator {
    private val client = OkHttpClient()
    private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"

    fun generatePlan(prompt: String, apiKey: String = System.getenv("GEMINI_API_KEY") ?: ""): String {
        if (apiKey.isBlank()) {
            return "Gemini API key not configured"
        }
        val json = "{" +
            "\"contents\":[{" +
            "\"parts\":[{" +
            "\"text\":\"" + prompt.replace("\"", "\\\"") + "\"" +
            "}]}]}"
        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$API_URL?key=$apiKey")
            .post(body)
            .build()
        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                return "Gemini request failed: ${'$'}{resp.code}"
            }
            return resp.body?.string().orEmpty()
        }
    }
}
