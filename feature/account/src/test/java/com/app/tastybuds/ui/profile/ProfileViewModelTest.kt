package com.app.tastybuds.ui.profile

import com.app.tastybuds.domain.UserUseCase
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var userUseCase: UserUseCase

    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = ProfileViewModel(userUseCase, authRepository)
    }

    private val user = User(
        id = "u1", name = "Alice", email = "alice@example.com",
        profileUrl = "http://img", currentLocation = null, createdAt = ""
    )

    @Test
    fun `initialize loads the profile and seeds the form state`() = runTest {
        coEvery { userUseCase.getUser("u1") } returns Result.Success(user)

        viewModel.initialize("u1")

        assertEquals(user, viewModel.uiState.value.user)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Alice", viewModel.formState.value.name)
        assertEquals("alice@example.com", viewModel.formState.value.email)
    }

    @Test
    fun `initialize surfaces a load error`() = runTest {
        coEvery { userUseCase.getUser("u1") } returns Result.Error("boom")

        viewModel.initialize("u1")

        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `UpdateName event updates the form state`() = runTest {
        viewModel.handleEvent(ProfileEvent.UpdateName("Bob"))

        assertEquals("Bob", viewModel.formState.value.name)
    }

    @Test
    fun `saving a blank profile reports validation errors and never calls the repository`() = runTest {
        viewModel.handleEvent(ProfileEvent.SaveProfile)

        assertTrue(viewModel.uiState.value.validationErrors.hasErrors())
        coVerify(exactly = 0) { userUseCase.updateUser(any(), any()) }
    }

    @Test
    fun `logout sets loggedOut and can be acknowledged`() = runTest {
        coEvery { authRepository.logout() } returns Unit

        viewModel.logout()
        assertTrue(viewModel.uiState.value.loggedOut)

        viewModel.onLogoutHandled()
        assertFalse(viewModel.uiState.value.loggedOut)
    }

    @Test
    fun `getUserId delegates to the auth repository`() = runTest {
        every { authRepository.getUserId() } returns flowOf("u1")

        assertEquals("u1", viewModel.getUserId().first())
    }

    @Test
    fun `StartEditing enters edit mode`() {
        viewModel.handleEvent(ProfileEvent.StartEditing)
        assertTrue(viewModel.uiState.value.isEditing)
    }

    @Test
    fun `CancelEditing leaves edit mode`() {
        viewModel.handleEvent(ProfileEvent.StartEditing)
        viewModel.handleEvent(ProfileEvent.CancelEditing)
        assertFalse(viewModel.uiState.value.isEditing)
    }

    @Test
    fun `UpdateEmail and UpdateProfileImage update the form state`() {
        viewModel.handleEvent(ProfileEvent.UpdateEmail("new@example.com"))
        viewModel.handleEvent(ProfileEvent.UpdateProfileImage("http://new/img"))

        assertEquals("new@example.com", viewModel.formState.value.email)
        assertEquals("http://new/img", viewModel.formState.value.profileUrl)
    }

    @Test
    fun `DismissError and DismissSuccess clear their flags`() = runTest {
        coEvery { userUseCase.getUser("u1") } returns Result.Error("boom")
        viewModel.initialize("u1")

        viewModel.handleEvent(ProfileEvent.DismissError)
        assertEquals(null, viewModel.uiState.value.error)

        viewModel.handleEvent(ProfileEvent.DismissSuccess)
        assertFalse(viewModel.uiState.value.updateSuccess)
    }
}
