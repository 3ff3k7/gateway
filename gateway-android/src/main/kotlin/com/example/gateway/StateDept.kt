package com.example.gateway

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup

object StateDept {
    private const val CEAC_STATUS_URL = "https://ceac.state.gov/CEACStatTracker/Status.aspx?App=NIV"
    private val client = OkHttpClient()

    /**
     * Retrieve case status from the State Department CEAC page. Since there is
     * no official API, this method submits a POST request and parses the HTML
     * response. If scraping fails it returns a stubbed response.
     */
    fun checkCeacStatus(caseNumber: String, visaType: String = "IV"): Map<String, Any> {
        if (caseNumber.isBlank()) {
            throw IllegalArgumentException("case_number required")
        }

        val form = FormBody.Builder()
            .add("CaseNumber", caseNumber)
            .add("VisaType", visaType)
            .build()
        val request = Request.Builder()
            .url(CEAC_STATUS_URL.toHttpUrl())
            .post(form)
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                ensureSuccess(response)
                val body = response.body?.string().orEmpty()
                val doc = Jsoup.parse(body)
                val statusText = doc.selectFirst("#ctl00_MainContent_lblStatus")?.text()
                    ?: "UNKNOWN"
                mapOf(
                    "case_number" to caseNumber,
                    "status" to statusText
                )
            }
        } catch (e: Exception) {
            mapOf(
                "case_number" to caseNumber,
                "status" to "UNKNOWN",
                "message" to "CEAC scraping not yet implemented"
            )
        }
    }

    private fun ensureSuccess(response: Response) {
        if (!response.isSuccessful) {
            throw RuntimeException("CEAC request failed: ${'$'}{response.code}")
        }
    }
}
