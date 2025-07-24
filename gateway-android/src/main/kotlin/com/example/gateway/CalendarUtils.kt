package com.example.gateway

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.app.ActivityCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

/**
 * Helper utilities for interacting with the Android Calendar Provider
 * and scheduling notifications for upcoming events.
 */
object CalendarUtils {
    /**
     * Insert an event using [CalendarContract]. Returns the new event ID or -1.
     */
    fun insertEvent(
        context: Context,
        summary: String,
        start: String,
        end: String,
        zoneId: ZoneId = ZoneId.systemDefault(),
        location: String? = null,
        description: String? = null,
        calendarId: Long = 1L
    ): Long {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, summary)
            put(CalendarContract.Events.DTSTART, toMillis(start, zoneId))
            put(CalendarContract.Events.DTEND, toMillis(end, zoneId))
            put(CalendarContract.Events.EVENT_TIMEZONE, zoneId.id)
            location?.let { put(CalendarContract.Events.LOCATION, it) }
            description?.let { put(CalendarContract.Events.DESCRIPTION, it) }
        }
        val uri: Uri? = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri?.lastPathSegment?.toLong() ?: -1L
    }

    /**
     * Update an existing event when the dates change.
     */
    fun updateEvent(
        context: Context,
        eventId: Long,
        newStart: String,
        newEnd: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, toMillis(newStart, zoneId))
            put(CalendarContract.Events.DTEND, toMillis(newEnd, zoneId))
            put(CalendarContract.Events.EVENT_TIMEZONE, zoneId.id)
        }
        val uri = Uri.parse("${CalendarContract.Events.CONTENT_URI}/$eventId")
        context.contentResolver.update(uri, values, null, null)
    }

    /**
     * Remove an event from the calendar.
     */
    fun deleteEvent(context: Context, eventId: Long) {
        val uri = Uri.parse("${CalendarContract.Events.CONTENT_URI}/$eventId")
        context.contentResolver.delete(uri, null, null)
    }

    /**
     * Schedule a notification ahead of the event start time using WorkManager.
     */
    fun scheduleNotification(context: Context, eventId: Long, start: String, zoneId: ZoneId = ZoneId.systemDefault(), minutesBefore: Long = 60) {
        val startMillis = toMillis(start, zoneId)
        val delay = LocalDateTime.now(zoneId).until(LocalDateTime.ofEpochSecond(startMillis / 1000, 0, zoneId.rules.getOffset(LocalDateTime.now())), ChronoUnit.MILLIS) - minutesBefore * 60_000
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    /**
     * Ensure calendar runtime permissions are granted.
     */
    fun ensurePermissions(activity: Activity, requestCode: Int = 100): Boolean {
        val writeGranted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
        val readGranted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        return if (!writeGranted || !readGranted) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR), requestCode)
            false
        } else {
            true
        }
    }

    private fun toMillis(dateTime: String, zoneId: ZoneId): Long {
        return LocalDateTime.parse(dateTime).atZone(zoneId).toInstant().toEpochMilli()
    }
}

/**
 * Minimal worker used for notification scheduling stub.
 */
class NotificationWorker(appContext: Context, params: androidx.work.WorkerParameters) : androidx.work.Worker(appContext, params) {
    override fun doWork(): androidx.work.Result {
        return androidx.work.Result.success()
    }
}
