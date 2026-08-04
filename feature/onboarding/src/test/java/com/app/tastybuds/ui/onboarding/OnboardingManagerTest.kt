package com.app.tastybuds.ui.onboarding

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OnboardingManagerTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var manager: OnboardingManager

    @Before
    fun setUp() {
        context = mockk()
        prefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)
        dataStore = mockk()
        every { context.getSharedPreferences(any(), any()) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        // Treat the app as already past first launch so the completion flag is read directly.
        every { prefs.contains("first_launch_detected") } returns true
        manager = OnboardingManager(context, dataStore)
    }

    @Test
    fun `isOnboardingCompleted returns the stored completion flag`() {
        every { prefs.getBoolean("onboarding_completed", false) } returns true
        assertTrue(manager.isOnboardingCompleted())
    }

    @Test
    fun `isOnboardingCompleted returns false when not completed`() {
        every { prefs.getBoolean("onboarding_completed", false) } returns false
        assertFalse(manager.isOnboardingCompleted())
    }

    @Test
    fun `isOnboardingCompleted returns false when reading throws`() {
        every { prefs.getBoolean("onboarding_completed", false) } throws RuntimeException("prefs error")
        assertFalse(manager.isOnboardingCompleted())
    }

    @Test
    fun `markOnboardingCompleted writes the flag without throwing`() {
        manager.markOnboardingCompleted()
        io.mockk.verify { editor.putBoolean("onboarding_completed", true) }
    }

    @Test
    fun `isUserLoggedIn reads true from the data store`() {
        every { dataStore.data } returns flowOf(preferencesOf(booleanPreferencesKey("is_logged_in") to true))
        assertTrue(manager.isUserLoggedIn())
    }

    @Test
    fun `isUserLoggedIn defaults to false when the flag is absent`() {
        every { dataStore.data } returns flowOf(emptyPreferences())
        assertFalse(manager.isUserLoggedIn())
    }

    @Test
    fun `isUserLoggedIn returns false when the data store throws`() {
        every { dataStore.data } throws RuntimeException("ds error")
        assertFalse(manager.isUserLoggedIn())
    }
}
