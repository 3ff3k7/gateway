package androidx.security.crypto

import android.content.Context
import android.content.SharedPreferences

object EncryptedSharedPreferences {
    enum class PrefKeyEncryptionScheme { AES256_SIV }
    enum class PrefValueEncryptionScheme { AES256_GCM }

    fun create(
        context: Context,
        fileName: String,
        masterKey: MasterKey,
        keyScheme: PrefKeyEncryptionScheme,
        valueScheme: PrefValueEncryptionScheme
    ): SharedPreferences = object : SharedPreferences {
        private val data = mutableMapOf<String, String?>()
        override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
            override fun putString(key: String, value: String?): SharedPreferences.Editor {
                data[key] = value
                return this
            }
            override fun clear(): SharedPreferences.Editor { data.clear(); return this }
            override fun apply() {}
        }
        override fun getString(key: String, defValue: String?): String? = data[key] ?: defValue
    }
}
