package com.app.tastybuds.ui.onboarding

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingManager @Inject constructor(
    private val context: Context,
    private val userDataStore: DataStore<Preferences>
) {
    companion object {
        private const val ONBOARDING_PREFS = "onboarding_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_FIRST_LAUNCH = "first_launch_detected"

        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ID_KEY = stringPreferencesKey("user_id")

        private const val TAG = "OnboardingManager"
    }

    private fun getOnboardingPrefs() =
        context.getSharedPreferences(ONBOARDING_PREFS, Context.MODE_PRIVATE)

    private fun isFirstLaunch(): Boolean {
        return try {
            val prefs = getOnboardingPrefs()
            !prefs.contains(KEY_FIRST_LAUNCH)
        } catch (e: Exception) {
            true
        }
    }

    private fun markFirstLaunchComplete() {
        try {
            getOnboardingPrefs().edit()
                .putBoolean(KEY_FIRST_LAUNCH, true)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error marking first launch complete", e)
        }
    }

    fun isOnboardingCompleted(): Boolean {
        return try {
            if (isFirstLaunch()) {
                Log.d(TAG, "FIRST LAUNCH DETECTED - Clearing all user data")
                clearAllUserData()
                markFirstLaunchComplete()
                return false
            }

            val result = getOnboardingPrefs().getBoolean(KEY_ONBOARDING_COMPLETED, false)
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error reading onboarding completion", e)
            false
        }
    }

    fun markOnboardingCompleted() {
        try {
            getOnboardingPrefs().edit()
                .putBoolean(KEY_ONBOARDING_COMPLETED, true)
                .apply()

            getOnboardingPrefs().getBoolean(KEY_ONBOARDING_COMPLETED, false)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving onboarding completion", e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return try {
            val result = runBlocking {
                userDataStore.data.map { preferences ->
                    preferences[IS_LOGGED_IN_KEY] ?: false
                }.first()
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error reading login state", e)
            false
        }
    }

    suspend fun clearUserSessionAsync() {
        try {
            userDataStore.edit { preferences ->
                preferences[IS_LOGGED_IN_KEY] = false
                preferences[USER_EMAIL_KEY] = ""
                preferences[USER_ID_KEY] = ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing user session", e)
        }
    }

    private fun clearAllUserData() {
        try {
            getOnboardingPrefs().edit()
                .putBoolean(KEY_ONBOARDING_COMPLETED, false)
                .apply()

            try {
                runBlocking {
                    kotlinx.coroutines.withTimeout(1000) {
                        userDataStore.edit { preferences ->
                            preferences[IS_LOGGED_IN_KEY] = false
                            preferences[USER_EMAIL_KEY] = ""
                            preferences[USER_ID_KEY] = ""
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "DataStore clear timed out during first launch, continuing...")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all user data", e)
        }
    }
}