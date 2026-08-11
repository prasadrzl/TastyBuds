package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.LoginResponse
import com.app.tastybuds.data.model.UserResponse
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private lateinit var local: UserLocalDataSource
    private lateinit var remote: UserRemoteDataSource
    private lateinit var apiService: TastyBudsApiService
    private lateinit var tokenStore: AuthTokenStore
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        local = mockk(relaxed = true)
        remote = mockk()
        apiService = mockk()
        tokenStore = mockk(relaxed = true)
        repository = AuthRepositoryImpl(local, remote, apiService, tokenStore)
    }

    @Test
    fun `login stores the token and login state on success`() = runTest {
        val response = LoginResponse(
            token = "jwt-123",
            user = UserResponse(id = "u1", name = "Alice", email = "a@b.com")
        )
        coEvery { apiService.login(any()) } returns Response.success(response)

        val result = repository.login("a@b.com", "pw")

        assertTrue(result is Result.Success)
        coVerify { tokenStore.setToken("jwt-123") }
        coVerify { local.saveLoginState(any()) }
    }

    @Test
    fun `login maps a 401 to invalid credentials and stores no token`() = runTest {
        coEvery { apiService.login(any()) } returns
                Response.error(401, "unauthorized".toResponseBody(null))

        val result = repository.login("a@b.com", "wrong")

        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { tokenStore.setToken(any()) }
        coVerify(exactly = 0) { local.saveLoginState(any()) }
    }

    @Test
    fun `login maps a thrown exception to an error`() = runTest {
        coEvery { apiService.login(any()) } throws RuntimeException("offline")

        assertTrue(repository.login("a@b.com", "pw") is Result.Error)
    }

    @Test
    fun `logout clears the token and the local login state`() = runTest {
        repository.logout()

        coVerify { tokenStore.clear() }
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
