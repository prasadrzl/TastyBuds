package com.app.tastybuds.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.app.tastybuds.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSource @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private const val TAG = "LocalDataSource"
        private const val ENCRYPTED_PREFS_NAME = "tasty_buds_secure_prefs"
        private const val FALLBACK_PREFS_NAME = "tasty_buds_fallback_prefs"
    }

    private val masterKey by lazy {
        try {
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create master key", e)
            null
        }
    }

    private val securePrefs: SharedPreferences by lazy {
        try {
            if (masterKey != null) {
                EncryptedSharedPreferences.create(
                    context,
                    ENCRYPTED_PREFS_NAME,
                    masterKey!!,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } else {
                throw Exception("Master key is null")
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to create EncryptedSharedPreferences, falling back to regular SharedPreferences",
                e
            )
            clearCorruptedEncryptedData()
            context.getSharedPreferences(FALLBACK_PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun clearCorruptedEncryptedData() {
        try {
            context.getSharedPreferences(ENCRYPTED_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            val masterKeyAlias = "_androidx_security_master_key_"
            val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            if (keyStore.containsAlias(masterKeyAlias)) {
                keyStore.deleteEntry(masterKeyAlias)
                Log.d(TAG, "Deleted corrupted master key")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not clear corrupted data completely", e)
        }
    }

    fun savePassword(email: String, password: String) {
        try {
            val hashedPassword = password.hashCode().toString()
            securePrefs.edit()
                .putString("password_$email", hashedPassword)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save password", e)
        }
    }

    suspend fun validatePassword(email: String, password: String): Boolean {
        return try {
            val storedHashedPassword = securePrefs.getString("password_$email", null)
            if (storedHashedPassword == null) {
                savePassword(email, password)
                true
            } else {
                val hashedInputPassword = password.hashCode().toString()
                storedHashedPassword == hashedInputPassword
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to validate password", e)
            // On validation error, allow login to proceed
            true
        }
    }

    suspend fun saveLoginState(user: User) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = true
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_ID_KEY] = user.id
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    }

    fun getUserEmail(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }

    fun getUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }

    suspend fun clearLoginState() {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences[USER_EMAIL_KEY] = ""
            preferences[USER_ID_KEY] = ""
        }
    }
}