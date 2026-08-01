package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.UserResponse
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
class UserRemoteDataSourceTest {

    private lateinit var api: TastyBudsApiService
    private lateinit var dataSource: UserRemoteDataSource

    @Before
    fun setUp() {
        api = mockk()
        dataSource = UserRemoteDataSource(api)
    }

    @Test
    fun `getUserByEmail returns the user whose email matches (case-insensitive)`() = runTest {
        coEvery { api.getUser(email = "a@b.com") } returns Response.success(
            listOf(UserResponse(id = "u1", name = "A", email = "A@B.com"))
        )

        val result = dataSource.getUserByEmail("a@b.com")

        assertTrue(result is Result.Success)
        assertEquals("u1", (result as Result.Success).data.id)
    }

    @Test
    fun `getUserByEmail returns Error when no user matches the email`() = runTest {
        coEvery { api.getUser(email = "a@b.com") } returns Response.success(
            listOf(UserResponse(id = "u1", email = "other@x.com"))
        )

        val result = dataSource.getUserByEmail("a@b.com")

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getUserByEmail returns Error when the API call is unsuccessful`() = runTest {
        coEvery { api.getUser(email = "a@b.com") } returns
            Response.error(500, "err".toResponseBody("text/plain".toMediaTypeOrNull()))

        val result = dataSource.getUserByEmail("a@b.com")

        assertTrue(result is Result.Error)
    }
}
