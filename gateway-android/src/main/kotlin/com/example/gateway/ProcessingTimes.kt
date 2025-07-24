package com.example.gateway

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl

object ProcessingTimes {
    private const val API_URL = "https://egov.uscis.gov/processing-times/api/"
    private val client = OkHttpClient()

    /**
     * Fetch processing time information for a form and office using the
     * public USCIS Processing Times API. Returns a stub if the request
     * fails.
     */
    fun getProcessingTime(formNumber: String, officeCode: String): Map<String, Any> {
        if (formNumber.isBlank() || officeCode.isBlank()) {
            throw IllegalArgumentException("form_number and office_code required")
        }

        val url = "${API_URL}forms/${formNumber}/offices/${officeCode}".toHttpUrl()
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                ensureSuccess(response)
                val body = response.body?.string() ?: "{}"
                mapOf(
                    "form_number" to formNumber,
                    "office_code" to officeCode,
                    "raw" to body
                )
            }
        } catch (e: Exception) {
            mapOf(
                "form_number" to formNumber,
                "office_code" to officeCode,
                "processing_time" to "N/A",
                "message" to "Processing time API integration pending"
            )
        }
    }

    private fun ensureSuccess(response: Response) {
        if (!response.isSuccessful) {
            throw RuntimeException("Processing Time API failed: ${'$'}{response.code}")
        }
    }
}
