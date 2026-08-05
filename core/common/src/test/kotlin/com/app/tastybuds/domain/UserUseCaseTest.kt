package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.UpdateProfileRequest
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.repository.UserRepository
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserUseCaseTest {

    @MockK
    private lateinit var repository: UserRepository

    private lateinit var useCase: UserUseCase

    private val userId = "user_1"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UserUseCase(repository)
    }

    @Test
    fun `updateUser succeeds and calls the repository for a valid request`() = runTest {
        val request = UpdateProfileRequest(name = "New Name", email = "new@example.com")
        coEvery { repository.updateUser(userId, request) } returns Result.Success(mockk<User>())

        val result = useCase.updateUser(userId, request)

        assertTrue(result is Result.Success)
        coVerify { repository.updateUser(userId, request) }
    }

    @Test
    fun `updateUser rejects an invalid email without hitting the repository`() = runTest {
        val request = UpdateProfileRequest(email = "not-an-email")

        val result = useCase.updateUser(userId, request)

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { repository.updateUser(any(), any()) }
    }

    @Test
    fun `updateUser rejects a blank name without hitting the repository`() = runTest {
        val request = UpdateProfileRequest(name = "")

        val result = useCase.updateUser(userId, request)

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { repository.updateUser(any(), any()) }
    }

    @Test
    fun `updateUser rejects an empty request with no fields to change`() = runTest {
        val result = useCase.updateUser(userId, UpdateProfileRequest())

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { repository.updateUser(any(), any()) }
    }

    @Test
    fun `updateUser propagates a repository error for a valid request`() = runTest {
        val request = UpdateProfileRequest(name = "Valid")
        coEvery { repository.updateUser(userId, request) } returns Result.Error("update failed")

        val result = useCase.updateUser(userId, request)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `updateUser accepts a profile-image-only change`() = runTest {
        val request = UpdateProfileRequest(profileUrl = "http://img/new.png")
        coEvery { repository.updateUser(userId, request) } returns Result.Success(mockk<User>())

        assertTrue(useCase.updateUser(userId, request) is Result.Success)
        coVerify { repository.updateUser(userId, request) }
    }

    @Test
    fun `updateUser accepts a blank email that carries no other change guard`() = runTest {
        // email present but blank -> validation is skipped, request is considered valid
        val request = UpdateProfileRequest(email = "")
        coEvery { repository.updateUser(userId, request) } returns Result.Success(mockk<User>())

        assertTrue(useCase.updateUser(userId, request) is Result.Success)
    }

    @Test
    fun `updateUser accepts a variety of valid email formats`() = runTest {
        val request = UpdateProfileRequest(email = "first.last+tag@sub.domain.co")
        coEvery { repository.updateUser(userId, request) } returns Result.Success(mockk<User>())

        assertTrue(useCase.updateUser(userId, request) is Result.Success)
    }

    @Test
    fun `getUser delegates to the repository`() = runTest {
        coEvery { repository.getUser(userId) } returns Result.Success(mockk<User>())

        val result = useCase.getUser(userId)

        assertTrue(result is Result.Success)
        coVerify { repository.getUser(userId) }
    }
}
