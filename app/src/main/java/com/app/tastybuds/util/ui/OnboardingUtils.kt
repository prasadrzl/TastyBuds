package com.app.tastybuds.util.ui

import android.content.Context

object OnboardingUtils {
    private const val PREFS_NAME = "tastybuds_onboarding_prefs" // Different name to avoid conflicts
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_USER_LOGGED_IN = "user_logged_in"

    fun isOnboardingCompleted(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        } catch (e: Exception) {
            // If there's any error reading preferences, assume onboarding not completed
            false
        }
    }

    fun markOnboardingCompleted(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
        } catch (e: Exception) {
            // Log error but don't crash the app
            android.util.Log.e("OnboardingUtils", "Error saving onboarding completion", e)
        }
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getBoolean(KEY_USER_LOGGED_IN, false)
        } catch (e: Exception) {
            false
        }
    }

    fun setUserLoggedIn(context: Context, isLoggedIn: Boolean) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_USER_LOGGED_IN, isLoggedIn).apply()
        } catch (e: Exception) {
            // Log error but don't crash the app
            android.util.Log.e("OnboardingUtils", "Error saving login state", e)
        }
    }

    fun clearUserSession(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_USER_LOGGED_IN, false).apply()
        } catch (e: Exception) {
            // Log error but don't crash the app
            android.util.Log.e("OnboardingUtils", "Error clearing user session", e)
        }
    }

    fun clearAllOnboardingPrefs(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            android.util.Log.e("OnboardingUtils", "Error clearing preferences", e)
        }
    }
}