package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.model.User
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private lateinit var local: UserLocalDataSource
    private lateinit var remote: UserRemoteDataSource
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        local = mockk(relaxed = true)
        remote = mockk()
        repository = AuthRepositoryImpl(local, remote)
    }

    @Test
    fun `login fails when the password is invalid and never hits the network`() = runTest {
        every { local.validatePassword("a@b.com", "wrong") } returns false

        val result = repository.login("a@b.com", "wrong")

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { remote.getUserByEmail(any()) }
    }

    @Test
    fun `login succeeds and saves login state when password is valid and user is found`() = runTest {
        val user = mockk<User>()
        every { local.validatePassword("a@b.com", "pw") } returns true
        coEvery { remote.getUserByEmail("a@b.com") } returns Result.Success(user)

        val result = repository.login("a@b.com", "pw")

        assertTrue(result is Result.Success)
        coVerify { local.saveLoginState(user) }
    }

    @Test
    fun `login fails when the remote user lookup fails`() = runTest {
        every { local.validatePassword("a@b.com", "pw") } returns true
        coEvery { remote.getUserByEmail("a@b.com") } returns Result.Error("not found")

        val result = repository.login("a@b.com", "pw")

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { local.saveLoginState(any()) }
    }

    @Test
    fun `logout clears the local login state`() = runTest {
        repository.logout()
        coVerify { local.clearLoginState() }
    }

    @Test
    fun `isLoggedIn and getUserId delegate to the local data source`() = runTest {
        every { local.isLoggedIn() } returns flowOf(true)
        every { local.getUserId() } returns flowOf("u1")

        assertEquals(true, repository.isLoggedIn().first())
        assertEquals("u1", repository.getUserId().first())
    }
}
