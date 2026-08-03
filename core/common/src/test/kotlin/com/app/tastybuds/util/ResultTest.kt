package com.app.tastybuds.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest {

    @Test
    fun `onSuccess runs for Success and skips onError`() {
        var seen: String? = null
        var errorCalled = false

        val result: Result<String> = Result.Success("hello")
        result.onSuccess { seen = it }.onError { errorCalled = true }

        assertEquals("hello", seen)
        assertFalse(errorCalled)
    }

    @Test
    fun `onError runs for Error and skips onSuccess`() {
        var message: String? = null
        var successCalled = false

        val result: Result<String> = Result.Error("boom")
        result.onSuccess { successCalled = true }.onError { message = it }

        assertEquals("boom", message)
        assertFalse(successCalled)
    }

    @Test
    fun `neither branch runs for Loading`() {
        var touched = false
        val result: Result<String> = Result.Loading

        result.onSuccess { touched = true }.onError { touched = true }

        assertFalse(touched)
    }

    @Test
    fun `handlers return the same instance for chaining`() {
        val result: Result<String> = Result.Success("x")
        assertSame(result, result.onSuccess { }.onError { })
    }

    @Test
    fun `Success carries its data`() {
        val result = Result.Success(42)
        assertTrue(result.data == 42)
    }
}
