package com.example.gateway

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

/**
 * Simple utility to download and extract text from the U.S. Department of
 * State Visa Bulletin PDFs. Parsing of the dates is outside the scope of this
 * example, but the raw bulletin text can be used for further analysis.
 */
object VisaBulletinFetcher {
    private const val BASE_URL = "https://travel.state.gov/content/dam/visas/Bulletins"
    private val client = OkHttpClient()

    fun fetch(month: String, year: Int): VisaBulletin {
        val fileName = "visabulletin_${month.replaceFirstChar { it.uppercase() }}$year.pdf"
        val url = "$BASE_URL/$fileName"
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return VisaBulletin(month, year, message = "Failed to download bulletin: ${'$'}{resp.code}")
                }
                val bytes = resp.body?.bytes() ?: return VisaBulletin(month, year, message = "Empty response")
                val doc = PDDocument.load(bytes)
                val stripper = PDFTextStripper()
                val text = stripper.getText(doc)
                doc.close()
                VisaBulletin(month, year, text = text.trim())
            }
        } catch (e: Exception) {
            VisaBulletin(month, year, message = e.message)
        }
    }
}
