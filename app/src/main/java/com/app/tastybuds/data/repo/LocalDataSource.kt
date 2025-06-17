package com.app.tastybuds.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.content.Context
import com.app.tastybuds.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "tasty_buds_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    suspend fun savePassword(email: String, password: String) {
        val hashedPassword = password.hashCode().toString()
        encryptedPrefs.edit()
            .putString("password_$email", hashedPassword)
            .apply()
    }

    suspend fun validatePassword(email: String, password: String): Boolean {
        return try {
            val storedHashedPassword = encryptedPrefs.getString("password_$email", null)
            if (storedHashedPassword == null) {
                savePassword(email, password)
                true
            } else {
                val hashedInputPassword = password.hashCode().toString()
                storedHashedPassword == hashedInputPassword
            }
        } catch (e: Exception) {
            false
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