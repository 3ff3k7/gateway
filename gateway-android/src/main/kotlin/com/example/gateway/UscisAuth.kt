package com.example.gateway

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.int
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * Utility for acquiring an access token from the USCIS OAuth endpoint
 * using the client credentials flow. Tokens are cached in memory until
 * they expire. In production Android builds the client ID and secret
 * should be stored in the Android Keystore. This implementation also
 * supports retrieving credentials from environment variables which is
 * convenient for the CLI.
 */
object UscisAuth {
    /** Sandbox token endpoint as referenced in the project README */
    private const val TOKEN_URL = "https://api-int.uscis.gov/oauth/accesstoken"

    private val client = OkHttpClient()
    private var cachedToken: String? = null
    private var expiresAt: Long = 0L

    interface CredentialsStore {
        fun getClientId(): String?
        fun getClientSecret(): String?
    }

    /**
     * Simple credentials store that reads from environment variables.
     * On Android, implement [CredentialsStore] with the Android Keystore
     * to protect the client secret.
     */
    class EnvCredentialsStore(
        private val idKey: String = "USCIS_CLIENT_ID",
        private val secretKey: String = "USCIS_CLIENT_SECRET"
    ) : CredentialsStore {
        override fun getClientId(): String? = System.getenv(idKey)
        override fun getClientSecret(): String? = System.getenv(secretKey)
    }

    /**
     * Retrieve a valid access token, requesting a new one if necessary.
     * @return the access token string or null if credentials are missing
     */
    @Synchronized
    fun getAccessToken(store: CredentialsStore = EnvCredentialsStore()): String? {
        val now = System.currentTimeMillis()
        val token = cachedToken
        if (token != null && now < expiresAt - 60_000) {
            return token
        }

        val clientId = store.getClientId() ?: return null
        val clientSecret = store.getClientSecret() ?: return null

        val body = "grant_type=client_credentials"
            .toRequestBody("application/x-www-form-urlencoded".toMediaType())
        val request = Request.Builder()
            .url(TOKEN_URL.toHttpUrl())
            .header("Authorization", Credentials.basic(clientId, clientSecret))
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Token request failed: ${'$'}{response.code}")
            }
            val respBody = response.body?.string().orEmpty()
            val json = Json.parseToJsonElement(respBody).jsonObject
            val accessToken = json["access_token"]?.jsonPrimitive?.content
            val expiresIn = json["expires_in"]?.jsonPrimitive?.int ?: 3600
            if (accessToken != null) {
                cachedToken = accessToken
                expiresAt = now + expiresIn * 1000L
                return accessToken
            }
        }
        return null
    }
}
