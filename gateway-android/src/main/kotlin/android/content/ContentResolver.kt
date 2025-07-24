package android.content

import android.net.Uri

open class ContentResolver {
    open fun insert(uri: Uri, values: ContentValues): Uri? = null
    open fun update(uri: Uri, values: ContentValues, where: String?, args: Array<String>?): Int = 0
    open fun delete(uri: Uri, where: String?, args: Array<String>?): Int = 0
}
