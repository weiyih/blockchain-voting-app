package com.kevinwei.vote

import org.junit.Assert.*
import org.junit.Test

class UtilsTest {

    @Test
    fun convertDateToLocalFormatted_ReturnsString() {
        val testString = "2021-03-01T00:00:00.000Z-500"
        val expectedString = "Mar. 1, 2021, 12:00:00 a.m."


        val result = convertDateToLocalFormatted(testString)

        assertEquals(result, expectedString)
    }
}