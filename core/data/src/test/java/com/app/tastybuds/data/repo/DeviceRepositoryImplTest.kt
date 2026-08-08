package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.DeviceRegistrationRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class DeviceRepositoryImplTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: DeviceRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = DeviceRepositoryImpl(apiService)
    }

    @Test
    fun `registerToken posts the user id and token`() = runTest {
        val captured = slot<DeviceRegistrationRequest>()
        coEvery { apiService.registerDevice(capture(captured)) } returns Response.success(Unit)

        repository.registerToken("u1", "fcm-token-123")

        coVerify { apiService.registerDevice(any()) }
        assertEquals("u1", captured.captured.userId)
        assertEquals("fcm-token-123", captured.captured.token)
        assertEquals("ANDROID", captured.captured.platform)
    }

    @Test
    fun `registerToken swallows a network failure without throwing`() = runTest {
        coEvery { apiService.registerDevice(any()) } throws RuntimeException("offline")

        repository.registerToken("u1", "tok")  // must not throw

        coVerify { apiService.registerDevice(any()) }
    }
}
