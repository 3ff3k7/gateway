package com.example.gateway

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.ZoneId

class CalendarEventTest {
    @Test
    fun `creates ics with all fields`() {
        val zone = ZoneId.of("UTC")
        val ics = CalendarEvent.createEvent(
            summary = "Interview",
            start = "2025-08-18T10:00:00",
            end = "2025-08-18T11:00:00",
            zoneId = zone,
            location = "USCIS Office",
            description = "Bring documents"
        )
        assertTrue(ics.contains("BEGIN:VEVENT"))
        assertTrue(ics.contains("SUMMARY:Interview"))
        assertTrue(ics.contains("DTSTART;TZID=UTC:20250818T100000"))
        assertTrue(ics.contains("DTEND;TZID=UTC:20250818T110000"))
        assertTrue(ics.contains("LOCATION:USCIS Office"))
        assertTrue(ics.contains("DESCRIPTION:Bring documents"))
        assertTrue(ics.trim().endsWith("END:VCALENDAR"))
    }
}
