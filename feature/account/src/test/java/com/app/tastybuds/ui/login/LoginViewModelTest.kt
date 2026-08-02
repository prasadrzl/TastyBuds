package com.app.tastybuds.ui.login

import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { authRepository.isLoggedIn() } returns flowOf(false)
    }

    @Test
    fun `login rejects blank credentials without hitting the repository`() = runTest {
        val viewModel = LoginViewModel(authRepository)

        viewModel.login("", "")

        assertEquals("Please fill all fields", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `successful login marks the user logged in`() = runTest {
        val user = mockk<User>()
        coEvery { authRepository.login("a@b.com", "pw") } returns Result.Success(user)

        val viewModel = LoginViewModel(authRepository)
        viewModel.login("a@b.com", "pw")

        val state = viewModel.uiState.value
        assertTrue(state.isLoggedIn)
        assertFalse(state.isLoading)
        assertEquals(user, state.user)
    }

    @Test
    fun `failed login surfaces the error message`() = runTest {
        coEvery { authRepository.login("a@b.com", "bad") } returns Result.Error("Invalid credentials")

        val viewModel = LoginViewModel(authRepository)
        viewModel.login("a@b.com", "bad")

        val state = viewModel.uiState.value
        assertFalse(state.isLoggedIn)
        assertNotNull(state.errorMessage)
    }
}
