package com.example.gateway

import kotlinx.serialization.Serializable

/**
 * Static list of official USCIS study materials for the naturalization test.
 */
object StudyResources {
    /** Container for reading and video links. */
    @Serializable
    data class Resources(
        val readingLinks: List<String>,
        val videoLinks: List<String>
    )

    /**
     * Official links published by USCIS.
     */
    val resources = Resources(
        readingLinks = listOf(
            "https://www.uscis.gov/citizenship/find-study-materials-and-resources/study-for-the-test",
            "https://www.uscis.gov/sites/default/files/document/questions-and-answers/100q.pdf"
        ),
        videoLinks = listOf(
            "https://www.youtube.com/watch?v=ny5q52Zw7Xw",
            "https://www.youtube.com/watch?v=SQWMc5E_vMY"
        )
    )
}
