package com.example.gateway

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.concurrent.TimeUnit

class SecureStorage(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .setUserAuthenticationRequired(true, TimeUnit.MINUTES.toSeconds(5))
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "gateway_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveApiToken(token: String) {
        prefs.edit().putString("api_token", token).apply()
    }

    fun getApiToken(): String? = prefs.getString("api_token", null)

    fun saveCaseNumber(caseNumber: String) {
        prefs.edit().putString("case_number", caseNumber).apply()
    }

    fun getCaseNumber(): String? = prefs.getString("case_number", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}
