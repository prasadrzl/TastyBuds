package com.app.tastybuds.ui.onboarding

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OnboardingViewModelTest {

    @MockK(relaxed = true)
    private lateinit var onboardingManager: OnboardingManager

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = OnboardingViewModel(onboardingManager)
    }

    @Test
    fun `isOnboardingCompleted delegates to the manager`() {
        every { onboardingManager.isOnboardingCompleted() } returns true
        assertTrue(viewModel.isOnboardingCompleted())
    }

    @Test
    fun `isUserLoggedIn delegates to the manager`() {
        every { onboardingManager.isUserLoggedIn() } returns false
        assertFalse(viewModel.isUserLoggedIn())
    }

    @Test
    fun `markOnboardingCompleted forwards to the manager`() {
        viewModel.markOnboardingCompleted()
        verify { onboardingManager.markOnboardingCompleted() }
    }
}
