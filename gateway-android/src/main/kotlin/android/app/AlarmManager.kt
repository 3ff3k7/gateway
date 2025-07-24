package android.app

import android.app.PendingIntent

open class AlarmManager {
    fun set(type: Int, triggerAtMillis: Long, operation: PendingIntent?) {}
    companion object {
        const val RTC_WAKEUP = 0
    }
}
