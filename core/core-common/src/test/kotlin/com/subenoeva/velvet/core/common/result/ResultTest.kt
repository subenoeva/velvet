package com.subenoeva.velvet.core.common.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResultTest {

    // getOrNull

    @Test
    fun `getOrNull returns data when Success`() {
        val result: Result<Int> = Result.Success(42)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `getOrNull returns null when Error`() {
        val result: Result<Int> = Result.Error(RuntimeException("fail"))
        assertNull(result.getOrNull())
    }

    @Test
    fun `getOrNull returns null when Loading`() {
        val result: Result<Int> = Result.Loading
        assertNull(result.getOrNull())
    }

    // map

    @Test
    fun `map transforms Success data`() {
        val result: Result<Int> = Result.Success(10)
        val mapped = result.map { it * 2 }
        assertEquals(Result.Success(20), mapped)
    }

    @Test
    fun `map on Error returns same Error`() {
        val exception = RuntimeException("error")
        val result: Result<Int> = Result.Error(exception, "msg")
        val mapped = result.map { it * 2 }
        assertEquals(Result.Error(exception, "msg"), mapped)
    }

    @Test
    fun `map on Loading returns Loading`() {
        val result: Result<Int> = Result.Loading
        val mapped = result.map { it * 2 }
        assertEquals(Result.Loading, mapped)
    }
}
