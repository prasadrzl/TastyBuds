package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.UserResponse
import com.app.tastybuds.domain.model.UpdateProfileRequest
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: UserRepositoryImpl

    private val userId = "user_1"

    @Before
    fun setUp() {
        apiService = mockk()
        repository = UserRepositoryImpl(apiService)
    }

    private fun <T> errorResponse(code: Int = 500): Response<T> =
        Response.error(code, "error".toResponseBody("text/plain".toMediaTypeOrNull()))

    @Test
    fun `getUser maps the first user in the response to a domain model`() = runTest {
        coEvery { apiService.getUser(userId) } returns
            Response.success(listOf(UserResponse(id = userId, name = "John", email = "john@x.com")))

        val result = repository.getUser(userId)

        assertTrue(result is Result.Success)
        val user = (result as Result.Success).data
        assertEquals(userId, user.id)
        assertEquals("John", user.name)
        assertEquals("john@x.com", user.email)
    }

    @Test
    fun `getUser returns Error when the response body is empty`() = runTest {
        coEvery { apiService.getUser(userId) } returns Response.success(emptyList())

        val result = repository.getUser(userId)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getUser returns Error when the API call is unsuccessful`() = runTest {
        coEvery { apiService.getUser(userId) } returns errorResponse()

        val result = repository.getUser(userId)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getUser returns Error on a network exception`() = runTest {
        coEvery { apiService.getUser(userId) } throws RuntimeException("no network")

        val result = repository.getUser(userId)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `updateUser returns the updated user on success`() = runTest {
        val request = UpdateProfileRequest(name = "New Name")
        coEvery { apiService.updateUser(userId, request) } returns
            Response.success(listOf(UserResponse(id = userId, name = "New Name", email = "john@x.com")))

        val result = repository.updateUser(userId, request)

        assertTrue(result is Result.Success)
        assertEquals("New Name", (result as Result.Success).data.name)
    }

    @Test
    fun `updateUser returns Error when the response body is empty`() = runTest {
        val request = UpdateProfileRequest(name = "New Name")
        coEvery { apiService.updateUser(userId, request) } returns Response.success(emptyList())

        val result = repository.updateUser(userId, request)

        assertTrue(result is Result.Error)
    }
}
