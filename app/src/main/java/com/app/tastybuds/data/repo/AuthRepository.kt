package com.app.tastybuds.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")

        private const val VALID_EMAIL = "admin@tastybuds.com"
        private const val UNIVERSAL_PASSWORD = "tastybuds123"
    }

    suspend fun login(email: String, password: String): Boolean {
        val isValid = validateCredentials(email, password)

        if (isValid) {
            dataStore.edit { preferences ->
                preferences[IS_LOGGED_IN_KEY] = true
                preferences[USER_EMAIL_KEY] = email
            }
        }

        return isValid
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

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences[USER_EMAIL_KEY] = ""
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        return email.equals(VALID_EMAIL, ignoreCase = true) && password == UNIVERSAL_PASSWORD
    }

    fun getValidCredentials(): Pair<String, String> {
        return Pair(VALID_EMAIL, UNIVERSAL_PASSWORD)
    }
}