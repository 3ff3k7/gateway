package androidx.security.crypto

import android.content.Context

class MasterKey private constructor() {
    class Builder(private val context: Context) {
        fun setKeyScheme(scheme: KeyScheme): Builder = this
        fun setUserAuthenticationRequired(required: Boolean, timeoutSeconds: Long): Builder = this
        fun build(): MasterKey = MasterKey()
    }

    enum class KeyScheme { AES256_GCM }
}
