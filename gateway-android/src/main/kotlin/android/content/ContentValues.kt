package android.content

class ContentValues {
    private val data = mutableMapOf<String, Any?>()
    fun put(key: String, value: String?) { data[key] = value }
    fun put(key: String, value: Long) { data[key] = value }
}
