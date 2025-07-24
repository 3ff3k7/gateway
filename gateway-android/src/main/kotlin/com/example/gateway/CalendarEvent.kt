package com.example.gateway

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CalendarEvent {
    /**
     * Generate a basic ICS calendar event string.
     * Dates must be in ISO-8601 format.
     */
    fun createEvent(
        summary: String,
        start: String,
        end: String,
        location: String? = null,
        description: String? = null
    ): String {
        val fmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
        val startUtc = LocalDateTime.parse(start).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
        val endUtc = LocalDateTime.parse(end).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
        val builder = StringBuilder()
        builder.appendLine("BEGIN:VCALENDAR")
        builder.appendLine("VERSION:2.0")
        builder.appendLine("BEGIN:VEVENT")
        builder.appendLine("SUMMARY:$summary")
        builder.appendLine("DTSTART:${fmt.format(startUtc)}")
        builder.appendLine("DTEND:${fmt.format(endUtc)}")
        location?.let { builder.appendLine("LOCATION:$it") }
        description?.let { builder.appendLine("DESCRIPTION:$it") }
        builder.appendLine("END:VEVENT")
        builder.appendLine("END:VCALENDAR")
        return builder.toString()
    }
}
