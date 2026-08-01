package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.domain.repository.VouchersRepository
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VouchersUseCaseTest {

    @MockK
    private lateinit var repository: VouchersRepository

    private lateinit var useCase: VouchersUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = VouchersUseCase(repository)
    }

    private fun voucher(
        id: String, value: Double = 0.0, canBeUsed: Boolean = true,
        isExpired: Boolean = false, expiryDate: String = "", createdAt: String = ""
    ) = Voucher(
        id = id, value = value, canBeUsed = canBeUsed, isExpired = isExpired,
        expiryDate = expiryDate, createdAt = createdAt
    )

    @Test
    fun `getActiveVouchers filters out unusable and expired vouchers`() = runTest {
        coEvery { repository.getActiveVouchers("u1") } returns Result.Success(
            listOf(
                voucher("keep", canBeUsed = true, isExpired = false),
                voucher("expired", canBeUsed = true, isExpired = true),
                voucher("unusable", canBeUsed = false, isExpired = false)
            )
        )

        val result = useCase.getActiveVouchers("u1")

        val ids = (result as Result.Success).data.map { it.id }
        assertEquals(listOf("keep"), ids)
    }

    @Test
    fun `getActiveVouchers sorts usable vouchers by value descending`() = runTest {
        coEvery { repository.getActiveVouchers("u1") } returns Result.Success(
            listOf(voucher("low", value = 2.0), voucher("high", value = 10.0), voucher("mid", value = 5.0))
        )

        val result = useCase.getActiveVouchers("u1")

        assertEquals(listOf("high", "mid", "low"), (result as Result.Success).data.map { it.id })
    }

    @Test
    fun `getActiveVouchers propagates a repository error`() = runTest {
        coEvery { repository.getActiveVouchers("u1") } returns Result.Error("boom")

        val result = useCase.getActiveVouchers("u1")

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getUsedVouchers sorts by createdAt descending`() = runTest {
        coEvery { repository.getUsedVouchers("u1") } returns Result.Success(
            listOf(
                voucher("old", createdAt = "2024-01-01"),
                voucher("new", createdAt = "2024-03-01"),
                voucher("mid", createdAt = "2024-02-01")
            )
        )

        val result = useCase.getUsedVouchers("u1")

        assertEquals(listOf("new", "mid", "old"), (result as Result.Success).data.map { it.id })
    }
}
