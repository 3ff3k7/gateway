package androidx.core.app

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

object ActivityCompat {
    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {}
    fun checkSelfPermission(context: Context, permission: String): Int = PackageManager.PERMISSION_GRANTED
}
