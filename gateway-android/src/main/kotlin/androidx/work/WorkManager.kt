package androidx.work

import android.content.Context
import java.util.concurrent.TimeUnit

abstract class Worker(appContext: Context, params: WorkerParameters) {
    abstract fun doWork(): Result
}
open class WorkerParameters
class OneTimeWorkRequest
class OneTimeWorkRequestBuilder<T : Worker> {
    fun setInitialDelay(duration: Long, unit: TimeUnit): OneTimeWorkRequestBuilder<T> = this
    fun build(): OneTimeWorkRequest = OneTimeWorkRequest()
}

class WorkManager private constructor() {
    companion object {
        fun getInstance(context: Context): WorkManager = WorkManager()
    }
    fun enqueue(request: OneTimeWorkRequest) {}
}

sealed class Result {
    companion object {
        fun success(): Result = Success
    }
    object Success : Result()
}

class Data private constructor()
class Constraints private constructor()
object ExistingWorkPolicy { const val REPLACE = 0 }
