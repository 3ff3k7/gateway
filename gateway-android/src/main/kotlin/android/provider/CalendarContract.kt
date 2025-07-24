package android.provider

import android.net.Uri

object CalendarContract {
    object Events {
        val CONTENT_URI: Uri = Uri.parse("content://com.android.calendar/events")
        const val CALENDAR_ID = "calendar_id"
        const val TITLE = "title"
        const val DTSTART = "dtstart"
        const val DTEND = "dtend"
        const val EVENT_TIMEZONE = "eventTimezone"
        const val LOCATION = "eventLocation"
        const val DESCRIPTION = "description"
    }
}
