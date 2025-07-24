package android.content

interface SharedPreferences {
    fun edit(): Editor
    fun getString(key: String, defValue: String?): String?

    interface Editor {
        fun putString(key: String, value: String?): Editor
        fun clear(): Editor
        fun apply()
    }
}
