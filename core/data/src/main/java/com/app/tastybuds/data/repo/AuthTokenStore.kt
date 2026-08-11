package com.app.tastybuds.data.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * Synchronous, encrypted store for the JWT access token. Read synchronously by the OkHttp
 * interceptor and the chat WebSocket URL builder, so it can't be a suspend/DataStore API.
 */
@Singleton
class AuthTokenStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "auth_token_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Falling back to plain prefs for token store", e)
            context.getSharedPreferences("auth_token_prefs_plain", Context.MODE_PRIVATE)
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun setToken(token: String) = prefs.edit { putString(KEY_TOKEN, token) }

    fun clear() = prefs.edit { remove(KEY_TOKEN) }

    private companion object {
        const val TAG = "AuthTokenStore"
        const val KEY_TOKEN = "jwt_access_token"
    }
}
